package io.github.coderodde.regex.tokenizer;

import io.github.coderodde.regex.CodePointRange;
import io.github.coderodde.regex.InvalidRegexException;
import io.github.coderodde.regex.Utils;
import io.github.coderodde.regex.parser.ast.tokens.RegexToken;
import io.github.coderodde.regex.parser.ast.RegexTokenType;
import io.github.coderodde.regex.parser.ast.RegexTokenizationResult;
import io.github.coderodde.regex.parser.ast.tokens.RegexTokenCharacterClass;
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
    
    static {
        REGEX_TOKEN_KLEENE_STAR = 
            new RegexTokenSimple(RegexTokenType.KLEENE_STAR);
        
        REGEX_TOKEN_PLUS        = new RegexTokenSimple(RegexTokenType.PLUS);
        REGEX_TOKEN_QUESTION    = new RegexTokenSimple(RegexTokenType.QUESTION);
        REGEX_TOKEN_DOT         = new RegexTokenSimple(RegexTokenType.DOT);
        REGEX_TOKEN_UNION       = new RegexTokenSimple(RegexTokenType.UNION);
        REGEX_TOKEN_CONCAT      = 
            new RegexTokenSimple(RegexTokenType.CONCATENATION);
        
        REGEX_TOKEN_LEFT_PARENTHESIS = 
            new RegexTokenSimple(RegexTokenType.LEFT_PARENTHESIS);
        
        REGEX_TOKEN_RIGHT_PARENTHESIS = 
            new RegexTokenSimple(RegexTokenType.RIGHT_PARENTHESIS);
    }
    
    private int index;
    private String regex;
    
    /**
     * Converts the input regular expression into the list of {@link RegexToken}
     * objects encoding the same regular language as the input regular 
     * expression.
     * 
     * @param rgx the regular expression to tokenize.
     * @return the list of {@link RegexToken} objects.
     */
    public RegexTokenizationResult tokenize(String rgx) {
        Objects.requireNonNull(rgx, "The input regex is null.");
        
        this.regex = rgx.trim();
        
        if (rgx.isEmpty()) {
            return new RegexTokenizationResult(List.of(), false, false);
        }
        
        Utils.validateRegularExpressionParentheses(rgx);
        Utils.characterClassBracketsValid(rgx);
        
        int startOfLineSymbols = Utils.countNonescapedStartOfLineSymbols(rgx);
        
        if (startOfLineSymbols > 1) {
            throw new InvalidRegexException(
                "The ^ token appears " + startOfLineSymbols + 
                " times. At most one expected.");
        }
        
        int endOfLineSymbols = Utils.countNonescapedEndOfLineSymbols(rgx);
        
        if (endOfLineSymbols > 1) {
            throw new InvalidRegexException(
                "The $ token appears " + endOfLineSymbols + " times.");
        }
        
        boolean anchoredAtStart = false;
        boolean anchoredAtEnd   = false;
        
        if (startOfLineSymbols == 1) {
            if (rgx.charAt(0) != '^') {
                throw new InvalidRegexException("Misplaced ^ symbol.");
            }
            
            anchoredAtStart = true;
            rgx = rgx.substring(1);
        }
        
        if (endOfLineSymbols == 1) {
            if (!rgx.endsWith("$")) {
                throw new InvalidRegexException("Misplaceed $ symbol.");
            }
            
            anchoredAtEnd = true;
            rgx = rgx.substring(0, rgx.length() - 1);
        }
        
        List<RegexToken> tokens = tokenizeImpl();
        
        return new RegexTokenizationResult(tokens, 
                                           anchoredAtStart, 
                                           anchoredAtEnd);
    }
    
    private List<RegexToken> tokenizeImpl() {
        List<RegexToken> tokens = new ArrayList<>();
        int previousCodePoint = 0;
        index = 0;
        
        while (!isAtEnd()) {
            int cp = peekCodePoint();
            
            switch (cp) {
                case (int) '*':
                    advance();
                    
                    if (previousCodePoint == 0) {
                        throw new InvalidRegexException(
                            "The regex starts with Kleene * star.");
                    }
                    
                    tokens.add(REGEX_TOKEN_KLEENE_STAR);
                    break;
                    
                case (int) '+':
                    advance();
                    
                    if (previousCodePoint == 0) {
                        throw new InvalidRegexException(
                            "The regex starts with the + operator.");
                    }
                    
                    tokens.add(REGEX_TOKEN_PLUS);
                    break;
                    
                case (int) '?':
                    advance();
                    
                    if (previousCodePoint == 0) {
                        throw new InvalidRegexException(
                            "The regex starts with ? operator.");
                    }
                    
                    tokens.add(REGEX_TOKEN_QUESTION);
                    break;
                    
                case (int) '.':
                    advance();
                    
                    if (needsConcatBefore(previousCodePoint)) {
                        tokens.add(REGEX_TOKEN_CONCAT);
                    }
                    
                    tokens.add(REGEX_TOKEN_DOT);
                    break;
                    
                case (int) '|':
                    advance();
                    tokens.add(REGEX_TOKEN_UNION);
                    break;
                    
                case (int) '(':
                    advance();
                    
                    if (needsConcatBefore(previousCodePoint)) {
                        tokens.add(REGEX_TOKEN_CONCAT);
                    }
                    
                    tokens.add(REGEX_TOKEN_LEFT_PARENTHESIS);
                    break;
                    
                case (int) ')':
                    advance();
                    tokens.add(REGEX_TOKEN_RIGHT_PARENTHESIS);
                    break;
                    
                case (int) '[':
                    if (needsConcatBefore(previousCodePoint)) {
                        tokens.add(REGEX_TOKEN_CONCAT);
                    }
                    
                    tokens.add(readCharacterClass());
                    break;
                    
                default:
                    advance();
                    
                    if (needsConcatBefore(previousCodePoint)) {
                        tokens.add(REGEX_TOKEN_CONCAT);
                    }
                    
                    tokens.add(new RegexTokenLiteral(cp));
                    break;
            }
            
            previousCodePoint = cp;
        }
        
        return tokens;   
    }
    
    private static boolean needsConcatBefore(int previousCodePoint) {
        return isTextCodePoint(previousCodePoint)
            || previousCodePoint == '*'
            || previousCodePoint == '+'
            || previousCodePoint == '?'
            || previousCodePoint == '.'
            || previousCodePoint == ')'
            || previousCodePoint == ']';
    }
    
    private RegexToken readCharacterClass() {
        advance();
        
        List<CodePointRange> ranges = new ArrayList<>();
        
        while (!isAtEnd() && peekCodePoint() != ']') {
            int first = readEscapedOrRawCodePoint();
            
            if (!isAtEnd() && peekCodePoint() == '-') {
                advance();

                if (isAtEnd() || peekCodePoint() == ']') {
                    throw new InvalidRegexException(
                        "Dangling '-' in character class.");
                }

                int last = readEscapedOrRawCodePoint();

                if (first > last) {
                    throw new InvalidRegexException(
                        "Bad range in character class.");
                }

                ranges.add(new CodePointRange(first, last));
            } else {
                ranges.add(new CodePointRange(first));
            }
        }
        
        if (isAtEnd()) {
            throw new InvalidRegexException("Unclosed character class.");
        }
        
        advance();
        
        return new RegexTokenCharacterClass(ranges);
    }
    
    private boolean isAtEnd() {
        return index >= regex.length();
    }
    
    private int peekCodePoint() {
        return regex.codePointAt(index);
    }
    
    private int advance() {
        int cp = regex.codePointAt(index);
        index += Character.charCount(cp);
        return cp;
    }
    
    private int readEscapedOrRawCodePoint() {
        int cp = advance();
        
        if (cp != '\\') {
            return cp;
        }
        
        if (isAtEnd()) {
            throw new InvalidRegexException("Dangling escape.");
        }
        
        return advance();
    }
    
    // TODO: Funkify here.
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
