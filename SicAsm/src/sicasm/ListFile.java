package sicasm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import sicasm.Constants.Errors;

public final class ListFile {
    
    // Memory constants in bytes.
    private static int kMemorySize = 0x8000;
    private static int kWordSize = 0x7FFFFF;
    
    private final ArrayList<SourceLine> sourceLines;
    private int locationCounter,
                startAddress,
                programLength;
    private SymbolTable symTable;
    private ArrayList<Literal> literalTable;
    private Stack<Integer> originLocations;
    private Boolean errorsExist;
    private final String fileDir;
    
    private ExpressionManager expManager;
        
    public ListFile(String sourceFileName, Boolean gerenateListFile) 
            throws Exception {
        SourceLine.resetMaxLength();
        SourceFile sourceFile = new SourceFile(sourceFileName);
        fileDir = new File(sourceFileName).getParent();
        sourceLines = sourceFile.getTokenz();
        if (sourceLines.isEmpty()) {
            throw new Exception("File is empty!");
        }
        
        symTable = new SymbolTable();
        literalTable = new ArrayList<>();
        errorsExist = false;
        originLocations = new Stack<>();
        expManager = ExpressionManager.getExpressionManager(symTable);
        
        passOne();
        passTwo();
        if (gerenateListFile) {
            export();            
        }
    }

    public ArrayList<SourceLine> getSourceLines() {
        return sourceLines;
    }
    
    private Boolean isHexInteger(String s) {
        s = s.toUpperCase();
        for (int i = 0; i < s.length(); i++) {
            if (!((s.charAt(i) >= '0' && s.charAt(i) <= '9') || 
                  (s.charAt(i) >= 'A' && s.charAt(i) <= 'F'))) {
                return false;
            }
        }
        return true;
    }    
    
    private boolean isLiteral(String operand) {
        return operand.startsWith("=");
    }
    
    private void fetchAndAddLiteral(final SourceLine sourceLine) {
        String operand = sourceLine.getOperand().substring(1);
        String hexCode = "";
        int checkWordResult = checkWordOperand(operand);        
        
        if ((operand.startsWith("c") || operand.startsWith("c")) &&
                operand.endsWith("'")) {
            for (int i = 2; i < operand.length() - 1; i++) {
                hexCode += String.format("%02X", (int)operand.charAt(i)); 
            }
        } else if ((operand.startsWith("x") || operand.startsWith("X")) &&
                operand.endsWith("'")) {
            String hexNumber = operand.substring(2, operand.length() - 1);
            if (isHexInteger(hexNumber)) {     
                int hexLength = operand.length() - 3;
                if (hexLength % 2 == 0) {
                    hexCode = hexNumber.toUpperCase();
                } else {
                    sourceLine.addError(Errors.INVALID_HEX_REPRESENTATION);
                    return;
                }
            } else {
                sourceLine.addError(Errors.INVALID_HEX);
                return;
            }
        } else if (checkWordResult == 7) {
            hexCode = getWordHexCode(operand);
        } else if (checkWordResult == 1) {
            sourceLine.addError(Errors.WORD_OPERAND_OUT_OF_RANGE);    
            return;
        } else {
            sourceLine.addError(Errors.INVALID_LITERAL);
            return;
        }
        literalTable.add(new Literal("=" + operand, hexCode));
    }
    
    private int getLiteralHexCodeAddressLocation(String hexCode) {
        for (Literal literal: literalTable) {
            if (literal.isUsed() && literal.getHexCode().equals(hexCode)) {
                return literal.getAddressLocation();
            }
        }
        return -1;
    }
    
    private int getLiteralAddressLocation(String operand) {
        for (Literal literal: literalTable) {
            if (literal.getName().equals(operand)) {
                return literal.getAddressLocation();
            }
        }
        return -1;
    }
    
    private ArrayList<SourceLine> getUnLocatedLiterals() {
        ArrayList<SourceLine> literals = new ArrayList<>();
        for (Literal literal: literalTable) {
            if (!literal.isUsed()) {
                int locationAddress = getLiteralHexCodeAddressLocation(
                        literal.getHexCode());
                if (locationAddress != -1) {
                    literal.setAddressLocation(locationAddress);
                } else {
                    literal.setAddressLocation(locationCounter);
                    locationCounter += literal.getHexCode().length() / 2;
                    literal.setAsUsed();
                    literals.add(new SourceLine(literal));                    
                }
            }
        }
        if (!literals.isEmpty()) {
            literals.add(0, new SourceLine(
                    "........  Literal decleartion start. ........"));
            literals.add(new SourceLine(
                    "........  Literal decleartion end.  ........"));
        }
        return literals;
    }
    
