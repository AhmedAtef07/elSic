package sicasm;

public class ExpressionTerm {
    
    public enum Operator {
        PLUS,
        MINUS,
        TIMES,
        DIVIDE,
    }
    
    public enum Sign {
        POSITIVE,
        NEGATIVE,
    }
    
    private int value;
    private final Operator operator;
    private final Sign sign;

    public ExpressionTerm(Operator operator, Sign sign) {
        this.operator = operator;
        this.sign = sign;
    }

    public Sign getSign() {
        return sign;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public Operator getOperator() {
        return operator;
    }
    
}
