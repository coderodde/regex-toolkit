package com.github.coderodde.regex;

import org.junit.Test;
import static org.junit.Assert.*;

public class DeterministicFiniteAutomatonTest {
    
    @Test
    public void on10DFA() {
        DeterministicFiniteAutomatonState q0 = 
                new DeterministicFiniteAutomatonState(0);
        
        DeterministicFiniteAutomatonState q1 = 
                new DeterministicFiniteAutomatonState(1);
        
        DeterministicFiniteAutomatonState q2 = 
                new DeterministicFiniteAutomatonState(2);
        
        DeterministicFiniteAutomaton dfa = new DeterministicFiniteAutomaton();
        
        dfa.setInitialState(q0);
        
        dfa.getAcceptingStateSet().addDeterministicFiniteAutomatonState(q2);
        
        q0.addFollowerState('1', q0);
        q0.addFollowerState('0', q1);
        q1.addFollowerState('0', q1);
        q1.addFollowerState('1', q2);
        q2.addFollowerState('0', q1);
        q2.addFollowerState('1', q0);
        
        assertTrue(dfa.matches("1001101"));
        assertTrue(dfa.matches("01"));
        
        assertFalse(dfa.matches("a01"));
        assertFalse(dfa.matches("00100"));
        assertFalse(dfa.matches("0"));
        assertFalse(dfa.matches(""));
    }
    
    @Test
    public void on10DFA2() {
        DeterministicFiniteAutomatonState a = 
                new DeterministicFiniteAutomatonState(0);
        
        DeterministicFiniteAutomatonState b = 
                new DeterministicFiniteAutomatonState(1);
        
        DeterministicFiniteAutomaton dfa = new DeterministicFiniteAutomaton();
        
        dfa.setInitialState(a);
        
        dfa.getAcceptingStateSet().addDeterministicFiniteAutomatonState(b);
        
        a.addFollowerState('1', a);
        a.addFollowerState('0', b);
        b.addFollowerState('0', b);
        b.addFollowerState('1', a);
        
        assertTrue(dfa.matches("110"));
        assertTrue(dfa.matches("10"));
        assertTrue(dfa.matches("0"));
        
        assertFalse(dfa.matches("a0"));
        assertFalse(dfa.matches(""));
        assertFalse(dfa.matches("1"));
        assertFalse(dfa.matches("01"));
        assertFalse(dfa.matches("11"));
    }
    
    @Test
    public void hopcroft1() {
        DeterministicFiniteAutomaton dfa = new DeterministicFiniteAutomaton();
        
        DeterministicFiniteAutomatonState a = 
                new DeterministicFiniteAutomatonState(0);
        
        DeterministicFiniteAutomatonState b = 
                new DeterministicFiniteAutomatonState(1);
        
        dfa.setInitialState(a);
        dfa.getAcceptingStateSet().addDeterministicFiniteAutomatonState(a);
        dfa.getAcceptingStateSet().addDeterministicFiniteAutomatonState(b);
        a.addFollowerState('a', b);
        b.addFollowerState('a', b);
        
        assertTrue(dfa.matches(""));
        assertTrue(dfa.matches("a"));
        assertTrue(dfa.matches("aa"));
        assertFalse(dfa.matches("b"));
        
        assertEquals(2, dfa.getNumberOfStates());
        
        DeterministicFiniteAutomaton dfa2 = dfa.minimizeViaHopcroftAlgorithm();
        
        assertTrue(dfa2.matches(""));
        assertTrue(dfa2.matches("a"));
        assertTrue(dfa2.matches("aa"));
        assertFalse(dfa2.matches("b"));
        
        assertEquals(1, dfa2.getNumberOfStates());
    }
}
