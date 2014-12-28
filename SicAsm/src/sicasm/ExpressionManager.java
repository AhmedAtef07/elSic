package sicasm;

import java.util.*;

import sicasm.ExpressionTerm.Operator;

public class ExpressionManager {
    
    private String expression;
    private boolean valid;
    private int value;
    private ArrayList<ExpressionTerm> terms;

    private final ArrayList<Symbol> symbolTable;
    
    private static boolean instanceTaken = false;

    private ExpressionManager(ArrayList<Symbol> symbolTable) {
        this.symbolTable = symbolTable;
    }
    
    public static ExpressionManager getExpressionManager(
            ArrayList<Symbol> symbolTable) throws Exception {
        if (instanceTaken) {
            throw new Exception(
                    "Only one instance can be taken from this class");
        }
        instanceTaken = true;
        return new ExpressionManager(symbolTable);
    }
    
    public ArrayList<ExpressionTerm> getTerms() {
        return new ArrayList<ExpressionTerm> (terms);
    }
    
    /**
     * Return false only if there exists 2 consecutive operators.
     */ 
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
        Operator sign = ExpressionTerm.Operator.PLUS;
        String[] component = expression.split("[+,-,-,*,/]");
        String[] operator = expression.split("[^(+,-,-,*,/)]");
        int j = toNextOperator(0, operator);
        for(int i = 0 ; i < component.length ; i ++ ) {
            if ( isInteger(component[i]) )
                terms.add(new ExpressionTerm(sign, component[i]));
            else {
                terms.add(new ExpressionTerm(Integer.parseInt(component[i])));
                ready = false;
            }
            if(i != component.length - 1) {
                switch (operator[j]) {
                    case "-":
                        sign = ExpressionTerm.Operator.MINUS;
                        break;
                    case "+":
                        sign = ExpressionTerm.Operator.PLUS;
                        break;
                    case "*":
                        sign = ExpressionTerm.Operator.TIMES;
                        break;
                    case "/":
                        sign = ExpressionTerm.Operator.DIVIDE;
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
    
    public ExpressionResult evaluate(String expression) {
        Stack<Integer> term = new Stack<Integer>();
        Stack<ExpressionTerm.Operator> op = new Stack<ExpressionTerm.Operator>();
        term.push(terms.get(0).getValue());
        for( int i = 1 ; i < terms.size() ; i ++) {
            if ( terms.get(i).getOperator() == ExpressionTerm.Operator.TIMES 
              || terms.get(i).getOperator() == ExpressionTerm.Operator.DIVIDE ) {
              int x = terms.get(i).getValue();
              int y = term.pop();
              if( terms.get(i).getOperator() == ExpressionTerm.Operator.TIMES )
                term.push(y*x);
              else term.push(y/x);
            } else {
                term.push(terms.get(i).getValue());
                op.push(terms.get(i).getOperator());
            }
        }
        while(!op.empty()) {
            ExpressionTerm.Operator oper = op.pop();
            int x = term.pop();
            int y = term.pop();
            if ( oper == ExpressionTerm.Operator.PLUS )
                term.push(x+y);
            else term.push(y-x);
        }
        return new ExpressionResult(null, value = term.pop());
    }
}
