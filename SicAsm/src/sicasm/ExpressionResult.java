package sicasm;

import java.util.ArrayList;

public class ExpressionResult {

    private final ArrayList<Constants.Errors> errors;
    private final int value;

    public ExpressionResult(ArrayList<Constants.Errors> errors, int value) {
        this.errors = errors;
        this.value = value;
    }

    public ArrayList<Constants.Errors> getErrors() {
        return errors;
    }

    public int getValue() {
        return value;
    }    
    
    public boolean containsErrors() {
        return errors != null;
    }
}
