package com.github.coderodde.regex;

import java.util.ArrayList;
import java.util.List;

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
    
    /**
     * Converts the input regular expression into the list of {@link RegexToken}
     * objects encoding the same regular language as the input regular 
     * expression.
     * 
     * @param regex the regular expression to tokenize.
     * @return the list of {@link RegexToken} objects.
     */
    public List<RegexToken> tokenize(String regex) {
        List<RegexToken> tokens = new ArrayList<>();
        char previousCharacter = '\0';
        
        for (char ch : regex.toCharArray()) {
            switch (ch) {
                case '*':
                    tokens.add(new RegexToken(RegexTokenType.KLEEN_STAR));
                    break;
                    
                case '|':
                    tokens.add(new RegexToken(RegexTokenType.UNION));
                    break;
                    
                case '(':
                    if (isTextCharacter(previousCharacter)) {
                        tokens.add(new RegexToken(RegexTokenType.CONCATENATION));
                    } else if (previousCharacter == '*') {
                        tokens.add(new RegexToken(RegexTokenType.CONCATENATION));
                    } else if (previousCharacter == ')') {
                        tokens.add(new RegexToken(RegexTokenType.CONCATENATION));
                    }
                    
                    tokens.add(new RegexToken(RegexTokenType.LEFT_PARENTHESIS));
                    break;
                    
                case ')':
                    tokens.add(new RegexToken(RegexTokenType.RIGHT_PARENTHESIS));
                    break;
                    
                default:
                    // Once here, the ch is an alphabet character:
                    if (isTextCharacter(previousCharacter)) {
                        tokens.add(new RegexToken(RegexTokenType.CONCATENATION));
                    } else if (previousCharacter == '*') {
                        tokens.add(new RegexToken(RegexTokenType.CONCATENATION));
                    } else if (previousCharacter == ')') {
                        tokens.add(new RegexToken(RegexTokenType.CONCATENATION));
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