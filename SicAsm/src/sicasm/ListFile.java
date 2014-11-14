package sicasm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeMap;

public final class ListFile {
    ArrayList<SourceLine> sourceLines;
    // ArrayList<Symbol> symTable;
    int locationCounter;
    int instructionCounter;
    TreeMap<String, Integer> symTable;
    public ListFile(String sourceFileName) throws IOException {
        SourceLine.resetMaxLength();
        SourceFile sourceFile = new SourceFile(sourceFileName);
        sourceLines = sourceFile.getTokenz();
        symTable = new TreeMap<>();
        instructionCounter = 0;
        
        passOne();
        passTwo();
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
        int lineNumber = 0;
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
                if (lineNumber != 1) {
                    // Error misplaces start.
                    sourceLine.addError(Constants.Errors.DUPLICATE_START);
                    continue;
                }
                String operand = sourceLines.get(0).getOperand();
                if (isHexInteger(operand)) {
                    locationCounter = Integer.parseInt(operand, 16);
                    if (locationCounter >= 0x8000) {
                        // Out of memeory 
                    }
                } else {
                    locationCounter = 0;
                    
                    // throw some error becuase the operand is not a number.
                }                 
            } else if (menomonic.equals("END")) {
                return;
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
                // Error undefined mneomonic!!!!!!
                sourceLine.addError(Constants.Errors.UNRECOGNIZED_MNEMONIC);
                System.out.println(menomonic);
                System.out.println("Error");
            }
        }
    }
    
    private void passTwo() {
        for (SourceLine sourceLine : sourceLines) {
            if (sourceLine.getIsLineComment()) {
                continue;
            }                    
            // TODO(AhmedAtef07): Convert following else ifs into switch case.
            // TODO(AhmedAtef07): Check ignore case or all to upper.
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
        }
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
        return Integer.parseInt(operand) <= 0xFFFFFF ? 7 : 1;
    }
    
    public void export() throws FileNotFoundException {
        String lines = "ElSic Assembler V1.0\n\n";
        for (SourceLine sourceLine : sourceLines) {
            if (sourceLine.getIsLineComment()) {
                lines += "            " + sourceLine.getComment() + "\n";
                continue;
            }            
            int it = 0;
            ArrayList<Constants.Errors> errorsList = sourceLine.getErrorsList();
            String objectCode = sourceLine.getObjectCode();
            String subObjectCode;
            if (errorsList == null) {
                subObjectCode = objectCode.substring(
                        it, it = Math.min(it + 6, objectCode.length()));
            } else {
                subObjectCode = "";
            }
            
            lines += String.format(
                    "%04X %-6s %-" + SourceLine.getLabelMaxLength() + "s  %-" + 
                    SourceLine.getMnemonicMaxLength()+ "s  %-" + 
                    SourceLine.getOperandMaxLength()+ "s  %s\n",
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
                             ". ****n";
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
}
