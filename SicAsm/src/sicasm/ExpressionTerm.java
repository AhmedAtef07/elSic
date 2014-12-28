package sicasm;

public class ExpressionTerm {
    
    public enum Operator {
        PLUS,
        MINUS,
        TIMES,
        DIVIDE,
    }
    
    public enum Sign {
        POSTIVE,
        NEGATIVE,
    }
    
    private int value;
    private Operator operator;
    private String label;
    
    boolean isVariable;
    
    public ExpressionTerm(Operator operator, String label) {
        this.operator = operator;
        this.label = label;
    }
    
    public ExpressionTerm(int value) {
        this.value = value;
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

    public String getLabel() {
        return label;
    }
    
}
