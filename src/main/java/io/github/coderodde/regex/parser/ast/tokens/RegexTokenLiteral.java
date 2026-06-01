package io.github.coderodde.regex.parser.ast.tokens;

/**
 * Represents a simple regex token with a single code point.
 */
public record RegexTokenLiteral(int codePoint) implements RegexToken {
    
}