    private String getWordHexCode(String operand) {
        String hexCode = String.format("%06X", Integer.parseInt(
                    operand));
        // Java integer is 4 bytes, SIC's is 3 bytes.
        // In case of negative number, the hex representation will 
        // be in 2's complement of 4 bytes, so only 3 bytes are 
        // taken from the right.
        return hexCode.substring(Math.max(0, hexCode.length() - 6),
                hexCode.length());     
    }
    
    private void passOne() throws Exception {
        String progName = "Prog";
        int lineNumber = -1,
            firstCodeLine = -1;
        boolean startFound = false,
                firstUnCommentedLineFound = false, 
                endFound = false;
        
        for(int i = 0; i < sourceLines.size(); ++i) {
            SourceLine sourceLine = sourceLines.get(i);
            if (!firstUnCommentedLineFound) ++firstCodeLine;
            if (sourceLine.isLineComment()) {
                continue;
            }
            if (endFound) {
                sourceLine.convertToLineComment();
                continue;
            }
            firstUnCommentedLineFound = true;
            ++lineNumber;
            sourceLine.setAddressLocation(locationCounter);
            if (locationCounter >= kMemorySize) {
                sourceLine.addError(Errors.ARITHMETIC_OVERFLOW);
            }
            String label = sourceLine.getLabel();
            String menomonic = sourceLine.getMnemonic().toUpperCase();
            String operand = sourceLine.getOperand();

            // Adding labels into symTable.
            if (!label.isEmpty()) {
                if (!isValidLabelRepresentation(label)) {
                    sourceLine.addError(Errors.INVALID_LABEL_REPRESENTATION);
                } else if (symTable.containsLabel(label)) {
                    sourceLine.addError(Errors.DUPLICATE_LABEL);
                } else {
                    if (locationCounter >= kMemorySize) {
                        symTable.add(label, kMemorySize + 1);
                    } else {
                        symTable.add(label, locationCounter);                        
                    }
                }
            }

            // Adding literals into literalTable.
            if (isLiteral(operand)) {
                fetchAndAddLiteral(sourceLine);
            }
            
            // Handeling Mnemonics and directives.
            if (menomonic.isEmpty()) {
                sourceLine.addError(Errors.MISSING_MNEMONIC);
            } else if (Constants.OpTable.containsKey(menomonic)) {
                // Menomonic 'RSUB' does not need operand. So this should be 
                // checked before checking if there is an oprand or not.
                locationCounter += 3;
            } else if (operand.isEmpty() && !menomonic.equals("LTORG") && 
                    !menomonic.equals("ORG") && !menomonic.equals("END")) {
                // Directives which do not need oprand.
                // Add error after more checks in pass two.
            } else if (menomonic.equals("START")) {
                if (lineNumber != 0) {
                    sourceLine.addError(Errors.DUPLICATE_START);
                }
                startFound = true;
                if (label.isEmpty()) {
                    sourceLine.addError(Errors.UNNAMED_PROGRAM);
                } else {
                    progName = label;                    
                }
                if (isHexInteger(operand)) {
                    if(isInRange(operand, 16, kMemorySize)) {
                        locationCounter = Integer.parseInt(operand, 16);
                        startAddress = locationCounter;
                    } else {
                        sourceLine.addError(Errors.INVALID_ADDRESS_LOCATION);
                    }
                } else {
                    locationCounter = 0;
                    sourceLine.addError(Errors.INVALID_START_ADDRESS);
                }                 
            } else if (menomonic.equals("END")) {
                endFound = true;
                ArrayList<SourceLine> addedLiterals = getUnLocatedLiterals();
                for(SourceLine literalSourceLine: addedLiterals) {
                    sourceLines.add(i, literalSourceLine);
                    i++;
                }
            } else if (menomonic.equals("WORD")) {
                locationCounter += 3;
            } else if (menomonic.equals("BYTE")) {
                if (operand.charAt(0) == 'c' || operand.charAt(0) == 'C') {
                    locationCounter += operand.length() - 3;
                } else if (operand.charAt(0) == 'x' || operand.charAt(0) == 'X') {
                    int hexLength = operand.length() - 3;
                    if (hexLength % 2 == 0) {
                        locationCounter += hexLength / 2;
                    } else {
                        // Error number of digits must be even. Added in pass 2.
                    }
                }
            } else if (menomonic.equals("RESW") || menomonic.equals("RESB")) {
                int multi;
                if (menomonic.equals("RESW")) {
                    multi = 3;
                } else {
                    multi = 1;
                }
                if (isValidReserveOperand(operand)) {
                    if (isInRange(operand, 10, kMemorySize)) {
                        int toBeReserved = Integer.parseInt(operand) * multi;
                        if (toBeReserved + locationCounter < kMemorySize) {
                            locationCounter += toBeReserved;
                        } else {
                            sourceLine.addError(Errors.INVALID_RESERVE_OPERAND);
                            locationCounter = kMemorySize + 1;
                        }
                    } else {
                        sourceLine.addError(Errors.INVALID_RESERVE_OPERAND);
                        locationCounter = kMemorySize + 1;
                    }               
                } else {
                    sourceLine.addError(Errors.INVALID_OPERAND);
                }
            } else if (menomonic.equals("LTORG")) {
                ArrayList<SourceLine> addedLiterals = getUnLocatedLiterals();
                for(SourceLine literalSourceLine: addedLiterals) {
                    i++;
                    sourceLines.add(i, literalSourceLine);
                }
            } else if (menomonic.equals("EQU")) {
                if (operand.equals("*")) {
                    symTable.add(label, locationCounter, -1);
                } else {                    
                    ExpressionResult result = expManager.evaluate(operand);
                    int value = result.getValue();
                    if (result.containsErrors()) {
                        for (Errors error: result.getErrors()) {
                            sourceLine.addError(error);                        
                        }
                    } else {                    
                        if (label.isEmpty()) {  
                            sourceLine.addError(Errors.MISSING_EQUATE_LABEL);
                        } else {
                            if (value >= 0 && value < kMemorySize) {
                                symTable.add(label, value, -1);
    //                            System.out.println(result.getValue());
                                sourceLine.setAddressLocation(value);
                            } else {
                                sourceLine.addError(
                                        Errors.EQAUTE_RESULT_OUT_OF_RANGE);
                            }
                        }
                    }
                }
            } else if (menomonic.equals("ORG")) {
                // TODO(ahmedatef): empty operand case will never happen in case
                // source line contains a comment.
                if (operand.isEmpty()) {
                    if (originLocations.isEmpty()) {
                        sourceLine.addError(Errors.EMPTY_ORIGIN_STACK);
                    } else {
                        locationCounter = originLocations.pop();                        
                    }
                } else {
                    if (symTable.containsLabel(operand)) {
                        originLocations.push(locationCounter);
                        locationCounter = symTable.getAddressLocation(operand);
                    } else {
                        sourceLine.addError(Errors.UNDEFINED_LABEL);
                    }
                }
            } else {
                sourceLine.addError(Errors.UNRECOGNIZED_MNEMONIC);
            }
        }
        if (!firstUnCommentedLineFound) {
            throw new Exception("No SIC commands found!");
        }
        if (!startFound) {
             sourceLines.get(firstCodeLine).addError(Errors.MISSING_START);
        }
        
        if (!endFound) {
            // Adding un located literals.
            ArrayList<SourceLine> addedLiterals = getUnLocatedLiterals();
            for(SourceLine literalSourceLine: addedLiterals) {
                sourceLines.add(literalSourceLine);
            }

            SourceLine endLine = new SourceLine("", "END", progName,
                     "Automatically added by elSic.");            
             sourceLines.add(endLine);
             endLine.setAddressLocation(locationCounter);
        }
    }
    
