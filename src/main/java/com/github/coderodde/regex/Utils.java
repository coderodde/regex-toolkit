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
}
