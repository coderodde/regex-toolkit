package com.github.coderodde.regex;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public class NondeterministicFiniteAutomatonTransitionFunctionTest {
    
    @Test
    public void transitionFunctionWorks() {
        NondeterministicFiniteAutomatonTransitionFunction transitionFunction = 
                new NondeterministicFiniteAutomatonTransitionFunction();
        
        NondeterministicFiniteAutomatonState state1 = 
                new NondeterministicFiniteAutomatonState("q0");
        
        NondeterministicFiniteAutomatonState state2 = 
                new NondeterministicFiniteAutomatonState("q1");
        
        NondeterministicFiniteAutomatonState state3 = 
                new NondeterministicFiniteAutomatonState("q2");
        
        assertNull(
                transitionFunction.runTransition(state1, Character.MIN_VALUE));
        
        transitionFunction.connect(state1, state2, '0');
        transitionFunction.connect(state1, state3, '0');
        
        assertNull(transitionFunction.runTransition(state1, 'a'));
        
        Set<NondeterministicFiniteAutomatonState> successors = new HashSet<>();
        successors.add(state2);
        successors.add(state3);
        
        assertEquals(successors, transitionFunction.runTransition(state1, '0'));
    }
}
