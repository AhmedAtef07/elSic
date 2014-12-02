package sicasm;

import java.util.*;

public final class Constants {

    public static final TreeMap<String, Integer> OpTable = new TreeMap<>();

    static {        
        OpTable.put("ADD", 0x18);
        OpTable.put("ADDF", 0x58);
        OpTable.put("ADDR", 0x90);
        OpTable.put("AND", 0x40);
        OpTable.put("CLEAR", 0xB4);
        OpTable.put("COMP", 0x28);
        OpTable.put("COMPF", 0x88);
        OpTable.put("COMPR", 0xA0);
        OpTable.put("DIV", 0x24);
        OpTable.put("DIVF", 0x64);
        OpTable.put("DIVR", 0x9C);
        OpTable.put("FIX", 0xC4);
        OpTable.put("FLOAT", 0xC0);
        OpTable.put("HIO", 0xF4);
        OpTable.put("J", 0x3C);
        OpTable.put("JEQ", 0x30);
        OpTable.put("JGT", 0x34);
        OpTable.put("JLT", 0x38);
        OpTable.put("JSUB", 0x48);
        OpTable.put("LDA", 0x00);
        OpTable.put("LDB", 0x68);
        OpTable.put("LDCH", 0x50);
        OpTable.put("LDF", 0x70);
        OpTable.put("LDL", 0x08);
        OpTable.put("LDS", 0x6C);
        OpTable.put("LDT", 0x74);
        OpTable.put("LDX", 0x04);
        OpTable.put("LPS", 0xD0);
        OpTable.put("MUL", 0x20);
        OpTable.put("MULF", 0x60);
        OpTable.put("MULR", 0x98);
        OpTable.put("NORM", 0xC8);
        OpTable.put("OR", 0x44);
        OpTable.put("RD", 0xD8);
        OpTable.put("RMO", 0xAC);
        OpTable.put("RSUB", 0x4C);
        OpTable.put("SHIFTL", 0xA4);
        OpTable.put("SHIFTR", 0xA8);
        OpTable.put("SIO", 0xF0);
        OpTable.put("SSK", 0xEC);
        OpTable.put("STA", 0x0C);
        OpTable.put("STB", 0x78);
        OpTable.put("STCH", 0x54);
        OpTable.put("STF", 0x80);
        OpTable.put("STI", 0xD4);
        OpTable.put("STL", 0x14);
        OpTable.put("STS", 0x7C);
        OpTable.put("STSW", 0xE8);
        OpTable.put("STT", 0x84);
        OpTable.put("STX", 0x10);
        OpTable.put("SUB", 0x1C);
        OpTable.put("SUBF", 0x5C);
        OpTable.put("SUBR", 0x94);
        OpTable.put("SVC", 0xB0);
        OpTable.put("TD", 0xE0);
        OpTable.put("TIO", 0xF8);
        OpTable.put("TIX", 0x2C);
        OpTable.put("TIXR", 0xB8);
        OpTable.put("WD", 0xDC);
    }
    
    public enum Errors {
        // Sorted in alphabetical order. 
        ARITHMETIC_OVERFLOW,
        DUPLICATE_LABEL,
        DUPLICATE_START,
        INVALID_ADDRESS_LOCATION,
        INVALID_BYTE_OPERAND,
        INVALID_HEX,
        INVALID_HEX_REPRESENTATION,
        INVALID_LABEL_REPRESENTATION,
        INVALID_OPERAND,
        INVALID_RESERVE_OPERAND,
        INVALID_START_ADDRESS,
        INVALID_WORD_OPERAND,
        MISSING_MNEMONIC, 
        MISSING_OPERAND,
        MISSING_START,
        UNCLOSED_QUOTE,
        UNDEFINED_LABEL,  
        UNRECOGNIZED_MNEMONIC,
        WORD_OPERAND_OUT_OF_RANGE,
    }
    
    public static final TreeMap<Errors, String> ErrorMessages = new TreeMap<>();
    
    static {
        ErrorMessages.put(Errors.ARITHMETIC_OVERFLOW, 
                "Location counter exceeded memory size (2^15)");       
        ErrorMessages.put(Errors.DUPLICATE_LABEL, 
                "Dupliacted labels are not allowed");
        ErrorMessages.put(Errors.DUPLICATE_START, 
                "Start statment must exist once and in the first line");
        ErrorMessages.put(Errors.INVALID_ADDRESS_LOCATION, 
                "Address location out of range (must be less than 0xFFFF)");        
        ErrorMessages.put(Errors.INVALID_BYTE_OPERAND, 
                "Invalid byte operand");
        ErrorMessages.put(Errors.INVALID_HEX, 
                "Expected valid hexadecimal number in operand");
        ErrorMessages.put(Errors.INVALID_HEX_REPRESENTATION,
                "Number of hex digits must be even");
        ErrorMessages.put(Errors.INVALID_LABEL_REPRESENTATION,
                ("Invalid label represenation (must only contain " + 
                 "[A-Z][a-z][0-9][_] except first char without [0_9]"));        
        ErrorMessages.put(Errors.INVALID_OPERAND, 
                "Invalid operand");
        ErrorMessages.put(Errors.INVALID_RESERVE_OPERAND, 
                ("Invalid reserve operand " + 
                 "(operand lead to arthimatic overflow)"));        
        ErrorMessages.put(Errors.INVALID_START_ADDRESS, 
                "Start address must be a hexadecimal number");
        ErrorMessages.put(Errors.INVALID_WORD_OPERAND, 
                "Word operand must be a decimal integer");
        ErrorMessages.put(Errors.MISSING_MNEMONIC, 
                "Can not find the mnemonic");
        ErrorMessages.put(Errors.MISSING_OPERAND, 
                "Can not find the operand");
        ErrorMessages.put(Errors.MISSING_START, 
                "Program must begin with start statment");        
        ErrorMessages.put(Errors.UNCLOSED_QUOTE, 
                "Can not find closing quote for byte operand");
        ErrorMessages.put(Errors.UNDEFINED_LABEL, 
                "Undefined label in operand");
        ErrorMessages.put(Errors.UNRECOGNIZED_MNEMONIC, 
                "Unrecognized menmonic");
        ErrorMessages.put(Errors.WORD_OPERAND_OUT_OF_RANGE, 
                "Word operand is out of range (must be less than 2 ^ 23)");        
    }
    
    public static final String getRandomSymbols() {
         String symbols = "!@#$%^&*()";
         Random rand = new Random();
         String t = "";
         for (int i = 0; i < 4; i++) {
            t += symbols.charAt(rand.nextInt(symbols.length()));
        }
        return t;
    }
}
