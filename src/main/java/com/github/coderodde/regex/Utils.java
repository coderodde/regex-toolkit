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
        
    public static <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        Set<T> set = new HashSet<>(Math.max(set1.size(), set2.size()));
        Set<T> largerSet;
        Set<T> smallerSet;
        
        if (set1.size() < set2.size()) {
            smallerSet = set1;
            largerSet = set2;
        } else {
            smallerSet = set2;
            largerSet = set1;
        }
        
        for (T element : smallerSet) {
            if (largerSet.contains(element)) {
                set.add(element);
            }
        }
        
        return set;
    }
}
