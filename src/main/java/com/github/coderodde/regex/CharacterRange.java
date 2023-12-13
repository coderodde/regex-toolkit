package com.github.coderodde.regex;

/**
 *
 * @author Rodin "rodde" Efremov
 * @version 1.6 (Dec 10, 2023)
 * @since 1.6 (Dec 10, 2023)
 */
final class CharacterRange implements Comparable<CharacterRange> {
    
    private char minimumCharacter;
    private char maximumCharacter;
    
    CharacterRange(char minimumCharacter, char maximumCharacter) {
        this.minimumCharacter = minimumCharacter;
        this.maximumCharacter = maximumCharacter;
    }
    
    CharacterRange(char character) {
        this(character, character);
    }
    
    CharacterRange() {
        this('\u0000');
    }
    
    char getMinimumCharacter() {
        return minimumCharacter;
    }
    
    char getMaximumCharacter() {
        return maximumCharacter;
    }
    
    void setMinimumCharacter(Character minimumCharacter) {
        this.minimumCharacter = minimumCharacter;
    }
    
    void setMaximumCharacter(Character maximumCharacter) {
        this.maximumCharacter = maximumCharacter;
    }
    
    boolean characterIsWithinRange(char character) {
        return minimumCharacter <= character && character <= maximumCharacter;
    }
    
    boolean characterRangeSmallerThan(char character) {
        return maximumCharacter < character;
    }
    
    boolean characterRangeGreaterThan(char character) {
        return character < minimumCharacter;
    }
    
    @Override
    public boolean equals(Object o) {
        CharacterRange other = (CharacterRange) o;
        return this.minimumCharacter == other.minimumCharacter &&
               this.maximumCharacter == other.maximumCharacter;
    }
    
    @Override
    public int compareTo(CharacterRange other) {
        if (this.maximumCharacter < other.minimumCharacter) {
            return -1;
        }
        
        if (this.minimumCharacter > other.maximumCharacter) {
            return 1;
        }
        
        return 0;
    }
    
    @Override
    public String toString() {
        return "[CharacterRange: " 
                + minimumCharacter
                + " to "
                + maximumCharacter 
                + "]";
    }
}
