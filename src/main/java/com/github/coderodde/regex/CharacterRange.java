package com.github.coderodde.regex;

/**
 *
 * @author Rodin "rodde" Efremov
 * @version 1.6 (Dec 10, 2023)
 * @since 1.6 (Dec 10, 2023)
 */
final class CharacterRange implements Comparable<CharacterRange> {
    
    private final char minimumCharacter;
    private final char maximumCharacter;
    
    CharacterRange(char minimumCharacter, char maximumCharacter) {
        this.minimumCharacter = minimumCharacter;
        this.maximumCharacter = maximumCharacter;
    }
    
    CharacterRange(char character) {
        this(character, character);
    }
    
    char getMinimumCharacter() {
        return minimumCharacter;
    }
    
    char getMaximumCharacter() {
        return maximumCharacter;
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
}
