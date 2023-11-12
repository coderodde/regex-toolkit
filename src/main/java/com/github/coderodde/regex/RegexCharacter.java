package com.github.coderodde.regex;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 12, 2023)
 * @since 1.6 (Nov 12, 2023)
 */
public class RegexCharacter {
    
    public enum RegexCharacterType {
        EMPTY_STRING,
        CHARACTER,
        QUESTION_MARK,
        KLEENE_STAR,
        PLUS,
        CONCATENATION,
        CHOICE,
        LEFT_PARENTHESIS,
        RIGHT_PARENTHESIS;
    }
    
    private Character actualCharacter;
    private RegexCharacterType regexCharacterType;
    
    public RegexCharacter(RegexCharacterType regexCharacterType) {
        this.regexCharacterType = regexCharacterType;
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
