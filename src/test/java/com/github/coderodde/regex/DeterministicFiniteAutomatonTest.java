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
        
        dfa.getAcceptingStates().add(q2);
        
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
        
        dfa.getAcceptingStates().add(b);
        
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
        dfa.getAcceptingStates().add(a);
        dfa.getAcceptingStates().add(b);
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
    
    @Test
    public void hopcroft2() {
        DeterministicFiniteAutomaton dfa = new DeterministicFiniteAutomaton();
        
        DeterministicFiniteAutomatonState a =
                new DeterministicFiniteAutomatonState(0);
        
        DeterministicFiniteAutomatonState b =
                new DeterministicFiniteAutomatonState(1);
        
        DeterministicFiniteAutomatonState c =
                new DeterministicFiniteAutomatonState(2);
        
        DeterministicFiniteAutomatonState d =
                new DeterministicFiniteAutomatonState(3);
        
        DeterministicFiniteAutomatonState e =
                new DeterministicFiniteAutomatonState(4);
        
        DeterministicFiniteAutomatonState f =
                new DeterministicFiniteAutomatonState(5);
        
        dfa.setInitialState(a);
        dfa.getAcceptingStates().add(c);
        dfa.getAcceptingStates().add(d);
        dfa.getAcceptingStates().add(e);
        
        a.addFollowerState('0', b);
        a.addFollowerState('1', c);
        b.addFollowerState('0', a);
        b.addFollowerState('1', d);
        c.addFollowerState('0', e);
        c.addFollowerState('1', f);
        d.addFollowerState('0', e);
        d.addFollowerState('1', f);
        e.addFollowerState('0', e);
        e.addFollowerState('1', f);
        f.addFollowerState('0', f);
        f.addFollowerState('1', f);
        
        assertEquals(6, dfa.getNumberOfStates());
        
        assertTrue(dfa.matches("1"));
        assertTrue(dfa.matches("01"));
        assertTrue(dfa.matches("10"));
        assertTrue(dfa.matches("100"));
        
        assertFalse(dfa.matches("0"));
        assertFalse(dfa.matches("011"));
        assertFalse(dfa.matches("0110"));
        assertFalse(dfa.matches("0111"));
        
        DeterministicFiniteAutomaton dfa2 = dfa.minimizeViaHopcroftAlgorithm();
        
        assertEquals(3, dfa2.getNumberOfStates());
    }
    
    @Test
    public void toNFAConversion() {
        DeterministicFiniteAutomaton dfa = new DeterministicFiniteAutomaton();
        
        DeterministicFiniteAutomatonState q0 = 
                new DeterministicFiniteAutomatonState(0);
        
        DeterministicFiniteAutomatonState q1 = 
                new DeterministicFiniteAutomatonState(1);
        
        dfa.setInitialState(q0);
        dfa.getAcceptingStates().add(q1);
        
        dfa.setInitialState(q0);
        
        q0.addFollowerState('0', q0);
        q0.addFollowerState('1', q1);
        q1.addFollowerState('0', q0);
        q1.addFollowerState('1', q1);
        
        NondeterministicFiniteAutomaton nfa = 
                dfa.convertoToNondeterministicFiniteAutomaton();
        
        assertEquals(2, nfa.getNumberOfStates());
        // TODO: compare by equals() (regex)
    }
}
