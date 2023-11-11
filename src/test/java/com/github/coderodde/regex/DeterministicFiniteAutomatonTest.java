package com.github.coderodde.regex;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class DeterministicFiniteAutomatonTest {
    
    @Test
    public void on10DFA() {
        DeterministicFiniteAutomatonState q0 = 
                new DeterministicFiniteAutomatonState("q0");
        
        DeterministicFiniteAutomatonState q1 = 
                new DeterministicFiniteAutomatonState("q1");
        
        DeterministicFiniteAutomatonState q2 = 
                new DeterministicFiniteAutomatonState("q2");
        
        DeterministicFiniteAutomaton dfa = 
                new DeterministicFiniteAutomaton(q0);
        
        dfa.getStateSet().addDeterministicFiniteAutomatonState(q1);
        dfa.getStateSet().addDeterministicFiniteAutomatonState(q2);
        dfa.getAcceptingStateSet().addDeterministicFiniteAutomatonState(q2);
        
        dfa.getTransitionFunction().connect(q0, q0, '1');
        dfa.getTransitionFunction().connect(q0, q1, '0');
        dfa.getTransitionFunction().connect(q1, q1, '0');
        dfa.getTransitionFunction().connect(q1, q2, '1');
        dfa.getTransitionFunction().connect(q2, q1, '0');
        dfa.getTransitionFunction().connect(q2, q0, '1');
        
        assertTrue(dfa.matches("1001101"));
        assertTrue(dfa.matches("01"));
        assertFalse(dfa.matches("00100"));
        assertFalse(dfa.matches("0"));
        assertFalse(dfa.matches(""));
    }
    
    @Test
    public void on10DFA2() {
        DeterministicFiniteAutomatonState a = 
                new DeterministicFiniteAutomatonState("a");
        
        DeterministicFiniteAutomatonState b = 
                new DeterministicFiniteAutomatonState("b");
        
        DeterministicFiniteAutomaton dfa = 
                new DeterministicFiniteAutomaton(a);
        
        dfa.getStateSet().addDeterministicFiniteAutomatonState(a);
        dfa.getStateSet().addDeterministicFiniteAutomatonState(b);
        dfa.getAcceptingStateSet().addDeterministicFiniteAutomatonState(b);
        
        dfa.getTransitionFunction().connect(a, a, '1');
        dfa.getTransitionFunction().connect(a, b, '0');
        dfa.getTransitionFunction().connect(b, b, '0');
        dfa.getTransitionFunction().connect(b, a, '1');
        
        assertTrue(dfa.matches("110"));
        assertTrue(dfa.matches("10"));
        assertTrue(dfa.matches("0"));
        
        assertFalse(dfa.matches(""));
        assertFalse(dfa.matches("1"));
        assertFalse(dfa.matches("01"));
        assertFalse(dfa.matches("11"));
    }
}
