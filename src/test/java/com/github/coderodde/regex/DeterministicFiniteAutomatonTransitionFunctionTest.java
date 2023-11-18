package com.github.coderodde.regex;

import org.junit.Test;
import static org.junit.Assert.*;

public class DeterministicFiniteAutomatonTransitionFunctionTest {

    @Test
    public void test() {
        DeterministicFiniteAutomatonState state1 = 
                new DeterministicFiniteAutomatonState(0);
        
        DeterministicFiniteAutomatonState state2 = 
                new DeterministicFiniteAutomatonState(1);
        
        DeterministicFiniteAutomatonState state3 = 
                new DeterministicFiniteAutomatonState(2);
        
        DeterministicFiniteAutomatonTransitionFunction transitionFunction = 
                new DeterministicFiniteAutomatonTransitionFunction();
        
        transitionFunction.connect(state1, state2, '0');
        transitionFunction.connect(state1, state3, '1');
        
        assertNull(transitionFunction.runTransition(state3, '0'));
        assertNull(transitionFunction.runTransition(state3, '1'));
        
        assertEquals(state2, transitionFunction.runTransition(state1, '0'));
        assertEquals(state3, transitionFunction.runTransition(state1, '1'));
    }
}
