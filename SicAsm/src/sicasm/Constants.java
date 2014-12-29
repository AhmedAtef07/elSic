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
    
    public enum Target {
        LINE, LABEL, MNEMONIC, OPERAND;
    }
    
    public enum Errors {
        // Errors will be printed in order of decleration.
        // Line errors.
        UNNAMED_PROGRAM (Target.LINE, 
            "Program must have a name (label must not be empty)"),
        DUPLICATE_START (Target.LINE,
            "Start statment must exist once and in the first line"),
        MISSING_START (Target.LINE, 
            "Program must begin with start statment"),
        MISSING_EQUATE_LABEL (Target.LINE, 
            "Equate label must not be empty"),
        MISSING_MNEMONIC (Target.LINE, 
            "Can not find the mnemonic") ,
        MISSING_OPERAND (Target.LINE, 
            "Can not find the operand"),
        
        // Label errors.
        INVALID_LABEL_REPRESENTATION (Target.LABEL, 
            ("Invalid label represenation (must only contain " + 
                    "[A-Za-z0-9_] except first char without [0-9]")),
        DUPLICATE_LABEL (Target.LABEL, 
            "Dupliacted labels are not allowed"),
        
        // Menomonic errors.
        UNRECOGNIZED_MNEMONIC (Target.MNEMONIC, 
            "Unrecognized menmonic"),
        
        // Operand errors.
        ARITHMETIC_OVERFLOW (Target.OPERAND, 
            "Location counter exceeded memory size (2^15)"),
        INVALID_ADDRESS_LOCATION (Target.OPERAND, 
            "Address location out of range (must be less than 0xFFFF)"),
        INVALID_BYTE_OPERAND (Target.OPERAND,
            "Invalid byte operand"),
        INVALID_HEX (Target.OPERAND, 
            "Expected valid hexadecimal number in operand"),
        INVALID_HEX_REPRESENTATION (Target.OPERAND, 
            "Number of hex digits must be even"),
        INVALID_LITERAL (Target.OPERAND, 
            "Invalid literal, accept (decimal)\"=970\", (hexdecimal)"
                    + "\"=x'A793D'\" or (string)\"=c'Ahmed Atef'\""),
        INVALID_OPERAND (Target.OPERAND,
            "Invalid operand"),
        INVALID_RESERVE_OPERAND (Target.OPERAND, 
            ("Invalid reserve operand (operand lead to arthimatic overflow)")),
        INVALID_START_ADDRESS (Target.OPERAND, 
            "Start address must be a hexadecimal number"),
        INVALID_WORD_OPERAND (Target.OPERAND,
            "Word operand must be a decimal integer"),
        UNCLOSED_QUOTE (Target.OPERAND, 
            "Can not find closing quote for byte operand"),
        UNDEFINED_LABEL (Target.OPERAND, 
            "Undefined label in operand") ,
        WORD_OPERAND_OUT_OF_RANGE (Target.OPERAND, 
            "Operand value is out of range (must be less than 2 ^ 23)"),
        
        // Expression Errors.
        EXPRESSION_LABEL_UNDEFINED (Target.OPERAND, 
            "Undefined label in expression"),
        EXPRESSION_ODD_RELATIVE_TERMS (Target.OPERAND, 
            "Unpaired relative terms in expression "
                    + "(terms must have opposite signs)"),
        EXPRESSION_INVALID_RELATIVE_OPERATOR (Target.OPERAND, 
            "Relative terms in expression must be preceded only by + or -"),
        EXPRESSION_ILLEGAL_START(Target.OPERAND, 
            "Expression must not start with * or /"),
        EXPRESSION_ILLEGAL_END (Target.OPERAND, 
            "Expression must not end with an operator (+, -, * or /)"),
        EXPRESSION_ZERO_DIVISION (Target.OPERAND, 
            "Division by zero detected in expression"),
        EXPRESSION_INVALID_UNARY_OPERATOR (Target.OPERAND, 
            "Invalid unary operator in expression "
                    + "(+ or - can not be followed by * or /)");
        
        private final Target target;
        private final String message;
        
        private Errors(Target target, String message) {
            this.target = target;
            this.message = message;
        }

        public Target getTarget() {
            return target;
        }   

        public String getMessage() {
            return message;
        } 
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
