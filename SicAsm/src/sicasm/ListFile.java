package sicasm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeMap;

public final class ListFile {
    ArrayList<SourceLine> sourceLines;
    // ArrayList<Symbol> symTable;
    int locationCounter;
    int instructionCounter;
    TreeMap<String, Integer> symTable;
    public ListFile() {
        sourceLines = new ArrayList<SourceLine>();
        // symTable = new ArrayList<Symbol>();
        symTable = new TreeMap<>();
        instructionCounter = 0;
        sourceLines.add(new SourceLine("TEST", "START", "1000", ""));
        sourceLines.add(new SourceLine("LABEL1", "ADD", "ALPHA", ""));
        sourceLines.add(new SourceLine("", "ADD", "ALPHA", ""));
        sourceLines.add(new SourceLine("", "WORD", "5", ""));
        sourceLines.add(new SourceLine("", "RESW", "10", ""));
        sourceLines.add(new SourceLine("", "BYTE", "c'ahmed atef'", ""));
        sourceLines.add(new SourceLine("", "BYTE", "x'F6'", ""));
        sourceLines.add(new SourceLine("", "RESB", "2", ""));
        
        passOne();
        //setStartAddress();
    }
    
    private Boolean isHexInteger(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!((s.charAt(i) >= '0' && s.charAt(i) <= '9') || 
                  (s.charAt(i) >= 'A' && s.charAt(i) <= 'F'))) {
                return false;
            }
        }
        return true;
    }
    
    private void setStartAddress() {
        if (sourceLines.get(0).getMnemonic().equals("START")) {
            String operand = sourceLines.get(0).getOperand();
            if (isHexInteger(operand)) {
                locationCounter = Integer.parseInt(operand, 16);
                // System.out.println(Integer.toHexString(startAddress));
            } else {
                locationCounter = 0;
                // throw some error becuase the operand is not a number.
            }
        } else {
            locationCounter = 0;
        }
    }
    private void passOne() {
        for (int i = 0; i < sourceLines.size(); i++) {            
            if (sourceLines.get(i).isLineComment) {
                continue;
            } 
            sourceLines.get(i).setAddressLocation(locationCounter);
            // Adding labels into symTable.
            if (!sourceLines.get(i).getLabel().isEmpty()) {
                if (symTable.containsKey(sourceLines.get(i).getLabel())) {
                    // ERROR DUPLICATE!!!!!!!
                } else {
                    symTable.put(sourceLines.get(i).getLabel(), locationCounter);                    
                }
            }            
            // Handeling Mnemonics and directives.
            if (Constants.OpTable.containsKey(sourceLines.get(i).getMnemonic())) {
                locationCounter += 3;
            } else if (sourceLines.get(i).getMnemonic().equals("START")) {
                locationCounter = Integer.parseInt(
                        sourceLines.get(i).getOperand(), 16);
            } else if (sourceLines.get(i).getMnemonic().equals("END")) {
                
            } else if (sourceLines.get(i).getMnemonic().equals("WORD")) {
                locationCounter += 3;
            } else if (sourceLines.get(i).getMnemonic().equals("BYTE")) {
                String op = sourceLines.get(i).getOperand();
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
            } else if (sourceLines.get(i).getMnemonic().equals("RESW")) {
                locationCounter += Integer.parseInt(
                        sourceLines.get(i).getOperand()) * 3;
            } else if (sourceLines.get(i).getMnemonic().equals("RESB")) {
                locationCounter += Integer.parseInt(
                        sourceLines.get(i).getOperand());
            } else {
                // Error undefined mneomonic!!!!!!
                System.out.println("Error");
            }
            
           
        }
    }
    
    public void export() throws FileNotFoundException {
        String lines = "";
        for (SourceLine sourceLine : sourceLines) {
            lines += String.format(
                    "%04X %-8s %-7s %-8s %s\n", sourceLine.getAddressLocation(),
                                                sourceLine.getLabel(), 
                                                sourceLine.getMnemonic(), 
                                                sourceLine.getOperand(), 
                                                sourceLine.getComment()); 
        }
        PrintWriter pw = new PrintWriter(new File("LISTFILE"));
        pw.print(lines);
        pw.flush();
        pw.close();
    }
}
