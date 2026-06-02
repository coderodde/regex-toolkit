package io.github.coderodde.regex.parser.ast.tokens;

import io.github.coderodde.regex.parser.ast.RegexTokenType;

/**
 * Represents a simple regex token with a single code point.
 */
public final class RegexTokenLiteral implements RegexToken {

    private final int codePoint;
    
    public RegexTokenLiteral(int codePoint) {
        this.codePoint = codePoint;
    }
    
    @Override
    public RegexTokenType type() {
        return RegexTokenType.LITERAL;
    }

    @Override
    public int codePoint() {
        return codePoint;
    }
}
