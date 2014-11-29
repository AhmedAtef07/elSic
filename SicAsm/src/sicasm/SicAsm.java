package sicasm;

import java.io.IOException;

public class SicAsm {
    
    public static void main(String[] args) throws IOException {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GUI();
            }
        });        
    }    
}