    private void passTwo() {
        for (SourceLine sourceLine : sourceLines) {
            if (sourceLine.isLineComment() || sourceLine.isLiteral()) {
                continue;
            }
            
            String objectCode = "";   
            String menomonic = sourceLine.getMnemonic().toUpperCase();
            String operand = sourceLine.getOperand();
            
            // Directives which do not need oprand are added as special cases.
            if (operand.isEmpty() && !menomonic.equals("RSUB") && 
                    !menomonic.equals("LTORG") && !menomonic.equals("END") && 
                    !menomonic.equals("ORG")) {
                sourceLine.addError(Errors.MISSING_OPERAND);
            }
            // Handeling Mnemonics and directives.
            if (Constants.OpTable.containsKey(menomonic)) {                  
                int hexCode = Constants.OpTable.get(menomonic) << 16; 
                if (menomonic.equals("RSUB")) {
                    // Does not need an operand.
                    objectCode = String.format("%06X", hexCode); 
                } else {                
                    boolean indexed = false;
                    if (operand.endsWith(",x") || operand.endsWith(",X")) {
                        indexed = true;
                        operand = operand.substring(0, operand.length() - 2);
                    }
                    if (symTable.containsLabel(operand)) {
                        int operandAddress = symTable.getAddressLocation(
                                operand);
                        if (indexed) operandAddress |= 1 << 15; 
                        hexCode |= operandAddress;                        
                        objectCode = String.format("%06X", hexCode);
                    } else if (isLiteral(operand)) {                       
                        int operandAddress = getLiteralAddressLocation(operand);
                        if (indexed) operandAddress |= 1 << 15; 
                        hexCode |= operandAddress;                        
                        objectCode = String.format("%06X", hexCode);
                    } else {
                        sourceLine.addError(Errors.UNDEFINED_LABEL);                          
                    }                        
                }                
            }  else if (sourceLine.getOperand().isEmpty()) {
                // Error would be already added. This just to make sure coming 
                // conditions will be only satisfied in case there is an oprand.
            } else if (menomonic.equals("START")) {
                // Error was already added in pass one in case of duplicates.
            } else if (menomonic.equals("END")) {
            } else if (menomonic.equals("WORD")) {
                int checkResult = checkWordOperand(operand);
                if (checkResult == 7) {
                    objectCode = getWordHexCode(operand);
                } else if (checkResult == 1) {
                    sourceLine.addError(Errors.WORD_OPERAND_OUT_OF_RANGE);                        
                } else {
                    sourceLine.addError(Errors.INVALID_WORD_OPERAND);
                }              
            } else if (menomonic.equals("BYTE")) {
                // Check there are no flag for unclosed quote.
                if (isValidByteOperand(operand)) {                    
                    if (operand.charAt(0) == 'c' || operand.charAt(0) == 'C') {
                        for (int i = 2; i < operand.length() - 1; i++) {
                            objectCode += String.format(
                                    "%02X", (int)operand.charAt(i));                             
                        }
                    } else if (operand.charAt(0) == 'x' || 
                               operand.charAt(0) == 'X') {
                        String hexNumber = operand.substring(
                                2, operand.length() - 1);
                        if (isHexInteger(hexNumber)) {     
                            int hexLength = operand.length() - 3;
                            if (hexLength % 2 == 0) {
                                objectCode = hexNumber.toUpperCase();
                            } else {
                                sourceLine.addError(
                                        Errors.INVALID_HEX_REPRESENTATION);
                            }
                        } else {
                            sourceLine.addError(Errors.INVALID_HEX);
                        }
                    }
                } else {
                    // Invalid Byte operand.
                    sourceLine.addError(Errors.INVALID_BYTE_OPERAND);
                }
            } else if (menomonic.equals("RESW")) {
            } else if (menomonic.equals("RESB")) {
            } else if (menomonic.equals("EQU")) {
            } else {
                // Error undefined mneomonic. Error added in pass one.
            }
            sourceLine.setObjectCode(objectCode);            
            errorsExist |= sourceLine.containsErrors();
        }
        programLength = locationCounter - startAddress;
    }

