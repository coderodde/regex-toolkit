package io.github.coderodde.regex.tokenizer;

import io.github.coderodde.regex.InvalidRegexException;
import io.github.coderodde.regex.Utils;
import io.github.coderodde.regex.parser.ast.tokens.RegexToken;
import io.github.coderodde.regex.parser.ast.RegexTokenType;
import io.github.coderodde.regex.parser.ast.tokens.RegexTokenLiteral;
import io.github.coderodde.regex.parser.ast.tokens.RegexTokenSimple;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class implements regex tokenizer. The idea behind tokenization is to
 * convert the regular expression into a list of {@link RegexToken} instances
 * representing the regular expression. We do this in order to conveniently deal
 * with the explicit concatenation operators that are not present in the regular
 * expressions.
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
//    private static final RegexToken REGEX_TOKEN_CONCATENATION;
    private static final RegexToken REGEX_TOKEN_START_OF_LINE;
    private static final RegexToken REGEX_TOKEN_END_OF_LINE;
    
    static {
        REGEX_TOKEN_KLEENE_STAR = 
            new RegexTokenSimple(RegexTokenType.KLEENE_STAR);
        
        REGEX_TOKEN_PLUS        = new RegexTokenSimple(RegexTokenType.PLUS);
        REGEX_TOKEN_QUESTION    = new RegexTokenSimple(RegexTokenType.QUESTION);
        REGEX_TOKEN_DOT         = new RegexTokenSimple(RegexTokenType.DOT);
        REGEX_TOKEN_UNION       = new RegexTokenSimple(RegexTokenType.UNION);
        REGEX_TOKEN_CONCAT      = new RegexTokenSimple(RegexTokenType.UNION);
//        REGEX_TOKEN_CONCATENATION = new RegexTokenSimple(RegexTokenType.CONCATENATION);
        
        REGEX_TOKEN_LEFT_PARENTHESIS = 
            new RegexTokenSimple(RegexTokenType.LEFT_PARENTHESIS);
        
        REGEX_TOKEN_RIGHT_PARENTHESIS = 
            new RegexTokenSimple(RegexTokenType.RIGHT_PARENTHESIS);
        
        REGEX_TOKEN_START_OF_LINE = 
            new RegexTokenSimple(RegexTokenType.BEGIN_OF_LINE);
        
        REGEX_TOKEN_END_OF_LINE = 
            new RegexTokenSimple(RegexTokenType.END_OF_LINE);
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
        Objects.requireNonNull(regex, "The input regex is null.");
        
        regex = regex.trim();
        
        Utils.validateRegularExpressionParentheses(regex);
        Utils.characterClassBracketsValid(regex);
        
        int startOfLineSymbols = Utils.countNonescapedStartOfLineSymbols(regex);
        
        if (startOfLineSymbols > 1) {
            throw new InvalidRegexException(
                "The ^ token appears " + startOfLineSymbols + " times.");
        }
        
        List<RegexToken> tokens = new ArrayList<>();
        
        if (startOfLineSymbols == 1) {
            if (regex.charAt(0) != '^') {
                throw new InvalidRegexException("Misplaced ^ symbol.");
            }
            
            tokens.add(REGEX_TOKEN_START_OF_LINE);
        }
        
        int endOfLineSymbols = Utils.countNonescapedEndOfLineSymbols(regex);
        
        if (endOfLineSymbols > 2) {
            throw new InvalidRegexException(
                "The $ token appears " + endOfLineSymbols + " times.");
        }
        
        boolean appendEndOfLineSymbol = false;
        
        if (endOfLineSymbols == 1) {
            if (regex.charAt(regex.length() - 1) != '$') {
                throw new InvalidRegexException("Misplaced $ symbol.");
            }
            
            appendEndOfLineSymbol = true;
        }
        
        int previousCodePoint = 0;
        int[] regexCodePoints = regex.codePoints().toArray();
        
        for (int i = 0, n = regexCodePoints.length; i != n; i++) {
            int cp = regexCodePoints[i];
            
            switch (cp) {
                case (int) '*':
                    if (i == 0) {
                        throw new InvalidRegexException(
                            "The regex starts with Kleene star.");
                    }
                    
                    tokens.add(REGEX_TOKEN_KLEENE_STAR);
                    break;
                    
                case (int) '+':
                    if (i == 0) {
                        throw new InvalidRegexException(
                            "The regex starts with the + operator.");
                    }
                    
                    tokens.add(REGEX_TOKEN_PLUS);
                    break;
                    
                case (int) '?':
                    if (i == 0) {
                        throw new InvalidRegexException(
                            "The regex starts with ? operator.");
                    }
                    
                    tokens.add(REGEX_TOKEN_QUESTION);
                    break;
                    
                case (int) '.':
                    if (isTextCodePoint(previousCodePoint) // i == 0?
                            || previousCodePoint == (int) '*'
                            || previousCodePoint == (int) '+'
                            || previousCodePoint == (int) '?'
                            || previousCodePoint == (int) '.'
                            || previousCodePoint == (int) ')') {
                        tokens.add(REGEX_TOKEN_CONCAT);
                    }
                    
                    tokens.add(REGEX_TOKEN_DOT);
                    break;
                    
                case (int) '|':
                    tokens.add(REGEX_TOKEN_UNION);
                    break;
                    
                case (int) '(':
                    if (isTextCodePoint(previousCodePoint)
                            || previousCodePoint == (int) '*'
                            || previousCodePoint == (int) '+'
                            || previousCodePoint == (int) '.'
                            || previousCodePoint == (int) '?'
                            || previousCodePoint == (int) ')') {
                        tokens.add(REGEX_TOKEN_CONCAT);
                    }
                    
                    tokens.add(REGEX_TOKEN_LEFT_PARENTHESIS);
                    break;
                    
                case (int) ')':
                    tokens.add(REGEX_TOKEN_RIGHT_PARENTHESIS);
                    break;
                    
                default:
                    // Once here, the ch is an alphabet character:
                    if (isTextCodePoint(previousCodePoint)
                            || previousCodePoint == (int) '*'
                            || previousCodePoint == (int) '+'
                            || previousCodePoint == (int) '?'
                            || previousCodePoint == (int) '.'
                            || previousCodePoint == (int) ')') {
                        tokens.add(REGEX_TOKEN_CONCAT);
                    }
                    
                    tokens.add(new RegexTokenLiteral(cp));
                    break;
            }
            
            previousCodePoint = cp;
        }
        
        if (appendEndOfLineSymbol) {
            tokens.add(REGEX_TOKEN_END_OF_LINE);
        }
        
        return tokens;   
    }
    
    private static boolean isTextCodePoint(int cp) {
        switch (cp) {
            case (int) '*':
            case (int) '+':
            case (int) '.':
            case (int) '?':
            case (int) '|':
            case (int) '(':
            case (int) ')':
            case (int) '\0':
            case (int) '^':
            case (int) '$':
                return false;
                
            default:
                return true;
        }
    }
}
