package sicasm;

/**
 *
 * @author Ahmed Atef
 */
public class Block {

    String name;
    int index;
    int defaultlocation;
    int locationCounter;

    public Block(String name, int index, int locationCounter) {
        this.name = name;
        this.index = index;
        this.locationCounter = locationCounter;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public int getLocationCounter() {
        return locationCounter;
    }

    public void setLocationCounter(int locationCounter) {
        this.locationCounter = locationCounter;
    }

    /**
     * Will be needed to set the name of the starting block.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
}
