package io.github.coderodde.regex.parser.ast.tokens;

import io.github.coderodde.regex.CodePointRange;
import io.github.coderodde.regex.parser.ast.RegexTokenType;
import java.util.List;

/**
 * This interface defines the type for regex tokens.
 */
public sealed interface RegexToken permits RegexTokenLiteral,
                                           RegexTokenCharacterClass,
                                           RegexTokenSimple {
    
    public RegexTokenType type();
    
    public default int codePoint() {
        throw new UnsupportedOperationException("codePoint() not supported.");
    }
    
    public default List<CodePointRange> ranges() {
        throw new UnsupportedOperationException("ranges() not supported.");
    }
}