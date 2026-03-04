package com.github.coderodde.regex;

/**
 * This class implements a simple code point range.
 */
final class CodePointRange implements Comparable<CodePointRange> {
    
    private int minimumCodePoint;
    private int maximumCodePoint;
    
    CodePointRange(int minimumCharacter, int maximumCharacter) {
        this.minimumCodePoint = minimumCharacter;
        this.maximumCodePoint = maximumCharacter;
    }
    
    CodePointRange(int character) {
        this(character, character);
    }
    
    CodePointRange() {
        this(0);
    }
    
    int getMinimumCodePoint() {
        return minimumCodePoint;
    }
    
    int getMaximumCodePoint() {
        return maximumCodePoint;
    }
    
    void setMinimumCodePoint(int minimumCodePoint) {
        this.minimumCodePoint = minimumCodePoint;
    }
    
    void setMaximumCodePoint(int maximumCodePoint) {
        this.minimumCodePoint = maximumCodePoint;
    }
    
    boolean codePointIsWithinRange(int codePoint) {
        return minimumCodePoint <= codePoint && codePoint <= maximumCodePoint;
    }
    
    boolean codePointRangeSmallerThan(int codePoint) {
        return maximumCodePoint < codePoint;
    }
    
    boolean codePointRangeGreaterThan(int codePoint) {
        return codePoint < minimumCodePoint;
    }
    
    @Override
    public int compareTo(CodePointRange other) {
        if (this.maximumCodePoint < other.minimumCodePoint) {
            return -1;
        }
        
        if (this.minimumCodePoint > other.maximumCodePoint) {
            return 1;
        }
        
        return 0;
    }
    
    @Override
    public String toString() {
        return "[CharacterRange: " 
                + minimumCodePoint
                + " to "
                + maximumCodePoint 
                + "]";
    }
}
