package sicasm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ObjectFile {

    private ArrayList<SourceLine> sourceLines;
    private Boolean canProcess;
    private int startAddress,
                programLength;
    public ObjectFile(String sourceFile) throws IOException {
        ListFile listFile = new ListFile(sourceFile, true);
        sourceLines = listFile.getSourceLines();
        canProcess = !listFile.getErrorsExist();
        startAddress = listFile.getStartAddress();
        programLength = listFile.getProgramLength();
    }
    
    public void export() throws FileNotFoundException {
        if (!canProcess) {
            System.out.println("SRCFILE is not a good file!");
            PrintWriter pw = new PrintWriter(new File("OBJFILE"));
            pw.print("Check LISTFILE - SRCFILE contains errors.");
            pw.flush();
            pw.close();
            return;
        }
        String lines = "";
        String record = "";
        int locationCounter = startAddress;
        int lineNumber = -1; 
        for(SourceLine sourceLine : sourceLines) {
            ++lineNumber;
            if (sourceLine.getIsLineComment()) {
                continue;
            }            
            if (sourceLine.getMnemonic().equalsIgnoreCase("START")) {
                // Program name is max 6 chars.
                lines += String.format("H%-6s%06X%06X\n",
                        sourceLine.getLabel().substring(0, 
                                Math.min(sourceLine.getLabel().length(), 6)),
                        startAddress,
                        programLength);                        
                continue;               
            }   
            
            if (sourceLine.getMnemonic().equalsIgnoreCase("RESW") || 
                sourceLine.getMnemonic().equalsIgnoreCase("RESB") ||
                lineNumber == sourceLines.size() - 1) {                
                int it = 0;
                while(it < record.length()) {
                    int recoredLength = Math.min(60, record.length() - it);
                    lines += String.format("T%06X%02X", locationCounter,
                            Math.min(it + 60, recoredLength / 2));
                    lines += record.substring(
                            it, it = Math.min(it + 60, record.length())) + "\n";  
                    locationCounter += recoredLength / 2;
                }                
                record = "";
                if (sourceLine.getMnemonic().equalsIgnoreCase("RESW")) {
                    locationCounter +=
                            Integer.parseInt(sourceLine.getOperand()) * 3;
                } else if (sourceLine.getMnemonic().equalsIgnoreCase("RESB")) {
                    locationCounter += 
                            Integer.parseInt(sourceLine.getOperand());
                } else {
                    break;
                }                
                continue;
            }
            record += sourceLine.getObjectCode();
        }
        System.out.println(lineNumber + " " + (sourceLines.size() - 1));
        lines += String.format("E%06X\n", startAddress);
        PrintWriter pw = new PrintWriter(new File("OBJFILE"));
        pw.print(lines);
        pw.flush();
        pw.close();
    }
}