    private boolean isValidLabelRepresentation(String label) {
        label = label.toUpperCase();
        if(!((label.charAt(0) >= 'A' && label.charAt(0) <= 'Z') || 
             label.charAt(0) == '_')) {
            return false;
        }
        for(int i = 1; i < label.length(); ++i) {
            if(!((label.charAt(i) >= 'A' && label.charAt(i) <= 'Z') || 
                 (label.charAt(i) >= '0' && label.charAt(i) <= '9') || 
                  label.charAt(0) == '_')) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isValidReserveOperand(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!(s.charAt(i) >= '0' && s.charAt(i) <= '9')) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isValidByteOperand(String operand) {
        if (!(operand.startsWith("c'") || operand.startsWith("x'") ||
              operand.startsWith("C'") || operand.startsWith("X'"))) {
            return false;
        }
        if (!operand.endsWith("'")) {
            return false;
        }
        return true;
    }
    
    /**
     * @return 0: Not a valid word operand.
     * 1: Out of range number (Max 0x7FFFFF).
     * 7: Valid word operand.
     */  
    private int checkWordOperand(String operand) {
        for (int i = 0; i < operand.length(); i++) {
            if (i == 0 && operand.charAt(0) == '-') continue;
            if (!(operand.charAt(i) >= '0' && operand.charAt(i) <= '9')){
                return 0;
            }
        }        
        if (!isInRange(operand, 10, kWordSize)) {
            return 1;
        }
        return 7;
    }
    
    private boolean isInRange(String number, int numberRadix, int range) {
        BigInteger big = new BigInteger(number, numberRadix);
        big = big.abs();
        BigInteger maxInt = BigInteger.valueOf(range);
        return big.compareTo(maxInt) != 1;
    }
    
    private void export() throws FileNotFoundException {
        StringBuilder lines = new StringBuilder("ElSic Assembler 1.0\n");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        lines.append("Generated: ");
        lines.append(dateFormat.format(new Date()));
        lines.append("\n\n");
        
        for (SourceLine sourceLine : sourceLines) {
            lines.append(sourceLine.getListFileLine());
        }
        
        PrintWriter pw = new PrintWriter(new File(fileDir, "LISTFILE"));
        pw.print(lines);
        pw.flush();
        pw.close();
    }

    public Boolean getErrorsExist() {
        return errorsExist;
    }    

    public int getStartAddress() {
        return startAddress;
    }

    public int getProgramLength() {
        return programLength;
    }    
}
