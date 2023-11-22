package com.github.coderodde.regex;

/**
 * This enumeration enumerates all supported token types.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 15, 2023)
 * @since 1.6 (Nov 15, 2023)
 */
public enum RegexTokenType {
    KLEENE_STAR,
    PLUS,
    QUESTION,
    DOT,
    UNION,
    CONCAT,
    CHARACTER,
    EPSILON,
    LEFT_PARENTHESIS,
    RIGHT_PARENTHESIS;
}
