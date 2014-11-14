package sicasm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

public final class ListFile {
    private ArrayList<SourceLine> sourceLines;
    private int locationCounter,
                startAddress,
                programLength;
    private TreeMap<String, Integer> symTable;
    private Boolean errorsExist;
            
    public ListFile(String sourceFileName, Boolean gerenateListFile) 
            throws IOException {
        SourceLine.resetMaxLength();
        SourceFile sourceFile = new SourceFile(sourceFileName);
        sourceLines = sourceFile.getTokenz();
        symTable = new TreeMap<>();
        errorsExist = false;
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
    
    private void passOne() {
        int lineNumber = -1;
        Boolean startExist = false;
        for (SourceLine sourceLine : sourceLines) {
            ++lineNumber;
            if (sourceLine.getIsLineComment()) {
                continue;
            }
            sourceLine.setAddressLocation(locationCounter);
            // Adding labels into symTable.
            if (!sourceLine.getLabel().isEmpty()) {
                if (symTable.containsKey(sourceLine.getLabel())) {
                } else {
                    symTable.put(sourceLine.getLabel(), locationCounter);                    
                }
            }         
            String menomonic = sourceLine.getMnemonic().toUpperCase();
            // Handeling Mnemonics and directives.
            if (Constants.OpTable.containsKey(menomonic)) {
                locationCounter += 3;
            } else if (menomonic.equals("START")) {
                if (lineNumber != 0) {
                    // Error misplaces start.
                    sourceLine.addError(Constants.Errors.DUPLICATE_START);
                    continue;
                }
                startExist = true;
                String operand = sourceLine.getOperand();
                if (isHexInteger(operand)) {
                    locationCounter = Integer.parseInt(operand, 16);
                    startAddress = locationCounter;
                    if (locationCounter >= 0x8000) {
                        // Out of memeory 
                    }
                } else {
                    locationCounter = 0;
                    sourceLine.addError(Constants.Errors.INVALID_START_ADDRESS);
                }                 
            } else if (menomonic.equals("END")) {
                break;
            } else if (menomonic.equals("WORD")) {
                locationCounter += 3;
            } else if (menomonic.equals("BYTE")) {
                String op = sourceLine.getOperand();
                if (op.charAt(0) == 'c' || op.charAt(0) == 'C') {
                    locationCounter += op.length() - 3;
                } else if (op.charAt(0) == 'x' || op.charAt(0) == 'X') {
                    int hexLength = op.length() - 3;
                    if (hexLength % 2 == 0) {
                        locationCounter += hexLength / 2;
                    } else {
                        // Error number of digits must be even.
                    }
                }
            } else if (menomonic.equals("RESW")) {
                locationCounter += Integer.parseInt(
                        sourceLine.getOperand()) * 3;
            } else if (menomonic.equals("RESB")) {
                locationCounter += Integer.parseInt(sourceLine.getOperand());
            } else {
                sourceLine.addError(Constants.Errors.UNRECOGNIZED_MNEMONIC);
            }
        }
        if (!startExist) {
             sourceLines.get(0).addError(Constants.Errors.MISSING_START);
        }
    }
    
    private void passTwo() {
        for (SourceLine sourceLine : sourceLines) {
            if (sourceLine.getIsLineComment()) {
                continue;
            }                    
            
            String objectCode = "";   
            String menomonic = sourceLine.getMnemonic().toUpperCase();
            String operand = sourceLine.getOperand();
          
            
            if (operand.isEmpty() && !menomonic.equals("RSUB")) {
                sourceLine.addError(Constants.Errors.MISSING_OPERAND);
            }
            // Handeling Mnemonics and directives.
            if (Constants.OpTable.containsKey(menomonic)) {
                int hexCode = Constants.OpTable.get(menomonic) << 16;                 
                if (operand.endsWith(",x") || operand.endsWith(",X")) {
                    if (symTable.containsKey(operand.substring(0,
                            operand.length() - 2))) {
                        int operandNum = symTable.get(operand.substring(0, 
                                operand.length() - 2));
                        operandNum |= 1 << 15;      
                        hexCode |= operandNum;                        
                        objectCode = String.format("%06X", hexCode);                 
                    } else {
                        // Undefined label. Show which label is not defined.
                        sourceLine.addError(Constants.Errors.UNDEFINED_LABEL);
                    }                  
                } else if (menomonic.equals("RSUB")) {
                    // Does not need an operand.
                    objectCode = String.format("%06X", hexCode); 
                } else if (symTable.containsKey(operand)) {
                    int operandNum = symTable.get(operand);
                    hexCode |= operandNum;
                    objectCode = String.format("%06X", hexCode);          
                } else {
                    System.out.println(operand + " " + 
                            symTable.get(operand));
                    objectCode = "";
                    // Undefined label. Show which label is not defined.
                    sourceLine.addError(Constants.Errors.UNDEFINED_LABEL);
                }
            } else if (menomonic.equals("START")) {
                // Error was already set in pass one in case of duplicates.
            } else if (menomonic.equals("END")) {
                // Terminate proccessing. 
                sourceLine.setObjectCode(objectCode);
                break;
            } else if (menomonic.equals("WORD")) {
                if (isValidWordOperand(operand) == 7) {
                    objectCode = String.format(
                            "%06X", Integer.parseInt(operand));           
                } else if (isValidWordOperand(operand) == 1) {
                    // Number out of range  
                    sourceLine.addError(
                            Constants.Errors.WORD_OPERAND_OUT_OF_RANGE);                        
                } else {
                    // Invalid word operand.
                    sourceLine.addError(Constants.Errors.INVALID_WORD_OPERAND);
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
                                sourceLine.addError(Constants.Errors.
                                        INVALID_HEX_REPRESENTATION);
                            }
                        } else {
                            // Not valid Hex.
                            sourceLine.addError(Constants.Errors.INVALID_HEX);
                        }
                    }
                } else {
                    // Invalid Byte operand.
                    sourceLine.addError(Constants.Errors.INVALID_BYTE_OPERAND);
                }
            } else if (menomonic.equals("RESW")) {
            } else if (menomonic.equals("RESB")) {
            } else {
                // Error undefined mneomonic.
                System.out.println("Error");
            }
            sourceLine.setObjectCode(objectCode);            
            errorsExist |= sourceLine.lineContainsErrors();
        }
        programLength = locationCounter - startAddress;
    }
    
    private Boolean isValidByteOperand(String operand) {
        if (!(operand.startsWith("c'") || operand.startsWith("x'") ||
              operand.startsWith("C'") || operand.startsWith("X'"))) {
            return false;
        }
        if (!operand.endsWith("'")) {
            return false;
        }
        return true;
    }
    
    private int isValidWordOperand(String operand) {
        for (int i = 0; i < operand.length(); i++) {
            if (!(operand.charAt(i) >= '0' && operand.charAt(i) <= '9')){
                return 0;
            }
        }
        BigInteger big = new BigInteger(operand);
        BigInteger maxInt = new BigInteger(0xFFFFFF + "");
        if (big.compareTo(maxInt) == 1) {
            return 1;
        }
        return 7;
    }
    
    public void export() throws FileNotFoundException {
        String lines = "ElSic Assembler 1.0\n";
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        lines += "Generated: " + dateFormat.format(new Date()) + "\n\n";
        
        for (SourceLine sourceLine : sourceLines) {
            if (sourceLine.getIsLineComment()) {
                lines += "            " + sourceLine.getComment() + "\n";
                continue;
            }            
            int it = 0;
            ArrayList<Constants.Errors> errorsList = sourceLine.getErrorsList();
            String objectCode = sourceLine.getObjectCode();
            String subObjectCode;
            if (errorsList == null || (errorsList.size() == 1 && 
                    errorsList.get(0).equals(Constants.Errors.MISSING_START))) {
                subObjectCode = objectCode.substring(
                        it, it = Math.min(it + 6, objectCode.length()));
            } else {
                subObjectCode = "";
            }
            
            lines += String.format(
                    "%04X %-6s %-" + SourceLine.getLabelMaxLength() + "s  %-" + 
                    SourceLine.getMnemonicMaxLength()+ "s  %-" + 
                    SourceLine.getOperandMaxLength()+ "s   %s\n",
                    sourceLine.getAddressLocation(),    
                    subObjectCode,
                    sourceLine.getLabel(), 
                    sourceLine.getMnemonic(), 
                    sourceLine.getOperand(), 
                    sourceLine.getComment()); 
            
            while(it < objectCode.length()) {
                 subObjectCode = objectCode.substring(
                         it, it = Math.min(it + 6, objectCode.length()));   
                 lines += "     " + subObjectCode + "\n";
            }
            if (errorsList != null) {
                for (int i = 0; i < errorsList.size(); ++i) {
                    lines += "  **** " +
                             Constants.ErrorMessages.get(errorsList.get(i)) +
                             ". ****\n";
                }
            }
            if (sourceLine.getMnemonic().toUpperCase().equals("END")) {
                break;
            }
        }
        PrintWriter pw = new PrintWriter(new File("LISTFILE"));
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
