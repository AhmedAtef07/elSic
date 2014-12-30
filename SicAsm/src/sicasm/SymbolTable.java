package sicasm;

import java.util.ArrayList;

/**
 *
 * @author Ahmed Atef
 */
public class SymbolTable {

    private final ArrayList<Symbol> symbolList;
    private int currentBlock;
    private final ArrayList<Block> blocks;
    
    public SymbolTable() {
        symbolList = new ArrayList<>();
        blocks = new ArrayList<>();
    }
    
    /**
     *
     * @param blockName
     * @return integer location counter to the selected block.
     */
    public int setBlock(String blockName) {
        for (Block block: blocks) {
            if (block.getName().equals(blockName)) {
                currentBlock = block.getIndex();
                return block.getLocationCounter();
            }
        }        
        currentBlock = blocks.size();
        blocks.add(new Block(blockName, blocks.size(), 0));   
        return 0;
    }
    
    /**
     *
     * @return integer location counter to the default block of index 0.
     */
    public int setDefaultBlock() {
        currentBlock = 0;
        return blocks.get(0).getLocationCounter();
    }
    
    public void add(String label, int addressLocation) {
        symbolList.add(new Symbol(label, addressLocation, currentBlock));
    }

    public void add(String label, int addressLocation, int blockIndex) {
        symbolList.add(new Symbol(label, addressLocation, blockIndex));
    }    
    
    public Symbol get(int index) {
        return symbolList.get(index);
    }
    
    /**
     * Searches the label in each symbol in the symbol table.
     * @param label the label to be searched for in the symbol table.
     * @return true in case the label is predefined (case sensitive), 
     * false otherwise.
     */
    public boolean containsLabel(String label) {
        for (Symbol symbol : symbolList) {
            if (symbol.getLabel().equals(label)) {
                return true;
            }
        }
        return false;
    }
    
    public int getAddressLocation(String label) {
        for (Symbol symbol : symbolList) {
            if (symbol.getLabel().equals(label)) {
                return symbol.getAddressLoction();
            }
        }
        return -1;
    }
    
    public void setLoctionCounter(int newLocationCounter) {
        blocks.get(currentBlock).setLocationCounter(newLocationCounter);
    }
    
    public void addToLoctionCounter(int addition) {
        blocks.get(currentBlock).setLocationCounter(
                blocks.get(currentBlock).getLocationCounter() + addition);
    }
    
    /**
     *
     * @return Location counter of the current block.
     */
    public int getLocationCounter() {
        return blocks.get(currentBlock).getLocationCounter();
    }
    
    public void setStartBlock(String name, int startLocation) {
        blocks.get(0).setName(name);
        blocks.get(0).setLocationCounter(startLocation);        
    }

    public int getCurrentBlock() {
        return currentBlock;
    }
}
