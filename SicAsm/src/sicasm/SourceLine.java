package sicasm;

import java.util.ArrayList;

public class SourceLine {
    private String label, 
                   mnemonic, 
                   operand, 
                   comment,
                   objectCode;
    private int addressLocation,
                errors;
    private boolean isLineComment;
    
    private static int labelMaxLength, 
                       mnemonicMaxLength, 
                       operandMaxLength;
    
    public SourceLine(String label, String mnemonic, String operand, 
                      String comment) {
        this.label = label;
        this.mnemonic = mnemonic;
        this.operand = operand;
        this.comment = comment;
        isLineComment = false;
        labelMaxLength = Math.max(labelMaxLength, label.length());
        mnemonicMaxLength = Math.max(mnemonicMaxLength, mnemonic.length());
        operandMaxLength = Math.max(operandMaxLength, operand.length());
    }
    
    public SourceLine(String comment) {
        this.comment = comment;
        isLineComment = true;
    }

    public String getLabel() {
        return label;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public String getOperand() {
        return operand;
    }

    public String getComment() {
        return comment;
    }

    public void setAddressLocation(int addressLocation) {
        this.addressLocation = addressLocation;
    }

    public int getAddressLocation() {
        return addressLocation;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    public String getObjectCode() {
        return objectCode;
    }  

    public boolean isLineComment() {
        return isLineComment;
    }
    
    public void convertToLineComment() {
        isLineComment = true;
        // Note 'labelMaxLength' here is followed only by 1 space to compensate 
        // the shifting made by the dot, and keep the columns aligned.
        comment = String.format(
                (".%-" + labelMaxLength + "s " + 
                 "%-" + mnemonicMaxLength + "s  " + 
                 "%-" + operandMaxLength + "s   " + 
                 "%s"),
                label, 
                mnemonic,
                operand,
                comment);
    }
    
    public void addError(Constants.Errors error) {
        errors |= 1 << error.ordinal();
    }
    
    private ArrayList<Constants.Errors> getErrorsList() {
        if (errors == 0) {
            return null;
        }
        ArrayList<Constants.Errors> errorMessages = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            if ((errors & (1 << i)) != 0) {
                errorMessages.add(Constants.Errors.values()[i]);                        
            }
        }
        return errorMessages;
    }  
    
    public boolean containsErrors() {
        return errors != 0;
    }
    
    /**
     * Max length variables are static and needed to be reset whenever a new 
     * SourceFile instance is made.
     */
    public static void resetMaxLength() {
        labelMaxLength = 0;
        mnemonicMaxLength = 0;
        operandMaxLength = 0;
    }
    
    /**
     * 
     * @return StringBuilder of one or more lines.
     * Object code will be divided 6 hex digits per line.
     * Errors will be concatenated at the end of the StringBuilder, each on a 
     * separate line.
     */
    public StringBuilder getListFileLine() {
        if (isLineComment()) {
            return new StringBuilder("            " + comment + "\n");
        }
     
        String locationAddress;
        if (addressLocation < 0x8000) {
            locationAddress = String.format("%04X", addressLocation);
        } else {
            locationAddress = Constants.getRandomSymbols();
        }

        int it = 0;
        ArrayList<Constants.Errors> errorsList = getErrorsList();
        String subObjectCode;
        if (errorsList == null) {
            subObjectCode = objectCode.substring(
                    it, it = Math.min(it + 6, objectCode.length()));
        } else {
            subObjectCode = "";
            objectCode = "";
        }
        
        StringBuilder line = new StringBuilder("");
        
        line.append(String.format((
                "%s %-6s " +                 
                "%-" + labelMaxLength + "s  " + 
                "%-" + mnemonicMaxLength + "s  " + 
                "%-" + operandMaxLength + "s   " + 
                "%s\n"),                     
                locationAddress,    
                subObjectCode,
                label, 
                mnemonic,
                operand,
                comment));
             
        while(it < objectCode.length()) {
             subObjectCode = objectCode.substring(
                     it, it = Math.min(it + 6, objectCode.length()));   
             line.append("     ");
             line.append(subObjectCode);
             line.append("\n");
        }
        if (errorsList != null) {
            for (int i = 0; i < errorsList.size(); ++i) {
                line.append("  **** ");
                line.append(Constants.ErrorMessages.get(errorsList.get(i)));
                line.append(". ****\n");
            }
        }
        return line;
    }
}
