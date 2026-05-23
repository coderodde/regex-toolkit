package com.github.coderodde.regex;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 12, 2023)
 * @since 1.6 (Nov 12, 2023)
 */
public final class Utils {
    
    public static void 
        validateRegularExpressionParentheses(String regularExpression) {
            
        Deque<Character> stack = new ArrayDeque<>();
        
        for (char ch : regularExpression.toCharArray()) {
            if (ch == '(') {
                stack.addLast(ch);
            } else if (ch == ')') {
                if (stack.isEmpty()) {
                    throw new InvalidRegexException();
                }
                
                stack.removeLast();
            }
        }
        
        if (!stack.isEmpty()) {
            throw new InvalidRegexException();
        }
    }
        
    public static <T> Set<T> difference(Set<T> set1, Set<T> set2) {
        Set<T> outputSet = new HashSet<>(set1.size());
        
        for (T element : set1) {
            if (!set2.contains(element)) {
                outputSet.add(element);
            }
        }
        
        return outputSet;
    }
    
    public static <T> Set<T> intersection(Set<T> a, Set<T> b) {
        Set<T> r = new HashSet<>(Math.min(a.size(), b.size()));
        Set<T> small;
        Set<T> large;
        
        if (a.size() < b.size()) {
            small = a;
            large = b;
        } else {
            small = b;
            large = a;
        }
        
        for (T e : small) {
            if (large.contains(e)) {
                r.add(e);
            }
        }
        
        return r;
    }
    
    public static void choiceBracketsValid(String regex) {
        boolean in = false;
        
        for (int i = 0; i < regex.length(); ++i) {
            char ch = regex.charAt(i);
            
            if (ch == '[') {
                if (in) {
                    throw new InvalidRegexException(
                        String.format("Reopening the choice at index %d.n", i));
                }
                
                in = true;
            } else if (ch == ']') {
                if (in) {
                    in = false;
                } else {
                    throw new InvalidRegexException(
                        String.format(
                            "Closing non existent choice at index %d.n",
                            i));
                }
            }
        }
        
        if (in) {
            throw new InvalidRegexException("Last [ is not matched.");
        }
    }
    
    public static void validateStartOfLineSymbol(String regex) {
        if (regex.length() == 0) {
            throw new InvalidRegexException("Empty regular expression.");
        }
        
        boolean pumpingBackslashesBeforeCircumflex = false;
        int prevBackslashes = 0;
        int endIndex = regex.charAt(0) == '^' ? 1 : 0; 
        
        for (int i = regex.length() - 1; i >= endIndex; --i) {
            switch (regex.charAt(i)) {
                case '^':
                    pumpingBackslashesBeforeCircumflex = true;
                    break;
                    
                case '\\':
                    if (pumpingBackslashesBeforeCircumflex) {
                        ++prevBackslashes;
                    }
                    
                    break;
                    
                default:
                    
                    if (pumpingBackslashesBeforeCircumflex && 
                        prevBackslashes % 2 == 0) {
                        throw new InvalidRegexException(
                                String.format("Non-escaped ^ at index %d.", i));
                    }
                    
                    pumpingBackslashesBeforeCircumflex = false;
                    prevBackslashes = 0;
                    break;
            }
        }
        
        if (pumpingBackslashesBeforeCircumflex && prevBackslashes % 2 == 0) {
            throw new InvalidRegexException();
        }
    }
}
