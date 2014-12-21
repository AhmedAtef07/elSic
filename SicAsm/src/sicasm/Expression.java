package sicasm;

public class Expression {
    
    private String expression;
    private boolean valid;
    private int value;
    
    public Expression(String expression) {
        this.expression = expression;
    }
    
    
    public boolean isValid() {
        return valid;
    }
    
    public int evaluate() {
        return value;
    }
}
