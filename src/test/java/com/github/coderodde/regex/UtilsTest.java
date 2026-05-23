package com.github.coderodde.regex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public class UtilsTest {
    
    @Test(expected = InvalidRegexException.class)
    public void throwsOnValidateRegularExpressionParentheses1() {
        Utils.validateRegularExpressionParentheses("(");
    }
    
    @Test(expected = InvalidRegexException.class)
    public void throwsOnValidateRegularExpressionParentheses2() {
        Utils.validateRegularExpressionParentheses("((");
    }
    
    @Test(expected = InvalidRegexException.class)
    public void throwsOnValidateRegularExpressionParentheses3() {
        Utils.validateRegularExpressionParentheses(")(");
    }
    
    @Test(expected = InvalidRegexException.class)
    public void throwsOnValidateRegularExpressionParentheses4() {
        Utils.validateRegularExpressionParentheses(")a(");
    }
    
    @Test
    public void validateValidRegularExpressionParentheses1() {
        Utils.validateRegularExpressionParentheses("");
    }
    
    @Test
    public void validateValidRegularExpressionParentheses2() {
        Utils.validateRegularExpressionParentheses("()");
    }
    
    @Test
    public void validateValidRegularExpressionParentheses3() {
        Utils.validateRegularExpressionParentheses("(())");
    }
    
    @Test
    public void validateValidRegularExpressionParentheses4() {
        Utils.validateRegularExpressionParentheses("(()())");
    }
    
    @Test
    public void validateValidRegularExpressionParentheses5() {
        Utils.validateRegularExpressionParentheses("((a)bc(d)e)");
    }
    
    @Test
    public void difference() {
        Set<Integer> a = new HashSet<>(Arrays.asList(1, 2, 3));
        Set<Integer> b = new HashSet<>(Arrays.asList(3, 2, 4));
        Set<Integer> r = Utils.difference(a, b);
        
        assertEquals(1, r.size());
        assertTrue(r.contains(1));
        
        a = new HashSet<>(Arrays.asList(1, 2));
        b = new HashSet<>(Arrays.asList(2, 1));
        r = Utils.difference(a, b);
        
        assertTrue(r.isEmpty());
        
        a = new HashSet<>(Arrays.asList(1, 2, 3));
        b = new HashSet<>(Arrays.asList(4, 5));
        r = Utils.difference(a, b);
        
        assertEquals(3, r.size());
        assertTrue(r.contains(1));
        assertTrue(r.contains(2));
        assertTrue(r.contains(3));
        assertFalse(r.contains(0));
        assertFalse(r.contains(4));
        assertFalse(r.contains(5));
        assertFalse(r.contains(6));
    }
    
    @Test
    public void intersection() {
        Set<Integer> a = new HashSet<>(Arrays.asList(1, 2, 3));
        Set<Integer> b = new HashSet<>(Arrays.asList(3, 4, 2));
        Set<Integer> r = Utils.intersection(a, b);
        
        assertEquals(2, r.size());
        assertTrue(r.contains(2));
        assertTrue(r.contains(3));
        assertFalse(r.contains(1));
        assertFalse(r.contains(4));
        assertFalse(r.contains(0));
        assertFalse(r.contains(5));
    }
    
    @Test
    public void choiceBraces1() {
        try {
            Utils.choiceBracketsValid("[abc]");
        } catch (InvalidRegexException ex) {
            fail();
        }
    }
    
    @Test
    public void choiceBraces2() {
        try {
            Utils.choiceBracketsValid("[abc]d[ef]");
        } catch (InvalidRegexException ex) {
            fail();
        }
    }
    
    @Test
    public void choiceBraces3() {
        try {
            Utils.choiceBracketsValid("[abc]d[ef][]");
        } catch (InvalidRegexException ex) {
            fail();
        }
    }
    
    @Test(expected = InvalidRegexException.class)
    public void choiceBracesThrows1() {
        Utils.choiceBracketsValid("[");
    }
    
    @Test(expected = InvalidRegexException.class)
    public void choiceBracesThrows2() {
        Utils.choiceBracketsValid("]");
    }
    
    @Test(expected = InvalidRegexException.class)
    public void choiceBracesThrows3() {
        Utils.choiceBracketsValid("][");
    }
    
    @Test(expected = InvalidRegexException.class)
    public void choiceBracesThrows4() {
        Utils.choiceBracketsValid("[]]");
    }
    
    @Test
    public void startSymbol2() {
        Utils.validateStartOfLineSymbol("^");
    }
    
    @Test
    public void startSymbol3() {
        Utils.validateStartOfLineSymbol("^\\^");
    }
    
    @Test
    public void startSymbol4() {
        Utils.validateStartOfLineSymbol("\\\\\\^");
    }
    
    @Test
    public void startSymbol5() {
        Utils.validateStartOfLineSymbol("\\^abc\\\\\\^def\\\\\\\\\\^g");
    }
    
    @Test
    public void startSymbol6() {
        Utils.validateStartOfLineSymbol("^\\^");
    }
    
    @Test(expected = InvalidRegexException.class)
    public void startSymbolThrows1() {
        Utils.validateStartOfLineSymbol("\\\\^");
    }
    
    @Test(expected = InvalidRegexException.class)
    public void startSymbolThrows2() {
        Utils.validateStartOfLineSymbol("^\\\\^");
    }
    
    @Test(expected = InvalidRegexException.class)
    public void startSymbolThrows3() {
        Utils.validateStartOfLineSymbol("^abc\\\\^");
    }
    
    @Test(expected = InvalidRegexException.class)
    public void startSymbolThrows4() {
        Utils.validateStartOfLineSymbol("^abc\\^def^");
    }
}
