package com.github.coderodde.regex;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * This class provides a facility for converting the regular expressions 
 * expressed in the infix notation to the regular expressions expressed in the
 * postfix notation.
 * 
 * @author Rodion "rodio" Efremov
 * @version 1.6 (Nov 16, 2023)
 * @since 1.6 (Nov 16, 2023)
 */
public final class RegexInfixToPostfixConverter {
    
    /**
     * Converts a regular expression expressed in the infix notation of regular
     * expression token lists. This algorithm is essentially the
     * <a href="https://en.wikipedia.org/wiki/Shunting_yard_algorithm">Shunting yard algorithm</a> by
     * <a href="https://en.wikipedia.org/wiki/Edsger_W._Dijkstra">Edsger W. Dijkstra</a>.
     * 
     * @param infixRegex
     * @return the input regular expression in postfix notation.
     */
    public Deque<RegexToken> convert(List<RegexToken> infixRegex) {
        Objects.requireNonNull(infixRegex, 
                               "The input regular expression is null.");
        
        Deque<RegexToken> output = new ArrayDeque<>(infixRegex.size());
        Deque<RegexToken> operatorStack = new ArrayDeque<>(infixRegex.size());
        
        for (int i = 0, n = infixRegex.size(); i != n; i++) {
            RegexToken regexToken = infixRegex.get(i);
            
            switch (regexToken.getTokenType()) {
                case CHARACTER:
                case DOT:
                    output.addLast(regexToken);
                    break;
                    
                case KLEENE_STAR:
                case PLUS:
                case QUESTION:
                    if (i == 0) {
                        throw new InvalidRegexException();
                    }
                    
                    output.addLast(regexToken);
                    break;
                    
                case CONCAT:
                    if (i == 0) {
                        throw new InvalidRegexException();
                    }
                    
                    processConcatenation(output, operatorStack);
                    break;
                    
                case UNION:
                    if (i == 0) {
                        throw new InvalidRegexException();
                    }
                    
                    processUnion(output, operatorStack);
                    break;
                    
                case LEFT_PARENTHESIS:
                    operatorStack.addLast(regexToken);
                    break;
                    
                case RIGHT_PARENTHESIS:
                    processRightParenthesis(output, operatorStack);
                    break;
            }
        }
        
        while (!operatorStack.isEmpty()) {
            RegexToken topToken = operatorStack.removeLast();

            if (topToken.getTokenType() == RegexTokenType.LEFT_PARENTHESIS) {
                throw new InvalidRegexException();
            }

            output.addLast(topToken);
        }
        
        return output;
    }
    
    private static void processConcatenation(
            Deque<RegexToken> output, 
            Deque<RegexToken> operatorStack) {
        
        int tokenPrecedence = 
                getOperatorPrecedence(RegexTokenType.CONCAT);
        
        while (!operatorStack.isEmpty()) {
            RegexToken topToken = operatorStack.getLast();
            
            if (topToken.getTokenType() == RegexTokenType.LEFT_PARENTHESIS) {
                break;
            }
            
            int topTokenPrecedence = 
                    getOperatorPrecedence(topToken.getTokenType());
            
            if (topTokenPrecedence <= tokenPrecedence) {
                break;
            }
            
            output.addLast(operatorStack.removeLast());
        }
        
        operatorStack.addLast(new RegexToken(RegexTokenType.CONCAT));
    }
    
    private static void processUnion(
            Deque<RegexToken> output, 
            Deque<RegexToken> operatorStack) {
        
        int tokenPrecedence = 
                getOperatorPrecedence(RegexTokenType.UNION);
        
        while (!operatorStack.isEmpty()) {
            RegexToken topToken = operatorStack.getLast();
            
            if (topToken.getTokenType() == RegexTokenType.LEFT_PARENTHESIS) {
                break;
            }
            
            int topTokenPrecedence = 
                    getOperatorPrecedence(topToken.getTokenType());
            
            if (topTokenPrecedence <= tokenPrecedence) {
                break;
            }
            
            output.addLast(operatorStack.removeLast());
        }
        
        operatorStack.addLast(new RegexToken(RegexTokenType.UNION));
    }
    
    private static void processRightParenthesis(
            Deque<RegexToken> output, 
            Deque<RegexToken> operatorStack) {
        
        while (!operatorStack.isEmpty()) {
            RegexToken topToken = operatorStack.getLast();
            
            if (topToken.getTokenType() == RegexTokenType.LEFT_PARENTHESIS) {
                break;
            }
            
            output.addLast(operatorStack.removeLast());
        }
        
        if (operatorStack.isEmpty()) {
            throw new InvalidRegexException();
        }
        
        RegexToken topToken = operatorStack.removeLast();
        
        if (topToken.getTokenType() != RegexTokenType.LEFT_PARENTHESIS) {
            throw new InvalidRegexException();
        }
    }
    
    // Processes the question mark. a? -> e a |
    //                             ab? -> e a b concat |
    //                             (a+b)? -> e a plus concat b |
    private static void processQuestion(int questionMarkIndex,
                                        Deque<RegexToken> output,
                                        Deque<RegexToken> operatorStack) {
        
    }
    
    private static int getOperatorPrecedence(RegexTokenType tokenType) {
        switch (tokenType) {
            case KLEENE_STAR:
            case PLUS:
                return 3;
                
            case CONCAT:
                return 2;
                
            case QUESTION:
                return 1;
                
            case UNION:
                return 0;
                
            default:
                throw new IllegalStateException("Should not get here.");
        }
    }
}
