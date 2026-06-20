package io.github.coderodde.regex.parser.ast.tokens;

import io.github.coderodde.regex.CodePointRange;
import io.github.coderodde.regex.parser.ast.RegexTokenType;
import java.util.List;

/**
 * This class defines regex tokens representing character classes.
 */
public final class RegexTokenCharacterClass implements RegexToken {

    private final List<CodePointRange> ranges;
    private final boolean negated;
    
    public RegexTokenCharacterClass(List<CodePointRange> ranges,
                                    boolean negated) {
        this.ranges = List.copyOf(ranges);
        this.negated = negated;
    }
    
    public boolean isNegated() {
        return negated;
    }
    
    @Override
    public RegexTokenType type() {
        return RegexTokenType.CHARACTER_CLASS;
    }
    
    @Override
    public List<CodePointRange> ranges() {
        return ranges;
    }
    
    @Override
    public boolean equals(Object o) {
        List<CodePointRange> other = (List<CodePointRange>) o;
        return ranges.equals(other);
    }
    
    @Override
    public String toString() {
        return "[Character class: '" + ranges + "']";
    }
}
