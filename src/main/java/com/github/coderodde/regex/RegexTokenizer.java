package com.github.coderodde.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class implements regex tokenizer. The idea behind tokenization is to
 * convert the regular expression into a list of {@link RegexToken} instances
 * representing the regular expression. We do this in order to conveniently deal
 * with the explicit concatenation operators that are not present in the regular
 * expressions.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 15, 2023)
 * @since 1.6 (Nov 15, 2023)
 */
public final class RegexTokenizer {
    
    private static final RegexToken REGEX_TOKEN_KLEENE_STAR;
    private static final RegexToken REGEX_TOKEN_PLUS;
    private static final RegexToken REGEX_TOKEN_QUESTION;
    private static final RegexToken REGEX_TOKEN_DOT;
    private static final RegexToken REGEX_TOKEN_UNION;
    private static final RegexToken REGEX_TOKEN_CONCAT;
    private static final RegexToken REGEX_TOKEN_LEFT_PARENTHESIS;
    private static final RegexToken REGEX_TOKEN_RIGHT_PARENTHESIS;
    
    static {
        REGEX_TOKEN_KLEENE_STAR = new RegexToken(RegexTokenType.KLEENE_STAR);
        REGEX_TOKEN_PLUS        = new RegexToken(RegexTokenType.PLUS);
        REGEX_TOKEN_QUESTION    = new RegexToken(RegexTokenType.QUESTION);
        REGEX_TOKEN_DOT         = new RegexToken(RegexTokenType.DOT);
        REGEX_TOKEN_UNION       = new RegexToken(RegexTokenType.UNION);
        REGEX_TOKEN_CONCAT      = new RegexToken(RegexTokenType.CONCAT);
        
        REGEX_TOKEN_LEFT_PARENTHESIS = 
            new RegexToken(RegexTokenType.LEFT_PARENTHESIS);
        
        REGEX_TOKEN_RIGHT_PARENTHESIS = 
            new RegexToken(RegexTokenType.RIGHT_PARENTHESIS);
    }
    
    /**
     * Converts the input regular expression into the list of {@link RegexToken}
     * objects encoding the same regular language as the input regular 
     * expression.
     * 
     * @param regex the regular expression to tokenize.
     * @return the list of {@link RegexToken} objects.
     */
    public List<RegexToken> tokenize(String regex) {
        Utils.validateRegularExpressionParentheses(
                Objects.requireNonNull(
                        regex, "The input regular expression is null.")
        );
        
        List<RegexToken> tokens = new ArrayList<>();
        char previousCharacter = '\0';
        
        for (int i = 0, n = regex.length(); i != n; i++) {
            char ch = regex.charAt(i);
            
            switch (ch) {
                case '*':
                    if (i == 0) {
                        throw new InvalidRegexException();
                    }
                    
                    tokens.add(REGEX_TOKEN_KLEENE_STAR);
                    break;
                    
                case '+':
                    if (i == 0) {
                        throw new InvalidRegexException();
                    }
                    
                    tokens.add(REGEX_TOKEN_PLUS);
                    break;
                    
                case '?':
                    if (i == 0) {
                        throw new InvalidRegexException();
                    }
                    
                    tokens.add(REGEX_TOKEN_QUESTION);
                    break;
                    
                case '.':
                    if (isTextCharacter(previousCharacter) // i == 0?
                            || previousCharacter == '*'
                            || previousCharacter == '+'
                            || previousCharacter == '?'
                            || previousCharacter == '.'
                            || previousCharacter == ')') {
                        tokens.add(REGEX_TOKEN_CONCAT);
                    }
                    
                    tokens.add(REGEX_TOKEN_DOT);
                    break;
                    
                case '|':
                    tokens.add(REGEX_TOKEN_UNION);
                    break;
                    
                case '(':
                    if (isTextCharacter(previousCharacter)
                            || previousCharacter == '*'
                            || previousCharacter == '+'
                            || previousCharacter == '.'
                            || previousCharacter == '?'
                            || previousCharacter == ')') {
                        tokens.add(REGEX_TOKEN_CONCAT);
                    }
                    
                    tokens.add(REGEX_TOKEN_LEFT_PARENTHESIS);
                    break;
                    
                case ')':
                    tokens.add(REGEX_TOKEN_RIGHT_PARENTHESIS);
                    break;
                    
                default:
                    // Once here, the ch is an alphabet character:
                    if (isTextCharacter(previousCharacter)
                            || previousCharacter == '*'
                            || previousCharacter == '+'
                            || previousCharacter == '?'
                            || previousCharacter == '.'
                            || previousCharacter == ')') {
                        tokens.add(REGEX_TOKEN_CONCAT);
                    }
                    
                    tokens.add(new RegexToken(RegexTokenType.CHARACTER, ch));
                    break;
            }
            
            previousCharacter = ch;
        }
        
        return tokens;   
    }
    
    private static boolean isTextCharacter(char ch) {
        switch (ch) {
            case '*':
            case '+':
            case '.':
            case '?':
            case '|':
            case '(':
            case ')':
            case '\0':
                return false;
                
            default:
                return true;
        }
    }
}
