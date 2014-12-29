package sicasm;

import java.io.*;
import java.util.*;

public final class SourceFile {

    public ArrayList<SourceLine> getTokenz() {
        return new ArrayList<SourceLine>(file);
    }
    private BufferedReader in;
    private HashSet<String> labels;
    
    void loadlabels(String filename) throws IOException {
        in = new BufferedReader(new FileReader(filename));
        labels = new HashSet<String>();
        String input;
        StringandNext x;
        while ((input = in.readLine()) != null) {
            if ( input.isEmpty() || input.charAt(0) <= 32 
                    || input.charAt(0) == 46 ) continue;
            x = buildtospace(0, input);
            labels.add(x.str);
        }
        in.close();
    }
    public SourceFile(String filename) throws FileNotFoundException, 
            IOException {
        
        file = new ArrayList<SourceLine>();
        loadlabels(filename);
        load(filename);
    }
    private ArrayList<SourceLine> file;

    private void load(String filename) throws IOException {
        in = new BufferedReader(new FileReader(filename));
        String input;
        String label, mnemonic, comment, operand;
        StringandNext temp;
        while ((input = in.readLine()) != null) {
            label = mnemonic = comment = operand = "";
            unclosedQuote = false;
            literal = false;
            if (input.isEmpty())
                continue;
            int i;
            if (input.charAt(0) > 32) {
                if (input.charAt(0) == 46) {
                    comment = input;
                    comment = comment.trim();
                    file.add(new SourceLine(comment));
                    continue;
                }
                temp = buildtospace(0, input);
                i = temp.next;
                label = temp.str;
            } else {
                for (i = 0; i < input.length(); i++)
                    if (input.charAt(i) > 32)
                        break;
                if (i == input.length())
                    continue;
                if (input.charAt(i) == 46) {
                    comment = input;
                    comment = comment.trim();
                    file.add(new SourceLine(comment));
                    continue;
                }
            }
            temp = buildtospace(i, input);
            i = temp.next;
            mnemonic = temp.str;
            if ( isLiteral(i, input)) {
                temp = buildLiteral(i, input);
                literal = true;
            }
            else if (is_C_or_X(i, input)&&mnemonic.equalsIgnoreCase("byte"))
                temp = buildwithquote(i, input);
            else
                temp = buildtospace(i, input);
            i = temp.next;
            operand = temp.str;
            comment = buildComment(i, input);
            label = label.trim();
            mnemonic = mnemonic.trim();
            operand = operand.trim();
            comment = comment.trim();
            if (mnemonic.equalsIgnoreCase("rsub")) {
                StringBuilder Xx = new StringBuilder(operand);
                Xx.append(' ');
                Xx.append(comment);
                comment = Xx.toString();
                comment = comment.trim();
                operand = "";
            }
            SourceLine AXX = new SourceLine(label, mnemonic, operand, comment);
            if (isExpression(operand))
                AXX.setContainsExpression();
            if (unclosedQuote&&(literal || mnemonic.equalsIgnoreCase("byte")))
                AXX.addError(Constants.Errors.UNCLOSED_QUOTE);
            file.add(AXX);
        }
        in.close();
    }
    
    private boolean unclosedQuote;
    private boolean literal;
    
    private boolean isExpression(String exp) {
        for(int i = 0 ; i < exp.length() ; ++i)
            if ( isOperator(exp.charAt(i)) )
                return true;
        return false;
    }
    
    private boolean isOperator(char x) {
        return x == '-' || x == '*' || x == '+' || x == '/';
    }
    
    private StringandNext buildLiteral(int starting, String line) {
        StringBuilder ret = new StringBuilder("");
        ret.append(line.charAt(starting));
        ret.append(line.charAt(starting+1));
        ret.append(line.charAt(starting+2));
        int i;     
        for(i = starting+3 ; i < line.length() && line.charAt(i) != 39 ; i++)
            ret.append(line.charAt(i));
        if (i == line.length()) unclosedQuote = true;
        else {
            ret.append((char)39);
            i++;
        }
        return new StringandNext(ret.toString(), i, line);
    }

    private StringandNext buildtospace(int starting, String line) {
        StringBuilder ret = new StringBuilder("");
        int i;
        for (i = starting; i < line.length(); i++) {
            if (line.charAt(i) <= 32)
                break;
            ret.append(line.charAt(i));
        }
        return new StringandNext(ret.toString(), i, line);
    }

    private StringandNext buildwithquote(int starting, String line) {
        StringBuilder ret = new StringBuilder("");
        ret.append(line.charAt(starting));
        ret.append(line.charAt(starting + 1));
        int i, quotes = 0;
        boolean flag = false;
        for (i = starting + 2; i < line.length(); i++)
            if (line.charAt(i) == 39) {
                quotes++;
                flag = true;
            }
        if (line.charAt(starting) == 'x' || line.charAt(starting) == 'X')
            flag = false;
        for (i = starting + 2; i < line.length(); i++) {
            ret.append(line.charAt(i));
            if ( quotes == 0 && i + 1 < line.length() 
                    && line.charAt(i+1) <= 32 
                    && labels.contains(ret.toString()) )
                return new StringandNext(ret.toString(),i+1,line);
            if (!flag && line.charAt(i) == 39)
                return new StringandNext(ret.toString(), i + 1, line);
            if (line.charAt(i) == 39)
                quotes--;
            if (quotes == 0 && flag)
                return new StringandNext(ret.toString(), i + 1, line);
        }
        unclosedQuote = true;
        return new StringandNext(ret.toString(), i, line);
    }

    private String buildComment(int starting, String line) {
        StringBuilder ret = new StringBuilder("");
        for (int i = starting; i < line.length(); i++)
            ret.append(line.charAt(i));
        return ret.toString();
    }

    private boolean isLiteral(int idx, String CC) {
        if(idx + 2 >= CC.length()) return false;
        StringBuilder build = new StringBuilder("");
        build.append(CC.charAt(idx));
        build.append(CC.charAt(idx+1));
        build.append(CC.charAt(idx+2));
        String ret = build.toString();
        return ret.equals("=x'") || ret.equals("=X'") || ret.equals("=c'")
                || ret.equals("=C'");
    }
    
    private boolean is_C_or_X(int idx, String CC) {
        if (idx + 1 >= CC.length())
            return false;
        StringBuilder tt = new StringBuilder("");
        tt.append(CC.charAt(idx));
        tt.append(CC.charAt(idx + 1));
        String C = tt.toString();
        return C.equals("C'") || C.equals("c'") || C.equals("x'") 
                || C.equals("X'");
    }

    private class StringandNext {

        public String str;
        public int next;

        public StringandNext(String strx, int next, String str) {
            this.str = strx;
            while (next < str.length() && str.charAt(next) <= 32)
                next++;
            this.next = next;
        }
    }
}
