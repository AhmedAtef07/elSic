package sicasm;

public class ExpressionTerm {
    
    public enum Sign {
        PLUS,
        MINUS,
        TIMES,
        DIVIDE,
    }
    
    private int value;
    private Sign sign;
    private String Label;

    public ExpressionTerm(int value, Sign sign, String Label) {
        this.value = value;
        this.sign = sign;
        this.Label = Label;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public Sign getSign() {
        return sign;
    }

    public String getLabel() {
        return Label;
    }
    
}
