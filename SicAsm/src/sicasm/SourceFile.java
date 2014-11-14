package sicasm;

import java.io.*;
import java.util.*;

public class SourceFile {

    public ArrayList<SourceLine> getTokenz() {
        return new ArrayList<SourceLine>(file);
    }
    private BufferedReader in;

    public SourceFile(String filename) throws FileNotFoundException, IOException {
        in = new BufferedReader(new FileReader(filename));
        file = new ArrayList<SourceLine>();
        load();
    }
    private ArrayList<SourceLine> file;

    private void load() throws IOException {
        String input;
        String label, mnemonic, comment, operand;
        StringandNext temp;
        while ((input = in.readLine()) != null) {
            label = mnemonic = comment = operand = "";
            unclosedQuote = false;
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
            if (is_C_or_X(i, input))
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
            if (unclosedQuote)
                AXX.addError(Constants.Errors.UNCLOSED_QUOTE);
            file.add(AXX);
        }
    }
    private boolean unclosedQuote;

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
        for (i = starting + 2; i < line.length(); i++) {
            ret.append(line.charAt(i));
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

    private boolean is_C_or_X(int idx, String CC) {
        if (idx + 1 >= CC.length())
            return false;
        StringBuilder tt = new StringBuilder("");
        tt.append(CC.charAt(idx));
        tt.append(CC.charAt(idx + 1));
        String C = tt.toString();
        return C.equals("C'") || C.equals("c'") || C.equals("x'") || C.equals("X'");
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
