package sicasm;

import java.io.IOException;

public class SicAsm {
    
    public static void main(String[] args) throws IOException {
        try {
            assemble("SRCFILE03");
        } catch (Exception e) {
            System.out.println("Something wrong happened!\n" + e.getMessage());
        }
    }
    
    static void assemble(String fileName) throws IOException {
        new ObjectFile(fileName, true);
    }
}
