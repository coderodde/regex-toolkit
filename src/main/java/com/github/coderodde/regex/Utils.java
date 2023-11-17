package com.github.coderodde.regex;

import com.github.coderodde.regex.RegexCharacter.RegexCharacterType;
import static com.github.coderodde.regex.RegexCharacter.RegexCharacterType.CHOICE;
import static com.github.coderodde.regex.RegexCharacter.RegexCharacterType.CONCATENATION;
import static com.github.coderodde.regex.RegexCharacter.RegexCharacterType.KLEENE_STAR;
import static com.github.coderodde.regex.RegexCharacter.RegexCharacterType.LEFT_PARENTHESIS;
import static com.github.coderodde.regex.RegexCharacter.RegexCharacterType.RIGHT_PARENTHESIS;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    
    public static List<RegexCharacter> computePostfixRegex(String regex) {
        List<RegexCharacter> regexCharacterList = 
                convertPatternToRegexCharacterList(regex);
        
        return shuntingYardAlgorithm(regexCharacterList);
    }
    
    
    public static List<RegexCharacter> 
        shuntingYardAlgorithm(List<RegexCharacter> tokens) {
        return null;
    }
    
    private static List<RegexCharacter> 
        processQuestionMark(String text, int i) {
            
        if (i == 0) {
            throw new IllegalStateException(
                    "The ? operator cannot be the first character in a " + 
                    "regular expression.");
        }
        
        char preceedingCharacter = text.charAt(i - 1);
        List<RegexCharacter> ret = new ArrayList<>();
        
        if (preceedingCharacter == ')') {
            ret.addAll(getQuestionSubstring(text, i));
        } else {
            ret.addAll(getQuestionCharacters(preceedingCharacter));
        }
        
        return ret;
    }
        
    private static List<RegexCharacter> 
        getQuestionSubstring(String text, int i) {
        Deque<Character> stack = new ArrayDeque<>();
        int j = i - 1;
        
        for (; j >= 0; j--) {
            char ch = text.charAt(j);
            
            if (ch == ')') {
                stack.addLast(ch);
            } else if (ch == '(') {
                stack.removeLast();
                
                if (stack.isEmpty()) {
                    break;
                }
            }
        }
        
        if (j == i - 1) {
            return Collections.<RegexCharacter>emptyList();
        }
        
        StringBuilder sb = new StringBuilder(i - j + 1);
        
        return null;
    }
        
    private static List<RegexCharacter> 
        getQuestionCharacters(char preceedingCharacter) {
        RegexCharacter leftParenthesis = 
                new RegexCharacter(RegexCharacterType.LEFT_PARENTHESIS);
        
        RegexCharacter rightParenthesis = 
                new RegexCharacter(RegexCharacterType.RIGHT_PARENTHESIS);
        
        RegexCharacter epsilon = new RegexCharacter(RegexCharacterType.EPSILON);
        RegexCharacter choice = new RegexCharacter(RegexCharacterType.CHOICE);
        RegexCharacter letter = 
                new RegexCharacter(RegexCharacterType.CHARACTER);
        
        letter.setCharacter(preceedingCharacter);
        
        return new ArrayList<>(
                Arrays.asList(
                        leftParenthesis,
                        epsilon, 
                        choice, 
                        letter, 
                        rightParenthesis));
    }
        
    private static List<RegexCharacter> 
        convertPatternToRegexCharacterList(
                String regex,
                int startIndex,
                int toIndex) {
        List<RegexCharacter> list = new ArrayList<>();
        boolean previousCharacterWasLetter = false;
        
        for (int i = startIndex; i < toIndex; i++) {
            char ch = regex.charAt(i);
            
            switch (ch) {
                case '?':
                    list.addAll(processQuestionMark(regex, i));
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
                    list.add(getRegexChar(RegexCharacterType.EPSILON));
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
    
    public static List<RegexCharacter> 
        convertPatternToRegexCharacterList(String regex) {
//        validateRegularExpressionParentheses(regex);CC
        return convertPatternToRegexCharacterList(regex, 0, regex.length());
    }
        
    private static RegexCharacter 
        getRegexChar(RegexCharacterType regexCharacterType) {
        return new RegexCharacter(regexCharacterType);
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
    
    public static void 
        validateRegularExpressionParentheses(String regularExpression) {
            
        Deque<Character> stack = new ArrayDeque<>();
        
        for (char ch : regularExpression.toCharArray()) {
            if (ch == '(') {
                stack.addLast(ch);
            } else if (ch == ')') {
                if (stack.isEmpty()) {
                    throw new BadRegexException();
                }
                
                stack.removeLast();
            }
        }
        
        if (!stack.isEmpty()) {
            throw new BadRegexException();
        }
    }
}
