package sicasm;

import java.util.*;

public final class Constants {

    public static final TreeMap<String, String> OpTable = new TreeMap<>();

    static {
        OpTable.put("ADD", "18");
        OpTable.put("ADDF", "58");
        OpTable.put("ADDR", "90");
        OpTable.put("AND", "40");
        OpTable.put("CLEAR", "B4");
        OpTable.put("COMP", "28");
        OpTable.put("COMPF", "88");
        OpTable.put("COMPR", "A0");
        OpTable.put("DIV", "24");
        OpTable.put("DIVF", "64");
        OpTable.put("DIVR", "9C");
        OpTable.put("FIX", "C4");
        OpTable.put("FLOAT", "C0");
        OpTable.put("HIO", "F4");
        OpTable.put("J", "3C");
        OpTable.put("JEQ", "30");
        OpTable.put("JGT", "34");
        OpTable.put("JLT", "38");
        OpTable.put("JSUB", "48");
        OpTable.put("LDA", "00");
        OpTable.put("LDB", "68");
        OpTable.put("LDCH", "50");
        OpTable.put("LDF", "70");
        OpTable.put("LDL", "08");
        OpTable.put("LDS", "6C");
        OpTable.put("LDT", "74");
        OpTable.put("LDX", "04");
        OpTable.put("LPS", "D0");
        OpTable.put("MUL", "20");
        OpTable.put("MULF", "60");
        OpTable.put("MULR", "98");
        OpTable.put("NORM", "C8");
        OpTable.put("OR", "44");
        OpTable.put("RD", "D8");
        OpTable.put("RMO", "AC");
        OpTable.put("RSUB", "4C");
        OpTable.put("SHIFTL", "A4");
        OpTable.put("SHIFTR", "A8");
        OpTable.put("SIO", "F0");
        OpTable.put("SSK", "EC");
        OpTable.put("STA", "0C");
        OpTable.put("STB", "78");
        OpTable.put("STCH", "54");
        OpTable.put("STF", "80");
        OpTable.put("STI", "D4");
        OpTable.put("STL", "14");
        OpTable.put("STS", "7C");
        OpTable.put("STSW", "E8");
        OpTable.put("STT", "84");
        OpTable.put("STX", "10");
        OpTable.put("SUB", "1C");
        OpTable.put("SUBF", "5C");
        OpTable.put("SUBR", "94");
        OpTable.put("SVC", "B0");
        OpTable.put("TD", "E0");
        OpTable.put("TIO", "F8");
        OpTable.put("TIX", "2C");
        OpTable.put("TIXR", "B8");
        OpTable.put("WD", "DC");
    }
    
    public static enum Errors {
        // Sorted in alphabetical order.       
        DUPLICATE_LABEL,
        DUPLICATE_START,
        ILLEGAL_HEX,
        ILLEGAL_OPERAND,
        MISSING_MNEMONIC, 
        MISSING_OPERAND,
        UNCLOSED_QUOTE,
        UNDEFINED_LABEL,  
        UNRECOGNIZED_MNEMONIC,
    }
    
    public static final TreeMap<Errors, String> ErrorMessages = new TreeMap<>();
    
    static {
        ErrorMessages.put(Errors.DUPLICATE_LABEL, 
                "Duplicate or misplaced start statement");
        ErrorMessages.put(Errors.DUPLICATE_START, "");
        ErrorMessages.put(Errors.ILLEGAL_HEX,
                "Number of hex digits must be even");
        ErrorMessages.put(Errors.ILLEGAL_OPERAND, "");
        ErrorMessages.put(Errors.MISSING_MNEMONIC, "");
        ErrorMessages.put(Errors.MISSING_OPERAND, "");
        ErrorMessages.put(Errors.UNCLOSED_QUOTE, "");
        ErrorMessages.put(Errors.UNDEFINED_LABEL, "");
        ErrorMessages.put(Errors.UNRECOGNIZED_MNEMONIC, "");
    }
}
