package sicasm;

import java.util.ArrayList;

/**
 *
 * @author Ahmed Atef
 */
public class SymbolTable {

    private final ArrayList<Symbol> symbolList;
    
    public SymbolTable() {
        symbolList = new ArrayList<>();
    }
    
    public void add(String label, int addressLocation) {
        symbolList.add(new Symbol(label, addressLocation));
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
    
}
