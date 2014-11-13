package sicasm;

public class SourceLine {
    private String label, 
                   mnemonic, 
                   operand, 
                   comment,
                   objectCode;
    private int addressLocation,
                errors;
    private Boolean isLineComment;    
    
    public SourceLine(String label, String mnemonic, String operand, 
                      String comment) {
        this.label = label;
        this.mnemonic = mnemonic;
        this.operand = operand;
        this.comment = comment;
        isLineComment = false;
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

    public Boolean getIsLineComment() {
        return isLineComment;
    }
    
    public void addError(Constants.Errors error) {
        errors |= 1 << error.ordinal();
    }

    public int getErrors() {
        return errors;
    }    
}
