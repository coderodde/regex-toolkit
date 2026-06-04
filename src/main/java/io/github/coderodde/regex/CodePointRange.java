package io.github.coderodde.regex;

import java.util.Iterator;

/**
 * This class implements a simple code point range.
 */
public final class CodePointRange implements Comparable<CodePointRange>,
                                             Iterable<Integer> {
    
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
    public boolean equals(Object o) {
        CodePointRange other = (CodePointRange) o;
        return minimumCodePoint == other.minimumCodePoint &&
               maximumCodePoint == other.maximumCodePoint;
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
        String min = new String(Character.toChars(minimumCodePoint));
        String max = new String(Character.toChars(maximumCodePoint));
        
        return "[From '" + min + "' to '" + max + "']";
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator() {
            private int nextCodePoint = minimumCodePoint;
            
            @Override
            public boolean hasNext() {
                return nextCodePoint <= maximumCodePoint;
            }

            @Override
            public Object next() {
                return nextCodePoint++;
            }
        };
    }
}
