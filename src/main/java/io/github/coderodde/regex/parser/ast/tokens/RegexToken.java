package io.github.coderodde.regex.parser.ast.tokens;

/**
 * This interface defines the type for regex tokens.
 */
public sealed interface RegexToken permits RegexTokenLiteral,
                                           RegexTokenCharacterClass,
                                           RegexTokenSimple {
}