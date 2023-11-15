package com.github.coderodde.regex;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 12, 2023)
 * @since 1.6 (Nov 12, 2023)
 */
public class RegexCharacter {
    
    public enum RegexCharacterType {
        EPSILON           ("ϵ"),
        CHARACTER         (""),
        QUESTION_MARK     ("?"),
        
        KLEENE_STAR       ("*"),
        PLUS              ("+"),
        CONCATENATION     (""),
        
        CHOICE            ("|"),
        LEFT_PARENTHESIS  ("("),
        RIGHT_PARENTHESIS (")");
        
        private final String name;
        
        private RegexCharacterType(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    private Character actualCharacter;
    private RegexCharacterType regexCharacterType;
    
    public RegexCharacter(RegexCharacterType regexCharacterType) {
        this.regexCharacterType = regexCharacterType;
    }
    
    @Override
    public String toString() {
        switch (regexCharacterType) {
            case CHARACTER:
                return "[char = " + actualCharacter + "]";
                
            case CHOICE:
                return "|";
                
            case CONCATENATION:
                return "+";
                
            case EPSILON:
                return "ϵ";
                
            case KLEENE_STAR:
                return "*";
                
            case LEFT_PARENTHESIS:
                return "(";
                
            case RIGHT_PARENTHESIS:
                return ")";
                
            case PLUS:
                return "+";
                
            case QUESTION_MARK:
                return "?";
                
            default:
                throw new IllegalStateException("Unknown regex token type.");
        }
    }
    
    public Character getCharacter() {
        return actualCharacter;
    }
    
    public RegexCharacterType getRegexCharacterType() {
        return regexCharacterType;
    }
    
    public void setCharacter(Character character) {
        this.actualCharacter = character;
    }
    
    public void setRegexCharacterType(RegexCharacterType regexCharacterType) {
        this.regexCharacterType = regexCharacterType;
    }
}
