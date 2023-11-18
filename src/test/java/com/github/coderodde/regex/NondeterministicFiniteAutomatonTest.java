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
        
        NondeterministicFiniteAutomatonTransitionFunction transitionFunction = 
                new NondeterministicFiniteAutomatonTransitionFunction();
        
        NondeterministicFiniteAutomatonState q0 = 
                new NondeterministicFiniteAutomatonState(0, transitionFunction);
        
        NondeterministicFiniteAutomatonState q1 = 
                new NondeterministicFiniteAutomatonState(1, transitionFunction);
        
        NondeterministicFiniteAutomatonState q2 = 
                new NondeterministicFiniteAutomatonState(2, transitionFunction);
        
        nfa.setInitialState(q0);
        
        NondeterministicFiniteAutomatonTransitionFunction f = 
                nfa.getTransitionFunction();
        
        f.connect(q0, q1, 'a');
        
        f.addEpsilonConnection(q0, q1);
        f.addEpsilonConnection(q1, q2);
        f.addEpsilonConnection(q2, q0);
        
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
        
        NondeterministicFiniteAutomatonTransitionFunction transitionFunction = 
                new NondeterministicFiniteAutomatonTransitionFunction();
        
        NondeterministicFiniteAutomatonState q0 = 
                new NondeterministicFiniteAutomatonState(0, transitionFunction);
        
        NondeterministicFiniteAutomatonState q1 = 
                new NondeterministicFiniteAutomatonState(1, transitionFunction);
        
        NondeterministicFiniteAutomatonState q2 = 
                new NondeterministicFiniteAutomatonState(2, transitionFunction);
        
        NondeterministicFiniteAutomatonState q3 = 
                new NondeterministicFiniteAutomatonState(3, transitionFunction);
        
        nfa.setInitialState(q0);
        
        nfa.getAcceptingStateSet().addNondeterministicFiniteAutomatonState(q3);
        
        NondeterministicFiniteAutomatonTransitionFunction f = 
                nfa.getTransitionFunction();
        
        f.connect(q0, q0, 'a');
        f.connect(q0, q0, 'b');
        f.connect(q0, q1, 'b'); 
        f.connect(q1, q2, 'a');
        f.connect(q2, q3, 'b');
        f.connect(q3, q2, 'a');
        
        f.addEpsilonConnection(q1, q2);
        
        assertTrue(nfa.matches("bb"));
        assertTrue(nfa.matches("abbab"));
        assertTrue(nfa.matches("abab"));
        
        assertFalse(nfa.matches("ba"));
        assertFalse(nfa.matches("ababa"));
    }
}
