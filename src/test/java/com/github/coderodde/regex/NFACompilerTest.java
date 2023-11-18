package com.github.coderodde.regex;

import java.util.Deque;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class NFACompilerTest {
    
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
    
    private static NondeterministicFiniteAutomaton getNFA(String regex) {
        List<RegexToken> infixTokens = new RegexTokenizer().tokenize(regex);
        Deque<RegexToken> postfixTokens = 
                new RegexInfixToPostfixConverter().convert(infixTokens);
        
        return new NFACompiler().construct(postfixTokens);
    }
}
