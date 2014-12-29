package sicasm;

import java.util.*;
import sicasm.Constants.Errors;

import sicasm.ExpressionTerm.Operator;
import sicasm.ExpressionTerm.Sign;
import sicasm.Symbol.Type;

public class ExpressionManager {

    private final SymbolTable symbolTable;
    private static boolean instanceTaken = false;

    private ExpressionManager(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public static ExpressionManager getExpressionManager(
            SymbolTable symbolTable) throws Exception {
        if (instanceTaken) {
            throw new Exception(
                    "Only one instance can be taken from this class");
        }
        instanceTaken = true;
        return new ExpressionManager(symbolTable);
    }

    private boolean isInteger(String t) {
        try {
            Integer.parseInt(t);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Symbol findSymbol(String label) {
        try {
            for (int i = 0; true; i++) {
                if (symbolTable.get(i).getLabel().equals(label)) {
                    return symbolTable.get(i);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private boolean isOperator(char x) {
        return x == '+' || x == '-' || x == '*' || x == '/';
    }

    private boolean isUnaryOperator(char x) {
        return x == '+' || x == '-';
    }

    private Operator getOperator(char x) {
        switch (x) {
            case '-':
                return Operator.MINUS;
            case '*':
                return Operator.TIMES;
            case '/':
                return Operator.DIVIDE;
            default:
                return Operator.PLUS;
        }
    }

    private final class OperatorAndSign {

        private final ArrayList<Operator> op;
        private final Operator operator;
        private Sign sign;
        private boolean validUnary = true;

        public OperatorAndSign(ArrayList<Operator> op) {
            this.op = op;
            operator = op.get(0);
            make();
        }

        public boolean isValidUnary() {
            return validUnary;
        }

        public Operator getOperator() {
            return operator;
        }

        public Sign getSign() {
            return sign;
        }

        public void make() {
            int negative = 0;
            for (int i = 1; i < op.size(); i++) {
                if (op.get(i) == Operator.DIVIDE
                        || op.get(i) == Operator.TIMES) {
                    validUnary = false;
                } else if (op.get(i) == Operator.MINUS) {
                    negative++;
                }
            }

            if ((negative & 1) == 1) {
                sign = Sign.NEGATIVE;
            } else {
                sign = Sign.POSITIVE;
            }
        }
    }

    public ExpressionResult evaluate(String expression) {
        ArrayList<Errors> errors = new ArrayList<>();
        ArrayList<ExpressionTerm> expressionTerms = new ArrayList<>();
        ArrayList<Operator> operators = new ArrayList<>();
        boolean isValid = true;
        boolean flagOfend = false;
        if (isOperator(expression.charAt(expression.length() - 1))) {
            errors.add(Errors.EXPRESSION_ILLEGAL_END);
            isValid = false;
            flagOfend = true;
        }
        int i;
        int countnegative = 0;
        for (i = 0; isOperator(expression.charAt(i)); i++) {
            if (!isUnaryOperator(expression.charAt(i))) {
                errors.add(Constants.Errors.EXPRESSION_ILLEGAL_START);
                isValid = false;
            } else if (expression.charAt(i) == '-') {
                countnegative++;
            }
        }
        Operator sign;
        Sign s;
        if ((countnegative & 1) == 1) {
            sign = Operator.MINUS;
            s = Sign.NEGATIVE;
        } else {
            sign = Operator.PLUS;
            s = Sign.POSITIVE;
        }
        //operators.add(sign);
        expressionTerms.add(new ExpressionTerm(sign, s));
        StringBuilder term = new StringBuilder("");
        int relativeTerms = 0;
        boolean here = true, herop = false;
        while (i < expression.length()) {
            // System.out.println("term : "+term.toString());
            if (isOperator(expression.charAt(i))) {
                int value;
                if (!herop) {
                    if (isInteger(term.toString())) {
                        value = Integer.parseInt(term.toString());
                        if (expressionTerms.get(expressionTerms.size() - 1)
                                .getSign()
                                == Sign.NEGATIVE) {
                            value = -value;
                        }
                        expressionTerms.get(expressionTerms.size() - 1).
                                setValue(value);
//                        System.out.println("el term : " + term.toString());
                        //System.out.println("hereasfasfas");
                    } else {

                        String label = term.toString();
                        Symbol sym = findSymbol(label);
                        if (sym != null) {
//                            System.out.println("el term : " + term.toString());
                            if (sym.getType() == Type.RELATIVE) {
//                                System.out.println("Relative");
                                if (expressionTerms
                                        .get(expressionTerms.size() - 1)
                                        .getOperator() == Operator.MINUS) {
                                    if (expressionTerms.get(
                                            expressionTerms.size() - 1)
                                            .getSign()
                                            == Sign.NEGATIVE) {
                                        relativeTerms++;
                                    } else {
                                        relativeTerms--;
                                    }
                                } else if (expressionTerms
                                        .get(expressionTerms.size() - 1)
                                        .getOperator() == Operator.PLUS) {
                                    if (expressionTerms.get(
                                            expressionTerms.size() - 1)
                                            .getSign()
                                            == Sign.POSITIVE) {
                                        relativeTerms++;
                                    } else {
                                        relativeTerms--;
                                    }
                                } else {
                                    errors
                                      .add(Errors
                                        .EXPRESSION_INVALID_RELATIVE_OPERATOR);
                                    isValid = false;
                                }
                            }
                            value = sym.getAddressLoction();
                            if (expressionTerms.get(expressionTerms.size() - 1).
                                    getSign() == Sign.NEGATIVE) {
                                value = -value;
                            }
                            expressionTerms.get(expressionTerms.size() - 1).
                                    setValue(value);
                        } else {
                            errors.add(Errors.EXPRESSION_LABEL_UNDEFINED);
                            isValid = false;
                        }
                    }
                    herop = true;
                }
                term.delete(0, term.length());
                operators.add(getOperator(expression.charAt(i)));
                here = false;
            } else {
                if (!here) {
//                    System.out.print( "Operators : "+operators);
//                    System.out.println("");
                    OperatorAndSign opas = new OperatorAndSign(operators);
                    expressionTerms.add(new ExpressionTerm(opas.getOperator(),
                            opas.getSign()));
                    if (!opas.isValidUnary()) {
                        errors.add(
                                Errors.EXPRESSION_INVALID_UNARY_OPERATOR);
                        isValid = false;
                    }
                    operators.clear();
                    here = true;
                }
                term.append(expression.charAt(i));
                herop = false;
            }
            ++i;
        }
        if (!flagOfend) {
            int value;
            if (!herop) {
                if (isInteger(term.toString())) {
                    value = Integer.parseInt(term.toString());
                    if (expressionTerms.get(expressionTerms.size() - 1)
                            .getSign()
                            == Sign.NEGATIVE) {
                        value = -value;
                    }
                    expressionTerms.get(expressionTerms.size() - 1).
                            setValue(value);
//                    System.out.println("el term : " + term.toString());
                    //System.out.println("hereasfasfas");
                } else {

                    String label = term.toString();
                    Symbol sym = findSymbol(label);
                    if (sym != null) {
//                        System.out.println("el term : " + term.toString());
                        if (sym.getType() == Type.RELATIVE) {
//                            System.out.println("Relative");
                            if (expressionTerms.get(expressionTerms.size() - 1)
                                    .getOperator() == Operator.MINUS) {
                                if (expressionTerms.get(
                                        expressionTerms.size() - 1).getSign()
                                        == Sign.NEGATIVE) {
                                    relativeTerms++;
                                } else {
                                    relativeTerms--;
                                }
                            } else if (expressionTerms
                                    .get(expressionTerms.size() - 1)
                                    .getOperator() == Operator.PLUS) {
                                if (expressionTerms.get(
                                        expressionTerms.size() - 1).getSign()
                                        == Sign.POSITIVE) {
                                    relativeTerms++;
                                } else {
                                    relativeTerms--;
                                }
                            } else {
                                errors.add(Errors
                                        .EXPRESSION_INVALID_RELATIVE_OPERATOR);
                                isValid = false;
                            }
                        }
                        value = sym.getAddressLoction();
                        if (expressionTerms.get(expressionTerms.size() - 1).
                                getSign() == Sign.NEGATIVE) {
                            value = -value;
                        }
                        expressionTerms.get(expressionTerms.size() - 1).
                                setValue(value);
                    } else {
                        errors.add(Constants.Errors.EXPRESSION_LABEL_UNDEFINED);
                        isValid = false;
                    }
                }
                herop = true;
            }
            term.delete(0, term.length());
            //operators.add(getOperator(expression.charAt(i)));
            here = false;
        }
        if (relativeTerms != 0) {
            errors.add(Constants.Errors.EXPRESSION_ODD_RELATIVE_TERMS);
            isValid = false;
        }
//        System.out.println("\n Relativ : " + relativeTerms + "\n");
        //debugg
//        for (ExpressionTerm expressionTerm : expressionTerms) {
//            System.out.println("Operator : " + expressionTerm.getOperator());
//            System.out.println("Sign : " + expressionTerm.getSign());
//            System.out.println("Value : " + expressionTerm.getValue());
//            System.out.println("");
//        }
        if (!isValid) {
            return new ExpressionResult(errors, 0);
        }
        return evaluate(expressionTerms);
    }

    private ExpressionResult evaluate(ArrayList<ExpressionTerm> terms) {
        Stack<Integer> term = new Stack<>();
        Stack<Operator> op = new Stack<>();
        term.push(terms.get(0).getValue());
        for (int i = 1; i < terms.size(); i++) {
            if (terms.get(i).getOperator() == ExpressionTerm.Operator.TIMES
                    || terms.get(i).getOperator()
                    == ExpressionTerm.Operator.DIVIDE) {
                int x = terms.get(i).getValue();
                int y = term.pop();
                if (terms.get(i).getOperator()
                        == ExpressionTerm.Operator.TIMES) {
                    term.push(y * x);
                } else {
                    if (x == 0) {
                        ArrayList<Constants.Errors> errors = new ArrayList<>();
                        errors.add(Constants.Errors.EXPRESSION_ZERO_DIVISION);
                        return new ExpressionResult(errors, 0);
                    }
                    term.push(y / x);
                }
            } else {
                term.push(terms.get(i).getValue());
                op.push(terms.get(i).getOperator());
            }
        }
        while (!op.empty()) {
            ExpressionTerm.Operator oper = op.pop();
            int x = term.pop();
            int y = term.pop();
            if (oper == ExpressionTerm.Operator.PLUS) {
                term.push(x + y);
            } else {
                term.push(y - x);
            }
        }
        System.out.println("exp result : " + term.peek());
        return new ExpressionResult(null, term.pop());
    }
}
