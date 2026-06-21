package io.github.coderodde.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
    
    boolean includes(CodePointRange other) {
        
        if (negated) {   
            CodePointRangePair cprp = negate(this);   
            CodePointRange thisLo = cprp.lo();
            CodePointRange thisHi = cprp.hi();
            
            if (other.negated) {
                CodePointRangePair cprp2 = negate(other);
                CodePointRange otherLo = cprp2.lo();
                CodePointRange otherHi = cprp2.hi();
                
                return otherLo.getMaximumCodePoint() <
                        thisLo.getMinimumCodePoint()
                        && 
                        otherHi.getMinimumCodePoint() > 
                         thisHi.getMaximumCodePoint();
            } else {
                return (thisLo.getMaximumCodePoint() >= 
                         other.getMaximumCodePoint() && 
                        thisLo.getMinimumCodePoint() <= 
                         other.getMinimumCodePoint()) 
                        ||
                       (thisHi.getMaximumCodePoint() >=
                         other.getMaximumCodePoint() && 
                        thisHi.getMinimumCodePoint() <= 
                         other.getMinimumCodePoint());
            }
        } else {
            if (other.negated) {
                CodePointRangePair cprp = negate(other);
                CodePointRange otherLo = cprp.lo();
                CodePointRange otherHi = cprp.hi();
                
                return (otherLo.getMinimumCodePoint() <= 
                                   minimumCodePoint && 
                        otherLo.getMaximumCodePoint() >= 
                                   maximumCodePoint)
                        ||
                       (otherHi.getMinimumCodePoint() <= 
                                   minimumCodePoint && 
                        otherHi.getMaximumCodePoint() >= 
                                   maximumCodePoint);
            } else {
                return minimumCodePoint <= other.minimumCodePoint &&
                       maximumCodePoint >= other.maximumCodePoint;
            }
        }
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
        if (o instanceof CodePointRange other) {
            return minimumCodePoint == other.minimumCodePoint &&
                   maximumCodePoint == other.maximumCodePoint;
        }
            
        return false;
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
        if (negated) {
            return "[From '\\u0000' to '" 
                    + new String(Character.toChars(minimumCodePoint - 1)) 
                    + "' and from '"
                    + new String(Character.toChars(maximumCodePoint + 1))
                    + "' to '\\u10FFFF']";
        }
        
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
        List<CodePointRange> winners = new ArrayList<>();
        
        outer:
        for (int i = 0; i < ranges.length; ++i) {
            CodePointRange candidate = ranges[i];
            
            for (int j = i + 1; j < ranges.length; ++j) {
                CodePointRange testRange = ranges[j];
                
                if (testRange.includes(candidate)) {
                    continue outer;
                }
            }
            
            winners.add(candidate);
        }
        
        winners.sort(CodePointRange::compareTo);
        return winners.toArray(CodePointRange[]::new);
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
