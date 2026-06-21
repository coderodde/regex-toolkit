package io.github.coderodde.regex;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class implements a simple code point range.
 */
public final class CodePointRange implements Comparable<CodePointRange>,
                                             Iterable<Integer> {
    
    private final boolean negated;
    private int minimumCodePoint;
    private int maximumCodePoint;
    
    public CodePointRange(boolean negated,
                          int minimumCharacter, 
                          int maximumCharacter) {
        this.negated          = negated;
        this.minimumCodePoint = minimumCharacter;
        this.maximumCodePoint = maximumCharacter;
    }
    
    public CodePointRange(int minimumCharacter,
                          int maximumCharacter) {
        this(false, minimumCharacter, maximumCharacter);
    }
    
    public CodePointRange(int character) {
        this(false, character, character);
    }
    
    public CodePointRange() {
        this(0);
    }
    
    boolean isNegated() {
        return negated;
    }
    
    boolean isSingleCodePoint() {
        return minimumCodePoint == maximumCodePoint;
    }
    
    int getMinimumCodePoint() {
        return minimumCodePoint;
    }
    
    int getMaximumCodePoint() {
        return maximumCodePoint;
    }
    
    boolean codePointIsWithinRange(int codePoint) {
        boolean withinRange = minimumCodePoint <= codePoint &&
                               codePoint <= maximumCodePoint;
        
        return negated ? (!withinRange) : withinRange;
    }
    
    boolean codePointRangeSmallerThan(int codePoint) {
        return maximumCodePoint < codePoint;
    }
    
    public void setMinimumCodePoint(int minimumCodePoint) {
        this.minimumCodePoint = minimumCodePoint;
    }
    
    public void setMaximumCodePoint(int maximumCodePoint) {
        this.maximumCodePoint = maximumCodePoint;
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
    
    public static CodePointRangePair negate(CodePointRange range) {
        if (!range.isNegated()) {
            throw new IllegalArgumentException(
                "The input range is expected to be negated.");
        }
        
        CodePointRange lo = new CodePointRange(0,
                                               range.getMinimumCodePoint() - 1);
        
        CodePointRange hi = new CodePointRange(range.getMaximumCodePoint() + 1,
                                               Character.MAX_CODE_POINT);
        
        return new CodePointRangePair(lo, hi);
    }
    
    public static CodePointRange[] combine(CodePointRange... ranges) {
        Set<CodePointRange> singletonRanges    = new HashSet<>();
        Set<CodePointRange> nonSingletonRanges = new HashSet<>();
        
        for (CodePointRange range : ranges) {
            if (range.isSingleCodePoint()) {
                singletonRanges.add(range);
            } else {
                nonSingletonRanges.add(range);
            }
        }
        
        Iterator<CodePointRange> it = singletonRanges.iterator();
        
        while (it.hasNext()) {
            CodePointRange range = it.next();
            
            for (CodePointRange nonSingleton : nonSingletonRanges) {
                if (nonSingleton.codePointIsWithinRange(
                    range.getMinimumCodePoint())) {
                    
                    it.remove();
                }
            }
        }
        
        
        
        return null;
    }
    
    public final record CodePointRangePair(CodePointRange lo,
                                           CodePointRange hi) {
        
        public CodePointRangePair(CodePointRange lo,
                                  CodePointRange hi) {
            this.lo = lo;
            this.hi = hi;
        }
    }
}
