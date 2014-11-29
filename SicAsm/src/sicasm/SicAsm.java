package sicasm;

import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SicAsm extends JPanel {

    private JFileChooser fc;

    public SicAsm() {
        try {
            fc = new JFileChooser();
            if (fc.showOpenDialog(SicAsm.this) == JFileChooser.APPROVE_OPTION) {
                assemble(fc.getSelectedFile().getPath());
                JOptionPane.showMessageDialog(null, "File successfully assembled.", "elSic response", WIDTH);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to assemble the file.", "elSic response", WIDTH);
        }
    }

    public static void main(String[] args) throws IOException {
        SicAsm run = new SicAsm();
    }

    static void assemble(String fileName) throws IOException {
        new ObjectFile(fileName, true);
    }
}
