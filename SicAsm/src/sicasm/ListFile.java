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
        SourceFile sourceFile = new SourceFile(sourceFileName);
        sourceLines = sourceFile.getTokenz();
        symTable = new TreeMap<>();
        instructionCounter = 0;
        
        passOne();
        passTwo();
        //setStartAddress();
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
    
    private void setStartAddress() {
        if (sourceLines.get(0).getMnemonic().equalsIgnoreCase("START")) {
            String operand = sourceLines.get(0).getOperand();
            if (isHexInteger(operand)) {
                locationCounter = Integer.parseInt(operand, 16);
            } else {
                locationCounter = 0;
                // throw some error becuase the operand is not a number.
            }
        } else {
            locationCounter = 0;
        }
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
            // Handeling Mnemonics and directives.
            if (Constants.OpTable.containsKey(
                    sourceLine.getMnemonic().toUpperCase())) {
                locationCounter += 3;
            } else if (sourceLine.getMnemonic().equalsIgnoreCase("START")) {
                if (lineNumber != 1) {
                    // Error misplaces start.
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
            } else if (sourceLine.getMnemonic().equalsIgnoreCase("END")) {
                
            } else if (sourceLine.getMnemonic().equalsIgnoreCase("WORD")) {
                locationCounter += 3;
            } else if (sourceLine.getMnemonic().equalsIgnoreCase("BYTE")) {
                String op = sourceLine.getOperand();
                if (op.charAt(0) == 'c' || op.charAt(0) == 'C') {
                    locationCounter += op.length() - 3;
                } else if (op.charAt(0) == 'x' || op.charAt(0) == 'X') {
                    int hexLength = op.length() - 3;
                    if (hexLength % 2 == 0) {
                        locationCounter += hexLength / 2;
                    } else {
                        // ERROR IT MUST BE EVEN!!!!!!!
                    }
                }
            } else if (sourceLine.getMnemonic().equalsIgnoreCase("RESW")) {
                locationCounter += Integer.parseInt(
                        sourceLine.getOperand()) * 3;
            } else if (sourceLine.getMnemonic().equalsIgnoreCase("RESB")) {
                locationCounter += Integer.parseInt(sourceLine.getOperand());
            } else {
                // Error undefined mneomonic!!!!!!
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
            String thisObjectCode = "";            
            // Handeling Mnemonics and directives.
            if (Constants.OpTable.containsKey(
                    sourceLine.getMnemonic().toUpperCase())) {
                thisObjectCode = Constants.OpTable.get(
                        sourceLine.getMnemonic().toUpperCase());
                thisObjectCode += Integer.toHexString(
                        symTable.get(sourceLine.getOperand())).toUpperCase();
                System.out.println(symTable.get(sourceLine.getOperand()));
            } else if (sourceLine.getMnemonic().equalsIgnoreCase("START")) {
                // Set error in case of duplication, or not first line.
            } else if (sourceLine.getMnemonic().equalsIgnoreCase("END")) {
                // Terminate proccessing. and there should not be any END here
                // as pass one should not process any further.
            } else if (sourceLine.getMnemonic().equalsIgnoreCase("WORD")) {
                if (isValidWordOperand(sourceLine.getOperand())) {
                    thisObjectCode = String.format(
                            "%06X", Integer.parseInt(sourceLine.getOperand()));                    
                } else {
                     // Invalid word operand.
                }                
            } else if (sourceLine.getMnemonic().equalsIgnoreCase("BYTE")) {
                // Check there are no flag for unclosed quote.
                String op = sourceLine.getOperand();
                if (isValidByteOperand(op)) {                    
                    if (op.charAt(0) == 'c' || op.charAt(0) == 'C') {
                        for (int i = 2; i < op.length() - 1; i++) {
                            thisObjectCode += Integer.toHexString(
                                    (int)op.charAt(i)).toUpperCase();
                        }
                    } else if (op.charAt(0) == 'x' || op.charAt(0) == 'X') {
                        String hexNumber = op.substring(2, op.length() - 1);
                        if (isHexInteger(hexNumber)) {     
                            int hexLength = op.length() - 3;
                            if (hexLength % 2 == 0) {
                                thisObjectCode = hexNumber.toUpperCase();
                            } else {
                                // ERROR IT MUST BE EVEN!!!!!!!
                            }
                        } else {
                            // Not valid Hex.
                        }
                    }
                } else {
                    // Invalid Byte operand.
                }
            } else if (sourceLine.getMnemonic().equalsIgnoreCase("RESW")) {
            } else if (sourceLine.getMnemonic().equalsIgnoreCase("RESB")) {
            } else {
                // Error undefined mneomonic!!!!!!
                System.out.println("Error");
            }
            sourceLine.setObjectCode(thisObjectCode);
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
    
    private Boolean isValidWordOperand(String operand) {
        for (int i = 0; i < operand.length(); i++) {
            if (!(operand.charAt(i) >= '0' && operand.charAt(i) <= '9')){
                return false;
            }
        }
        return Integer.parseInt(operand) <= 0xFFFFFF;
    }
    
    public void export() throws FileNotFoundException {
        String lines = "";
        for (SourceLine sourceLine : sourceLines) {
            if (sourceLine.getIsLineComment()) {
                lines += "          " + sourceLine.getComment() + "\n";
                continue;
            }
            int it = 0;
            String objectCode = sourceLine.getObjectCode();
            String subObjectCode = objectCode.substring(
                    it, it = Math.min(it + 6, objectCode.length()));
            
            lines += String.format("%05d %04X %-6s %-8s %-7s %-8s %s\n", 
                                   sourceLine.getAddressLocation(),    
                                   sourceLine.getAddressLocation(),    
                                   subObjectCode,
                                   sourceLine.getLabel(), 
                                   sourceLine.getMnemonic(), 
                                   sourceLine.getOperand(), 
                                   sourceLine.getComment()); 
            while(it < objectCode.length()) {
                 subObjectCode = objectCode.substring(
                         it, it = Math.min(it + 6, objectCode.length()));   
                 lines += "           " + subObjectCode + "\n";
            }
        }
        PrintWriter pw = new PrintWriter(new File("LISTFILE"));
        pw.print(lines);
        pw.flush();
        pw.close();
    }
}
