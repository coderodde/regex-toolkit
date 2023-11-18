package com.github.coderodde.regex;

import java.util.Deque;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class NFACompilerTest {
    
    @Test
    public void testOnValidRegex1() {
        NondeterministicFiniteAutomaton nfa = getNFA("a");
        
        assertTrue(nfa.matches("a"));
        
        assertFalse(nfa.matches(""));
        assertFalse(nfa.matches("aa"));
    }
    
    private static NondeterministicFiniteAutomaton getNFA(String regex) {
        List<RegexToken> infixTokens = new RegexTokenizer().tokenize(regex);
        Deque<RegexToken> postfixTokens = 
                new RegexInfixToPostfixConverter().convert(infixTokens);
        
        return new NFACompiler().construct(postfixTokens);
    }
}
