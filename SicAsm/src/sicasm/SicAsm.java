package sicasm;

import java.io.IOException;

public class SicAsm {
    
    public static void main(String[] args) throws IOException {
        // ListFile listFile = new ListFile("SRCFILE03", true);
        ObjectFile objectFile = new ObjectFile("SRCFILE03");
        try {
            objectFile.export();
        } catch (Exception e) {
            System.out.println("Something prevented ListFile to exist!");
        }
    }

}
