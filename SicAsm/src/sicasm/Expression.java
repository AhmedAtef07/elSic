package sicasm;

import java.util.*;
import sicasm.ExpressionTerm.Sign;

public class Expression {
    
    private final String expression;
    private boolean valid;
    private int value;
    private ArrayList<ExpressionTerm> terms;

    public ArrayList<ExpressionTerm> getTerms() {
        return new ArrayList<ExpressionTerm> (terms);
    }
    
    public Expression(String expression) {
        this.expression = expression;
        valid = validCheck();
        if(valid)
            buildTerms();
    }
    
    private boolean validCheck() {
        if(expression.isEmpty()) return false;
        String[] terms = expression.split("[+,-,-,*,/]");
        for (String term : terms) {
            if (term.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    private boolean ready;
    
    private boolean isInteger(String t) {
        try {
            Integer.parseInt(t,16);
            return true;
        }catch(Exception e) {
            return false;
        }
    }
    
    private boolean isOperator(String x) {
        return x.equals("+") || x.equals("-") || x.equals("-") || x.equals("*")
               || x.equals("/");
    }
    
    private int toNextOperator(int j, String[] operand) {
        while( j < operand.length && !isOperator(operand[j]) ) j++;
        return j;
    }
    
    private void buildTerms() {
        terms = new ArrayList<ExpressionTerm>();
        Sign sign = ExpressionTerm.Sign.PLUS;
        String[] component = expression.split("[+,-,-,*,/]");
        String[] operator = expression.split("[^(+,-,-,*,/)]");
        int j = toNextOperator(0, operator);
        for(int i = 0 ; i < component.length ; i ++ ) {
            if ( isInteger(component[i]) )
                terms.add(new ExpressionTerm(Integer.parseInt(component[i]), 
                                                                sign, ""));
            else {
                terms.add(new ExpressionTerm(Integer.MIN_VALUE, sign, 
                                                        component[i]));
                ready = false;
            }
            if(i != component.length - 1) {
                switch (operator[j]) {
                    case "-":
                        sign = ExpressionTerm.Sign.MINUS;
                        break;
                    case "+":
                        sign = ExpressionTerm.Sign.PLUS;
                        break;
                    case "*":
                        sign = ExpressionTerm.Sign.TIMES;
                        break;
                    case "/":
                        sign = ExpressionTerm.Sign.DIVIDE;
                }
            }
            j = toNextOperator(j+1, operator);
        }
    }
    
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isReady() {
        return ready;
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public int evaluate() {
        return value;
    }
}
