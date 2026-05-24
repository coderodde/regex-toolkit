package com.github.coderodde.regex;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * This class contains various methods usable in regular expression engines.
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
    
    public static void characterClassBracketsValid(String regex) {
        boolean inClass = false;
        boolean escaped = false;
        
        for (int i = 0; i < regex.length(); ++i) {
            char ch = regex.charAt(i);
            
            if (escaped) {
                escaped = false;
                continue;
            }
            
            if (ch == '\\') {
                escaped = true;
                continue;
            }
            
            if (ch == '[') {
                if (inClass) {
                    throw new InvalidRegexException(
                        "Unescaped '[' inside character class at index " + i);
                }
                
                inClass = true;
                continue;
            }
            
            if (ch == ']') {
                if (!inClass) {
                    throw new InvalidRegexException(
                        "Unescaped ']' outside character class at index " + i);
                }
                
                inClass = false;
            }
        }
        
        if (escaped) {
            throw new InvalidRegexException("Dangling escape at end of regex.");
        }
        
        if (inClass) {
            throw new InvalidRegexException(
                "Unclosed character class: missing ].");
        }
    }
    
    public static int countNonescapedStartOfLineSymbols(String regex) {
        return countNonescapedStartOrEndOfLineSymbolsImpl(regex, '^');
    }
    
    public static int countNonescapedEndOfLineSymbols(String regex) {
        return countNonescapedStartOrEndOfLineSymbolsImpl(regex, '$');
    }
    
    private static int countNonescapedStartOrEndOfLineSymbolsImpl(
            String regex, char symbol) {
        
        regex = regex.trim();
        
        if (regex.length() == 0) {
            throw new InvalidRegexException("Empty regular expression.");
        }
        
        int count = 0;
        int consecutiveBackslashes = 0;
        
        for (int i = 0; i < regex.length(); ++i) {
            char ch = regex.charAt(i);
            
            if (ch == '\\') {
                consecutiveBackslashes++;
                continue; // Go to the next character.
            }
            
            if (ch == symbol && consecutiveBackslashes % 2 == 0) {
                count++;
            }
        
            consecutiveBackslashes = 0;
        }
        
        return count;
    }
}
