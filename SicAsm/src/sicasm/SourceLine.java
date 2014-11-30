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
        comment = String.format(
                (".%-" + (labelMaxLength - 1) + "s  " + 
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
    
    public ArrayList<Constants.Errors> getErrorsList() {
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
    
    public static int getLabelMaxLength() {
        return labelMaxLength;
    }

    public static int getMnemonicMaxLength() {
        return mnemonicMaxLength;
    }

    public static int getOperandMaxLength() {
        return operandMaxLength;
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
}
