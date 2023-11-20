package com.github.coderodde.regex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class NondeterministicFiniteAutomatonTest {
    
    @Test
    public void testEpsilonExpansion() {
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
                nfa.epsilonExpand(startState);
        
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
    
//    @Test
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
        c.addTransition('0', c);
        b.addTransition('1', c);
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
    
//    @Test
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
}
