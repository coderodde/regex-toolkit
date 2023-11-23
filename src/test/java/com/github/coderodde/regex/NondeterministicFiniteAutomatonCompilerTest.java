package com.github.coderodde.regex;

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
    
    @Test
    public void onQuestionMark1() {
        NondeterministicFiniteAutomaton nfa = getNFA("ab?");
        
        assertTrue(nfa.matches("a"));
        assertTrue(nfa.matches("ab"));
        
        assertFalse(nfa.matches(""));
        assertFalse(nfa.matches("abb"));
    }
    
    @Test
    public void onQuestionMark2() {
        NondeterministicFiniteAutomaton nfa = getNFA("(ab)?");
        
        assertTrue(nfa.matches(""));
        assertTrue(nfa.matches("ab"));
        
        assertFalse(nfa.matches("a"));
        assertFalse(nfa.matches("abb"));
    }
    
    @Test
    public void onQuestionMark3() {
        NondeterministicFiniteAutomaton nfa = getNFA("(ab*c)?");
        
        assertTrue(nfa.matches(""));
        assertTrue(nfa.matches("ac"));
        assertTrue(nfa.matches("abc"));
        assertTrue(nfa.matches("abbc"));
        assertTrue(nfa.matches("abbbc"));
        
        assertFalse(nfa.matches("a"));
        assertFalse(nfa.matches("bc"));
    }
    
    @Test
    public void onDot1() {
        NondeterministicFiniteAutomaton nfa = getNFA(".");
        
        assertTrue(nfa.matches("a"));
        assertTrue(nfa.matches("b"));
        
        assertFalse(nfa.matches(""));
        assertFalse(nfa.matches("ab"));
    }
    
    @Test
    public void onDot2() {
        NondeterministicFiniteAutomaton nfa = getNFA("..");
        
        assertTrue(nfa.matches("aa"));
        assertTrue(nfa.matches("bb"));
        
        assertFalse(nfa.matches(""));
        assertFalse(nfa.matches("b"));
        assertFalse(nfa.matches("baa"));
    }
    
    @Test
    public void onDot3() {
        NondeterministicFiniteAutomaton nfa = getNFA("..?");
        
        assertTrue(nfa.matches("a"));
        assertTrue(nfa.matches("b"));
        assertTrue(nfa.matches("aa"));
        assertTrue(nfa.matches("bb"));
        
        assertFalse(nfa.matches(""));
        assertFalse(nfa.matches("aaa"));
    }
    
    @Test
    public void onDot4() {
        NondeterministicFiniteAutomaton nfa = getNFA("a(b.)?.");
        
        assertTrue(nfa.matches("a1"));
        assertTrue(nfa.matches("ab11"));
        
        assertFalse(nfa.matches(""));
        assertFalse(nfa.matches("a"));
        assertFalse(nfa.matches("abc"));
    }
    
    private static NondeterministicFiniteAutomaton getNFA(String regex) {
        return NondeterministicFiniteAutomaton.compile(regex);
    }
}
