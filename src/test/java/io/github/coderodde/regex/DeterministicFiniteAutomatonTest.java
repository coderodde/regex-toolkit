package io.github.coderodde.regex;

import static io.github.coderodde.regex.DeterministicFiniteAutomaton.MinimizationAlgorithm.HOPCROFT;
import static io.github.coderodde.regex.DeterministicFiniteAutomaton.MinimizationAlgorithm.MOORE;
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
        
        dfa.addAcceptingState(q2);
        
        dfa.addTransition(q0, '1', q0);
        dfa.addTransition(q0, '0', q1);
        dfa.addTransition(q1, '0', q1);
        dfa.addTransition(q1, '1', q2);
        dfa.addTransition(q2, '0', q1);
        dfa.addTransition(q2, '1', q0);
        
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
        
        dfa.addAcceptingState(b);
        
        dfa.addTransition(a, '1', a);
        dfa.addTransition(a, '0', b);
        dfa.addTransition(b, '0', b);
        dfa.addTransition(b, '1', a);
        
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
        dfa.addAcceptingState(a);
        dfa.addAcceptingState(b);
        
        dfa.addTransition(a, 'a', b);
        dfa.addTransition(b, 'a', b);
        
        assertTrue(dfa.matches(""));
        assertTrue(dfa.matches("a"));
        assertTrue(dfa.matches("aa"));
        assertFalse(dfa.matches("b"));
        
        assertEquals(2, dfa.getNumberOfStates());
        
        DeterministicFiniteAutomaton dfa2 = dfa.minimize(HOPCROFT);
        
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
        dfa.addAcceptingState(c);
        dfa.addAcceptingState(d);
        dfa.addAcceptingState(e);
        
        dfa.addTransition(a, '0', b);
        dfa.addTransition(a, '1', c);
        dfa.addTransition(b, '0', a);
        dfa.addTransition(b, '1', d);
        dfa.addTransition(c, '0', e);
        dfa.addTransition(c, '1', f);
        dfa.addTransition(d, '0', e);
        dfa.addTransition(d, '1', f);
        dfa.addTransition(e, '0', e);
        dfa.addTransition(e, '1', f);
        dfa.addTransition(f, '0', f);
        dfa.addTransition(f, '1', f);
        
        assertEquals(6, dfa.getNumberOfStates());
        
        assertTrue(dfa.matches("1"));
        assertTrue(dfa.matches("01"));
        assertTrue(dfa.matches("10"));
        assertTrue(dfa.matches("100"));
        
        assertFalse(dfa.matches("0"));
        assertFalse(dfa.matches("011"));
        assertFalse(dfa.matches("0110"));
        assertFalse(dfa.matches("0111"));
        
        DeterministicFiniteAutomaton dfa2 = dfa.minimize(HOPCROFT);
        
        assertEquals(3, dfa2.getNumberOfStates());
        
        assertTrue(dfa2.matches("1"));
        assertTrue(dfa2.matches("01"));
        assertTrue(dfa2.matches("10"));
        assertTrue(dfa2.matches("100"));
        
        assertFalse(dfa2.matches("0"));
        assertFalse(dfa2.matches("011"));
        assertFalse(dfa2.matches("0110"));
        assertFalse(dfa2.matches("0111"));
    }
    
    
    @Test
    public void moore1() {
        DeterministicFiniteAutomaton dfa = new DeterministicFiniteAutomaton();
        
        DeterministicFiniteAutomatonState a = 
                new DeterministicFiniteAutomatonState(0);
        
        DeterministicFiniteAutomatonState b = 
                new DeterministicFiniteAutomatonState(1);
        
        dfa.setInitialState(a);
        dfa.addAcceptingState(a);
        dfa.addAcceptingState(b);
        
        dfa.addTransition(a, 'a', b);
        dfa.addTransition(b, 'a', b);
        
        assertTrue(dfa.matches(""));
        assertTrue(dfa.matches("a"));
        assertTrue(dfa.matches("aa"));
        assertFalse(dfa.matches("b"));
        
        assertEquals(2, dfa.getNumberOfStates());
        
        DeterministicFiniteAutomaton dfa2 = dfa.minimize(MOORE);
        
        assertTrue(dfa2.matches(""));
        assertTrue(dfa2.matches("a"));
        assertTrue(dfa2.matches("aa"));
        assertFalse(dfa2.matches("b"));
        
        assertEquals(1, dfa2.getNumberOfStates());
    }
    
    @Test
    public void moore2() {
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
        dfa.addAcceptingState(c);
        dfa.addAcceptingState(d);
        dfa.addAcceptingState(e);
        
        dfa.addTransition(a, '0', b);
        dfa.addTransition(a, '1', c);
        dfa.addTransition(b, '0', a);
        dfa.addTransition(b, '1', d);
        dfa.addTransition(c, '0', e);
        dfa.addTransition(c, '1', f);
        dfa.addTransition(d, '0', e);
        dfa.addTransition(d, '1', f);
        dfa.addTransition(e, '0', e);
        dfa.addTransition(e, '1', f);
        dfa.addTransition(f, '0', f);
        dfa.addTransition(f, '1', f);
        
        assertEquals(6, dfa.getNumberOfStates());
        
        assertTrue(dfa.matches("1"));
        assertTrue(dfa.matches("01"));
        assertTrue(dfa.matches("10"));
        assertTrue(dfa.matches("100"));
        
        assertFalse(dfa.matches("0"));
        assertFalse(dfa.matches("011"));
        assertFalse(dfa.matches("0110"));
        assertFalse(dfa.matches("0111"));
        
        DeterministicFiniteAutomaton dfa2 = dfa.minimize(MOORE);
        
        assertEquals(3, dfa2.getNumberOfStates());
        
        assertTrue(dfa2.matches("1"));
        assertTrue(dfa2.matches("01"));
        assertTrue(dfa2.matches("10"));
        assertTrue(dfa2.matches("100"));
        
        assertFalse(dfa2.matches("0"));
        assertFalse(dfa2.matches("011"));
        assertFalse(dfa2.matches("0110"));
        assertFalse(dfa2.matches("0111"));
    }
    
    @Test
    public void compareMooresToHopcroftsAlg() {
        DeterministicFiniteAutomaton dfa = new DeterministicFiniteAutomaton();
        
        DeterministicFiniteAutomatonState a = new DeterministicFiniteAutomatonState(0);
        DeterministicFiniteAutomatonState b = new DeterministicFiniteAutomatonState(1);
        DeterministicFiniteAutomatonState c = new DeterministicFiniteAutomatonState(2);
        DeterministicFiniteAutomatonState d = new DeterministicFiniteAutomatonState(3);
        
        dfa.addAcceptingState(c);
        dfa.addAcceptingState(d);
        
        dfa.setInitialState(a);
        
        dfa.addTransition(a, '0', b);
        dfa.addTransition(a, '1', c);
        
        dfa.addTransition(b, '0', a);
        dfa.addTransition(b, '1', d);
        
        dfa.addTransition(c, '0', c);
        dfa.addTransition(c, '1', c);
        
        dfa.addTransition(d, '0', d);
        dfa.addTransition(d, '1', d);
        
        assertEquals(4, dfa.getNumberOfStates());
        
        DeterministicFiniteAutomaton dfa1 = dfa.minimize(HOPCROFT);
        DeterministicFiniteAutomaton dfa2 = dfa.minimize(MOORE);
        
        assertEquals(2, dfa1.getNumberOfStates());
        assertEquals(2, dfa2.getNumberOfStates());
    } 
     
