package io.github.coderodde.regex.parser.ast;

import io.github.coderodde.regex.parser.ast.tokens.RegexToken;
import java.util.List;

/**
 * This record contains the tokenization results.
 */
public final record RegexTokenizationResult(List<RegexToken> tokens,
                                            boolean anchoredAtStart,
                                            boolean anchoredAtEnd) {
    
}
