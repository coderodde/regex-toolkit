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
}
