package sicasm;

/**
 *
 * @author Ahmed Atef
 */
public class Literal {

    private final String name;
    private final String hexCode;
    private int addressLocation;
    private boolean used;

    /**
     *
     * @param name is the operand starting with equal '='.
     * @param hexCode is the hex decimal hexCode as object code of literal.
     */
    public Literal(String name, String hexCode) {
        this.name = name;
        this.hexCode = hexCode;
        used = false;
    }

    public String getName() {
        return name;
    }

    public String getHexCode() {
        return hexCode;
    }

    public int getAddressLocation() {
        return addressLocation;
    }

    public void setAddressLocation(int addressLocation) {
        this.addressLocation = addressLocation;
    }    
    
    public boolean isUsed() {
        return used;
    }

    /**
     * Set literal as used, used = true.
     */
    public void setAsUsed() {
        used = true;
    }
}
