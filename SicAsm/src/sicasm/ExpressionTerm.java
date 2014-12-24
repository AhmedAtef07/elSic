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
    private String label;

    public ExpressionTerm(int value, Sign sign, String label) {
        this.value = value;
        this.sign = sign;
        this.label = label;
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
        return label;
    }
    
}
