package com.github.coderodde.regex;

import com.github.coderodde.regex.RegexCharacter.RegexCharacterType;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 12, 2023)
 * @since 1.6 (Nov 12, 2023)
 */
public final class Utils {
    
    public static <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        Set<T> set = new HashSet<>(Math.max(set1.size(), set2.size()));
        Set<T> largerSet  = set1.size() > set2.size() ? set1 : set2;
        Set<T> smallerSet = set1.size() <= set2.size() ? set1 : set2;
        
        for (T element : smallerSet) {
            if (largerSet.contains(element)) {
                set.add(element);
            }
        }
        
        return set;
    }
    
    public static List<RegexCharacter> computePostfixRegex(String regex) {
        List<RegexCharacter> regexCharacterList = 
                convertPatternToRegexCharacterList(regex);
        
        return shuntingYardAlgorithm(regexCharacterList);
    }
    
    public static void 
        validateRegularExpressionParentheses(String regularExpression) {
        Deque<Character> stack = new ArrayDeque<>();
        
        for (char ch : regularExpression.toCharArray()) {
            if (ch == '(') {
                stack.addLast(ch);
            } else if (ch == ')') {
                if (stack.isEmpty()) {
                    throw new BadRegexParenthesationException();
                }
                
                stack.removeLast();
            }
        }
        
        if (!stack.isEmpty()) {
            throw new BadRegexParenthesationException();
        }
    }
    
    public static List<RegexCharacter> 
        shuntingYardAlgorithm(List<RegexCharacter> tokens) {
        return null;
    }
        
    private static int getTokenOperatorPrecedence(
            RegexCharacter regexCharacter) {
        RegexCharacterType regexCharacterType =
                regexCharacter.getRegexCharacterType();
        
        switch (regexCharacterType) {
            case LEFT_PARENTHESIS:
            case RIGHT_PARENTHESIS:
                return 4;
                
            case KLEENE_STAR:
                return 3;
                
            case CONCATENATION:
                return 2;
                
            case CHOICE:
                return 1;
                
            default:
                return 0;
        }
    }
    
    private static List<RegexCharacter> 
        processQuestionMark(String text, int i) {
        return null;
    }
    
    public static List<RegexCharacter> 
        convertPatternToRegexCharacterList(String text) {
        List<RegexCharacter> list = new ArrayList<>();
        boolean previousCharacterWasLetter = false;
        
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            
            switch (ch) {
                case '?':
                    list.addAll(processQuestionMark(text, i));
                    previousCharacterWasLetter = false;
                    break;
                    
                case '*':
                    list.add(getRegexChar(RegexCharacterType.KLEENE_STAR));
                    previousCharacterWasLetter = false;
                    break;
                    
                case '+':
                    list.add(getRegexChar(RegexCharacterType.PLUS));
                    previousCharacterWasLetter = false;
                    break;
                    
                case '|':
                    list.add(getRegexChar(RegexCharacterType.CHOICE));
                    previousCharacterWasLetter = false;
                    break;
                    
                case '(':
                    list.add(getRegexChar(RegexCharacterType.LEFT_PARENTHESIS));
                    previousCharacterWasLetter = false;
                    break;
                    
                case ')':
                    list.add(
                            getRegexChar(
                                    RegexCharacterType.RIGHT_PARENTHESIS));
                    
                    previousCharacterWasLetter = false;
                    break;
                    
                case '\0':
                    list.add(getRegexChar(RegexCharacterType.EMPTY_STRING));
                    previousCharacterWasLetter = false;
                    // What??
                    break;
                    
                default:
                    RegexCharacter regexCharacter = 
                            new RegexCharacter(RegexCharacterType.CHARACTER);
                    
                    regexCharacter.setCharacter(ch);
                    list.add(regexCharacter);
                    previousCharacterWasLetter = true;
                    break;
            }
            
            if (previousCharacterWasLetter) {
                list.add(new RegexCharacter(RegexCharacterType.CONCATENATION));
            }
        }
        
        return list;
    }
        
    private static RegexCharacter 
        getRegexChar(RegexCharacterType regexCharacterType) {
        return new RegexCharacter(regexCharacterType);
    }
}
