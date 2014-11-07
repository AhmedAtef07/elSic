package sicasm;

public class SourceLine {
    private String label, 
            mnemonic, 
            operand, 
            comment;

    public SourceLine(String label, String mnemonic, String operand, 
                      String comment) {
        this.label = label;
        this.mnemonic = mnemonic;
        this.operand = operand;
        this.comment = comment;
    }

    public String getLabel() {
        return label;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public String getOperand() {
        return operand;
    }

    public String getComment() {
        return comment;
    }    
}
