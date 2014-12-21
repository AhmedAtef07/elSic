package sicasm;

/**
 *
 * @author Ahmed Atef
 */
public class Literal {

    String name;
    String value;
    String addressLocation;

    /**
     *
     * @param name is the operand starting with equal '='.
     * @param value is the hex decimal value as object code of literal.
     */
    public Literal(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getAddressLocation() {
        return addressLocation;
    }

    public void setAddressLocation(String addressLocation) {
        this.addressLocation = addressLocation;
    }    
}
