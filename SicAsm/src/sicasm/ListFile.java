package sicasm;

import java.util.ArrayList;

public class ListFile {
    ArrayList<SourceLine> sourceLines;
    int startAddress;
    public ListFile() {
        sourceLines = new ArrayList<SourceLine>();
        sourceLines.add(new SourceLine("LABEL1", "ADD", "ALPHA", ""));
        sourceLines.add(new SourceLine("LABEL1", "ADD", "ALPHA", ""));
        
        setStartAddress();
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
                startAddress = Integer.parseInt(operand, 16);
            } else {
                startAddress = 0;
            }
        } else {
            startAddress = 0;
        }
    }
    
    
    
}
