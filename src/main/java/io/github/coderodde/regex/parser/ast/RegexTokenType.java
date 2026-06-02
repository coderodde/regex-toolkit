package io.github.coderodde.regex.parser.ast;

/**
 * This enumeration enumerates all supported token types.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 15, 2023)
 * @since 1.6 (Nov 15, 2023)
 */
public enum RegexTokenType {
    LITERAL,
    DOT,
    CONCATENATION,
    UNION,              // |
    KLEENE_STAR,        // *
    PLUS,               // +
    QUESTION,           // ?
    LEFT_PARENTHESIS,   // (
    RIGHT_PARENTHESIS,  // )
    CHARACTER_CLASS,    // [x-z]
    BEGIN_OF_LINE,      // ^
    END_OF_LINE,        // $
    EOF;
}
