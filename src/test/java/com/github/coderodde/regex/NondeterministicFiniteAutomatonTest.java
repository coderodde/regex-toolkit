package com.github.coderodde.regex;

import static com.github.coderodde.regex.NondeterministicFiniteAutomaton.epsilonExpand;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class NondeterministicFiniteAutomatonTest {
    
    @Test
    public void epsilonExpansion() {
        NondeterministicFiniteAutomaton nfa =
                new NondeterministicFiniteAutomaton();
        
        NondeterministicFiniteAutomatonState q0 = 
                new NondeterministicFiniteAutomatonState(0);
       
        NondeterministicFiniteAutomatonState q1 = 
                new NondeterministicFiniteAutomatonState(1);
        
        NondeterministicFiniteAutomatonState q2 = 
                new NondeterministicFiniteAutomatonState(2);
        
        nfa.setInitialState(q0);
        
        q0.addTransition('a', q1);
        q0.addEpsilonTransition(q1);
        q1.addEpsilonTransition(q2);
        q2.addEpsilonTransition(q0);
        
        Set<NondeterministicFiniteAutomatonState> startState =
                new HashSet<>(Arrays.asList(q0));
        
        Set<NondeterministicFiniteAutomatonState> epsilonExpandedSet = 
                epsilonExpand(startState);
        
        assertEquals(3, epsilonExpandedSet.size());
        
        assertTrue(epsilonExpandedSet.contains(q0));
        assertTrue(epsilonExpandedSet.contains(q1));
        assertTrue(epsilonExpandedSet.contains(q2));
    }
    
    @Test
    public void match() {
        NondeterministicFiniteAutomaton nfa = 
                new NondeterministicFiniteAutomaton();
        
        NondeterministicFiniteAutomatonState q0 = 
                new NondeterministicFiniteAutomatonState(0);
        
        NondeterministicFiniteAutomatonState q1 = 
                new NondeterministicFiniteAutomatonState(1);
        
        NondeterministicFiniteAutomatonState q2 = 
                new NondeterministicFiniteAutomatonState(2);
        
        NondeterministicFiniteAutomatonState q3 = 
                new NondeterministicFiniteAutomatonState(3);
        
        nfa.setInitialState(q0);
        nfa.setAcceptingState(q3);
        
        q0.addTransition('a', q0);
        q0.addTransition('b', q0);
        q0.addTransition('b', q1);
        q1.addTransition('a', q2);
        q2.addTransition('b', q3);
        q3.addTransition('a', q2);
        
        q1.addEpsilonTransition(q2);
        
        assertTrue(nfa.matches("bb"));
        assertTrue(nfa.matches("abbab"));
        assertTrue(nfa.matches("abab"));
        
        assertFalse(nfa.matches("ba"));
        assertFalse(nfa.matches("ababa"));
    }
    
    @Test
    public void convertToDFA1() {
        NondeterministicFiniteAutomaton nfa =
                new NondeterministicFiniteAutomaton();
        
        NondeterministicFiniteAutomatonState a = 
                new NondeterministicFiniteAutomatonState(0);
        
        NondeterministicFiniteAutomatonState b = 
                new NondeterministicFiniteAutomatonState(1);
        
        NondeterministicFiniteAutomatonState c = 
                new NondeterministicFiniteAutomatonState(2);
        
        NondeterministicFiniteAutomatonState d = 
                new NondeterministicFiniteAutomatonState(3);
        
        nfa.setInitialState(a);
        nfa.setAcceptingState(d);
        
        a.addTransition('0', a);
        a.addTransition('0', b);
        a.addEpsilonTransition(c);
        b.addTransition('1', c);
        c.addTransition('0', c);
        c.addTransition('1', d);
        c.addEpsilonTransition(d);
        
        assertTrue(nfa.matches(""));
        assertTrue(nfa.matches("1"));
        assertTrue(nfa.matches("01"));
        assertTrue(nfa.matches("011"));
        assertTrue(nfa.matches("000"));
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches(""));
        assertTrue(dfa.matches("1"));
        assertTrue(dfa.matches("01"));
        assertTrue(dfa.matches("011"));
        assertTrue(dfa.matches("000"));
    }
    
    @Test
    public void convertToDFA2() {
        NondeterministicFiniteAutomaton nfa = 
                new NondeterministicFiniteAutomaton();
        
        NondeterministicFiniteAutomatonState state1 = 
                new NondeterministicFiniteAutomatonState(0);
        
        NondeterministicFiniteAutomatonState state2 = 
                new NondeterministicFiniteAutomatonState(1);
        
        nfa.setInitialState(state1);
        nfa.setAcceptingState(state2);
        
        state1.addTransition('1', state2);
        state1.addEpsilonTransition(state2);
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(nfa.matches(""));
        assertTrue(nfa.matches("1"));
        assertFalse(nfa.matches("0"));
        
        assertTrue(dfa.matches(""));
        assertTrue(dfa.matches("1"));
        assertFalse(dfa.matches("0"));
    }
    
    @Test
    public void convertSingleCharRegex() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("a");
        
        assertTrue(nfa.matches("a"));
        assertFalse(nfa.matches(""));
        assertFalse(nfa.matches("aa"));
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches("a"));
        assertFalse(dfa.matches(""));
        assertFalse(dfa.matches("aa"));
    }
    
    @Test
    public void convertTwoCharRegex() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("ab");
        
        assertTrue(nfa.matches("ab"));
        
        assertFalse(nfa.matches(""));
        assertFalse(nfa.matches("a"));
        assertFalse(nfa.matches("b"));
        assertFalse(nfa.matches("aa"));
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches("ab"));
        
        assertFalse(dfa.matches(""));
        assertFalse(dfa.matches("a"));
        assertFalse(dfa.matches("b"));
        assertFalse(dfa.matches("aa"));
    }
    
    @Test
    public void convertUnion() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("a|b");
        
        assertTrue(nfa.matches("a"));
        assertTrue(nfa.matches("b"));
        
        assertFalse(nfa.matches(""));
        assertFalse(nfa.matches("ab"));
        assertFalse(nfa.matches("ba"));
        assertFalse(nfa.matches("1"));
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches("a"));
        assertTrue(dfa.matches("b"));
        
        assertFalse(dfa.matches(""));
        assertFalse(dfa.matches("ab"));
        assertFalse(dfa.matches("ba"));
        assertFalse(dfa.matches("1"));
    }
    
    @Test
    public void convertToDFA3() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("(ab|c)*");
        
        assertTrue(nfa.matches(""));
        assertTrue(nfa.matches("c"));
        assertTrue(nfa.matches("ab"));
        assertTrue(nfa.matches("abab"));
        assertTrue(nfa.matches("abc"));
        assertTrue(nfa.matches("abcc"));
        assertTrue(nfa.matches("cc"));
        assertTrue(nfa.matches("ccc"));
        assertTrue(nfa.matches("ccab"));
        
        assertFalse(nfa.matches("a"));
        assertFalse(nfa.matches("b"));
        assertFalse(nfa.matches("ba"));
        assertFalse(nfa.matches("baab"));
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches(""));
        assertTrue(dfa.matches("c"));
        assertTrue(dfa.matches("ab"));
        assertTrue(dfa.matches("abab"));
        assertTrue(dfa.matches("abc"));
        assertTrue(dfa.matches("abcc"));
        assertTrue(dfa.matches("cc"));
        assertTrue(dfa.matches("ccc"));
        assertTrue(dfa.matches("ccab"));
        
        assertFalse(dfa.matches("a"));
        assertFalse(dfa.matches("b"));
        assertFalse(dfa.matches("ba"));
        assertFalse(dfa.matches("baab"));
    }
    
    @Test
    public void convertToDFA4() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("(a|b)*");
        
        assertTrue(nfa.matches(""));
        assertTrue(nfa.matches("a"));
        assertTrue(nfa.matches("b"));
        assertTrue(nfa.matches("ab"));
        assertTrue(nfa.matches("ba"));
        assertTrue(nfa.matches("aba"));
        assertTrue(nfa.matches("abb"));
        assertTrue(nfa.matches("abb"));
        
        assertFalse(nfa.matches("1"));
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches(""));
        assertTrue(dfa.matches("a"));
        assertTrue(dfa.matches("b"));
        assertTrue(dfa.matches("ab"));
        assertTrue(dfa.matches("ba"));
        assertTrue(dfa.matches("aba"));
        assertTrue(dfa.matches("abb"));
        assertTrue(dfa.matches("abb"));
        
        assertFalse(dfa.matches("1"));
    }
    
    @Test
    public void convertToDFA5() {
        NondeterministicFiniteAutomaton nfa =
                NondeterministicFiniteAutomaton.compile("a*");
        
        assertTrue(nfa.matches(""));
        assertTrue(nfa.matches("a"));
        assertTrue(nfa.matches("aa"));
        
        assertFalse(nfa.matches("b"));
        assertFalse(nfa.matches("ab"));
        
        DeterministicFiniteAutomaton dfa =
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches(""));
        assertTrue(dfa.matches("a"));
        assertTrue(dfa.matches("aa"));
        
        assertFalse(dfa.matches("b"));
        assertFalse(dfa.matches("ab"));
    }
    
    @Test
    public void unionOfDotsToDFA() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile(".|.");
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches("a"));
        assertTrue(dfa.matches("b"));
        assertTrue(dfa.matches("c"));
    }
    
    @Test
    public void dotNFAToDFA() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile(".");
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches("a"));
        assertTrue(dfa.matches("b"));
        assertTrue(dfa.matches("c"));
    }
}
