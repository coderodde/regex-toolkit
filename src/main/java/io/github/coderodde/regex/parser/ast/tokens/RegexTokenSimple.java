package io.github.coderodde.regex.parser.ast.tokens;

import io.github.coderodde.regex.parser.ast.RegexTokenType;

/**
 * This record represents all the regex tokens that do not "take parameters".
 */
public final class RegexTokenSimple implements RegexToken {

    private final RegexTokenType type;
    
    public RegexTokenSimple(RegexTokenType type) {
        this.type = type;
    }
    
    @Override
    public RegexTokenType type() {
        return type;
    }
    
    @Override
    public boolean equals(Object o) {
        return type == ((RegexTokenSimple) o).type;
    }
}
