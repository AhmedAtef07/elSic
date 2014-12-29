package sicasm;

public class Symbol {
    
    public enum Type {
        RELATIVE,
        ABSOLUTE,
    }
    
    private final String label;
    private final int addressLoction;
    private final int blockIndex;
    private final Type type;

    public Symbol(String label, int addressLoction) {
        this.label = label;
        this.addressLoction = addressLoction;
        this.blockIndex = 0;
        this.type = Type.RELATIVE;
    }

    /**
     *
     * @param label
     * @param addressLoction
     * @param blockIndex block in which the label will be declared, setting the
     * label type to absolute if index is less than 0.
     */
    public Symbol(String label, int addressLoction, int blockIndex) {
        this.label = label;
        this.addressLoction = addressLoction;
        if (blockIndex < 0) {
            this.blockIndex = -1;            
            this.type = Type.ABSOLUTE;            
        } else {
            this.blockIndex = blockIndex;
            this.type = Type.RELATIVE;            
        }
    }
    
    public String getLabel() {
        return label;
    }

    public int getAddressLoction() {
        return addressLoction;
    }

    public int getBlockIndex() {
        return blockIndex;
    }

    public Type getType() {
        return type;
    }
}