//    @Test 
//    public void toNFAConversion() {
//        DeterministicFiniteAutomaton dfa = new DeterministicFiniteAutomaton();
//        
//        DeterministicFiniteAutomatonState q0 = 
//                new DeterministicFiniteAutomatonState(0);
//        
//        DeterministicFiniteAutomatonState q1 = 
//                new DeterministicFiniteAutomatonState(1);
//        
//        dfa.setInitialState(q0);
//        dfa.getAcceptingStates().add(q1);
//        
//        dfa.setInitialState(q0);
//        
//        q0.addFollowerState('0', q0);
//        q0.addFollowerState('1', q1);
//        q1.addFollowerState('0', q0);
//        q1.addFollowerState('1', q1);
//        
//        NondeterministicFiniteAutomaton nfa = 
//                dfa.convertoToNondeterministicFiniteAutomaton();
//        
//        assertEquals(2, nfa.getNumberOfStates());
//        // TODO: compare by equals() (regex)
//    }

    @Test
    public void dotOperators1() {
        DeterministicFiniteAutomaton dfa = new DeterministicFiniteAutomaton();
        
        DeterministicFiniteAutomatonState q0 =
            new DeterministicFiniteAutomatonState(0);
        
        DeterministicFiniteAutomatonState a = 
            new DeterministicFiniteAutomatonState(1);
        
        DeterministicFiniteAutomatonState b = 
            new DeterministicFiniteAutomatonState(2);
        
        dfa.setInitialState(q0);
        
        dfa.addTransition(q0, 'a', a);
        
        dfa.addDotTransition(q0, b);
        
        dfa.addAcceptingState(b);
        
        assertTrue(dfa.matches("a"));
        assertTrue(dfa.matches("b"));
    }
}
