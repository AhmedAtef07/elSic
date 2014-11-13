package sicasm;

public class SicAsm {
    
    public static void main(String[] args) {
        ListFile listFile = new ListFile();
        try {
            listFile.export();            
        } catch (Exception e) {
            System.out.println("WHERE IS THE FILE!");
        }
    }

}
