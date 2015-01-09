package sicasm;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class SicAsm extends JPanel {

    private JFileChooser fc;

    public static void main(String[] args) throws Exception {
//         assemble("E:\\Ahmed\\College\\Term 6\\Subjects\\System Programming\\"
//                 + "Assembler TA\\elSic\\SicAsm\\jars\\SRCFILE", true);
//        new SicAsm().openFileDialog();
//         new SicAsm().assembleSourceFile();
         new SicAsm().runGUI();       
    }
    
    public void openFileDialog() {
        try {
            fc = new JFileChooser();
            if (fc.showOpenDialog(SicAsm.this) == JFileChooser.APPROVE_OPTION) {
                assemble(fc.getSelectedFile().getPath(), true);  
            }
        } catch (Exception e) { 
            JOptionPane.showMessageDialog(null, "Failed to assemble the file." +
                    "\n" + e.getMessage(),
                    "elSic response", JOptionPane.ERROR_MESSAGE);
        }   
    }

    public void assembleSourceFile() {
        try {
            File file = new File(System.getProperty("user.dir"), "SRCFILE");
            System.out.println(file.getPath());
            if (file.exists()) {
                assemble(file.getPath(), true);
            }
            else {
                JOptionPane.showMessageDialog(null, "Couldn't find 'SRCFILE'!",
                    "elSic response", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to assemble the file." +
                    "\n" + e.getMessage(),
                    "elSic response", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void runGUI() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info :
                        UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {                
                System.out.println("Something went wrong loading Nimbus");                
            }
            new GUI();
        });     
    }
    
    static void assemble(String filePath, boolean showMessageResults) 
            throws Exception {
        boolean errorsExist = new ObjectFile(filePath, true).isErrorsExist();
        if (showMessageResults) {
            showMessageResults(errorsExist);
        }        
    }
    
    static void showMessageResults(boolean errorsExist)  {
        if (errorsExist) {
            JOptionPane.showMessageDialog(null,
                    ("File successfully assembled.\n" +
                     "LISTFILE generated with errors."), 
                    "elSic response", JOptionPane.INFORMATION_MESSAGE);            
        } else {
           JOptionPane.showMessageDialog(null,
                    ("File successfully assembled.\n" +
                     "LISTFILE generated successfully.\n" +
                     "OBJFILE generated successfully."), 
                    "elSic response", JOptionPane.INFORMATION_MESSAGE);             
        }   
    }
}
