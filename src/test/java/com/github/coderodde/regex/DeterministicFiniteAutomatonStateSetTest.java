package com.github.coderodde.regex;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

public class DeterministicFiniteAutomatonStateSetTest {
    
    @Test
    public void test() {
        DeterministicFiniteAutomatonState state1 = 
                new DeterministicFiniteAutomatonState("q1");
        
        DeterministicFiniteAutomatonState state2 = 
                new DeterministicFiniteAutomatonState("q2");
        
        DeterministicFiniteAutomatonStateSet stateSet = 
                new DeterministicFiniteAutomatonStateSet();
        
        assertTrue(stateSet.getStateSet().isEmpty());
        assertEquals(0, stateSet.getStateSet().size());
        
        stateSet.addDeterministicFiniteAutomatonState(state1);
        
        assertFalse(stateSet.getStateSet().isEmpty());
        assertEquals(1, stateSet.getStateSet().size());
        
        assertEquals(state1, stateSet.getStateSet().iterator().next());
        
        stateSet.addDeterministicFiniteAutomatonState(state1);
        
        assertFalse(stateSet.getStateSet().isEmpty());
        assertEquals(1, stateSet.getStateSet().size());
        
        assertEquals(state1, stateSet.getStateSet().iterator().next());
        
        stateSet.addDeterministicFiniteAutomatonState(state2);
        
        assertFalse(stateSet.getStateSet().isEmpty());
        assertEquals(2, stateSet.getStateSet().size());
        
        Iterator<DeterministicFiniteAutomatonState> iter =
                stateSet.getStateSet().iterator();
        
        DeterministicFiniteAutomatonState first = iter.next();
        DeterministicFiniteAutomatonState second = iter.next();
        
        List<DeterministicFiniteAutomatonState> lst = 
                Arrays.asList(first, second);
        
        lst.sort((DeterministicFiniteAutomatonState s1,
                  DeterministicFiniteAutomatonState s2) -> {
            return s1.getStateName().compareTo(s2.getStateName());
        });
        
        assertEquals(state1, lst.get(0));
        assertEquals(state2, lst.get(1));
    }
}
