package sicasm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public final class ObjectFile {
    
    private final ArrayList<SourceLine> sourceLines;
    private final Boolean errorsExist;
    private final int startAddress,
                      programLength;
    private final String fileDir;
    
    public ObjectFile(String sourceFile, Boolean generateListFile) 
            throws IOException {
        ListFile listFile = new ListFile(sourceFile, generateListFile);
        fileDir = new File(sourceFile).getParent();
        sourceLines = listFile.getSourceLines();
        errorsExist = listFile.getErrorsExist();
        startAddress = listFile.getStartAddress();
        programLength = listFile.getProgramLength();
        export();
    }
    
    private void export() throws FileNotFoundException {
        if (errorsExist) {
            System.out.println("Source file contains errors!");
            PrintWriter pw = new PrintWriter(new File(fileDir, "OBJFILE"));
            pw.print("LISTFILE was generated with errors.");
            pw.flush();
            pw.close();
            return;
        }
        String lines = "";
        String record = "";
        int locationCounter = startAddress;
        for(SourceLine sourceLine : sourceLines) {
            if (sourceLine.isLineComment()) {
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
                sourceLine.getMnemonic().equalsIgnoreCase("END")) {                
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
            } else {
                record += sourceLine.getObjectCode();
            }
        }
        lines += String.format("E%06X\n", startAddress);
        PrintWriter pw = new PrintWriter(new File(fileDir, "OBJFILE"));
        pw.print(lines);
        pw.flush();
        pw.close();
    }  

    public Boolean isErrorsExist() {
        return errorsExist;
    }
}
