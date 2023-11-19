package com.github.coderodde.regex;

import java.util.Deque;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class NondeterministicFiniteAutomatonCompilerTest {
    
    @Test
    public void onSingleCharacterRegex() {
        NondeterministicFiniteAutomaton nfa = getNFA("a");
        
        assertTrue(nfa.matches("a"));
        
        assertFalse(nfa.matches(""));
        assertFalse(nfa.matches("aa"));
        assertFalse(nfa.matches("ab"));
        assertFalse(nfa.matches("b"));
    }
    
    @Test
    public void onUnionRegex() {
        NondeterministicFiniteAutomaton nfa = getNFA("b|c");
        
        assertTrue(nfa.matches("b"));
        assertTrue(nfa.matches("c"));
        
        assertFalse(nfa.matches("a"));
        assertFalse(nfa.matches("bc"));
    }
    
    @Test
    public void onKleeneStarOverSingleChar() {
        NondeterministicFiniteAutomaton nfa = getNFA("1*");
        
        assertTrue(nfa.matches(""));
        assertTrue(nfa.matches("1"));
        assertTrue(nfa.matches("11"));
        assertTrue(nfa.matches("111"));
        
        assertFalse(nfa.matches("0"));
        assertFalse(nfa.matches("10"));
        assertFalse(nfa.matches("01"));
    }
    
    @Test
    public void onKleeneStar() {
        NondeterministicFiniteAutomaton nfa = getNFA("(01)*");
        
        assertTrue(nfa.matches(""));
        assertTrue(nfa.matches("01"));
        assertTrue(nfa.matches("0101"));
        assertTrue(nfa.matches("010101"));
        
        assertFalse(nfa.matches("0"));
        assertFalse(nfa.matches("1"));
        assertFalse(nfa.matches("10"));
        assertFalse(nfa.matches("010"));
    }
    
    @Test
    public void onTwoCharacterConcatenation() {
        NondeterministicFiniteAutomaton nfa = getNFA("aa");
        
        assertTrue(nfa.matches("aa"));
        
        assertFalse(nfa.matches("ab"));
        assertFalse(nfa.matches("a"));
        assertFalse(nfa.matches(""));
    }
    
    @Test
    public void onThreeCharacterConcatenation() {
        NondeterministicFiniteAutomaton nfa = getNFA("abc");
        
        assertTrue(nfa.matches("abc"));
        
        assertFalse(nfa.matches("ab"));
        assertFalse(nfa.matches("abcd"));
        assertFalse(nfa.matches("aba"));
    }
    
    @Test
    public void onComplexRegex() {
        NondeterministicFiniteAutomaton nfa = getNFA("a(ba)*|ab");
        
        assertTrue(nfa.matches("a"));
        assertTrue(nfa.matches("ab"));
        assertTrue(nfa.matches("aba"));
        assertTrue(nfa.matches("ababa"));
        assertTrue(nfa.matches("abababa"));
        
        assertFalse(nfa.matches("ba"));
        assertFalse(nfa.matches("abab"));
    }
    
    private static NondeterministicFiniteAutomaton getNFA(String regex) {
        List<RegexToken> infixTokens = new RegexTokenizer().tokenize(regex);
        Deque<RegexToken> postfixTokens = 
                new RegexInfixToPostfixConverter().convert(infixTokens);
        
        return new NondeterministicFiniteAutomatonCompiler()
                .construct(postfixTokens);
    }
}
