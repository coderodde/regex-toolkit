package com.github.coderodde.regex;

import java.util.Objects;

/**
 * This class specifies the regular expression token type.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 15, 2023)
 * @since 1.6 (Nov 15, 2023)
 */
public final class RegexToken {
    private final RegexTokenType tokenType;
    private final Character character;
    
    public RegexToken(RegexTokenType tokenType, Character character) {
        this.tokenType = tokenType;
        this.character = character;
    }
    
    public RegexToken(RegexTokenType tokenType) {
        this.tokenType = tokenType;
        this.character = null;
    }
    
    public RegexTokenType getTokenType() {
        return tokenType;
    }
    
    public Character getCharacter() {
        return character;
    }
    
    // Used for more conventient debugging.
    @Override
    public String toString() {
        switch (tokenType) {
            case CHARACTER:
                return "" + character;
                
            case CONCAT:
                return "o";
                
            case KLEENE_STAR:
                return "*";
                
            case PLUS:
                return "+";
                
            case LEFT_PARENTHESIS:
                return "(";
                
            case RIGHT_PARENTHESIS:
                return ")";
                
            case UNION:
                return "|";
                
            case DOT:
                return ".";
                
            default:
                throw new IllegalStateException("Unknown token type.");
        }
    }
    
    @Override
    public boolean equals(Object o) {
        RegexToken other = (RegexToken) o;
        return tokenType.equals(other.tokenType) 
            && Objects.equals(character, other.character);
    }
}