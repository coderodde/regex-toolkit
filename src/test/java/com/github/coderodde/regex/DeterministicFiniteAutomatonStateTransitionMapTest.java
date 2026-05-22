package com.github.coderodde.regex;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class DeterministicFiniteAutomatonStateTransitionMapTest {
    
    @Test
    public void returnsNullOnNoMatchingCharacterRange() {
        DeterministicFiniteAutomatonStateTransitionFunction map = 
                new DeterministicFiniteAutomatonStateTransitionFunction();
        
        DeterministicFiniteAutomatonState state0 = 
                new DeterministicFiniteAutomatonState(0);
        
        DeterministicFiniteAutomatonState state1 = 
                new DeterministicFiniteAutomatonState(1);
        
        DeterministicFiniteAutomatonState goal0 =
                new DeterministicFiniteAutomatonState(2);
        
        DeterministicFiniteAutomatonState goal1 =
                new DeterministicFiniteAutomatonState(3);
        
        map.addTransition(new CodePointRange('a', 'c'), state0, goal0);
        map.addTransition(new CodePointRange('g', 'h'), state1, goal1);
        
        assertEquals(state0, map.getTargetState('a'));
        assertEquals(state0, map.getTargetState('b'));
        assertEquals(state0, map.getTargetState('c'));
        
        assertNull(map.getTargetState('d'));
        assertNull(map.getTargetState('e'));
        assertNull(map.getTargetState('f'));
        
        assertEquals(state1, map.getTargetState('g'));
        assertEquals(state1, map.getTargetState('h'));
        
        assertNull(map.getTargetState('i'));
        assertNull(map.getTargetState('j'));
        assertNull(map.getTargetState('k'));
    }
    
    @Test
    public void doesNotThrowOnLargeData() {
        DeterministicFiniteAutomatonStateTransitionFunction map =
                new DeterministicFiniteAutomatonStateTransitionFunction();
        
        List<DeterministicFiniteAutomatonState> states = new ArrayList<>(1000);
        
        for (int i = 0; i < 1_000; i++) {
            char c = (char)('a' + 2 * i);
            
            DeterministicFiniteAutomatonState state = 
                    new DeterministicFiniteAutomatonState(i);
            
            map.addTransition(new CodePointRange(c), state, null);
        }
        
        for (int i = 0; i < states.size(); i++) {
            char c = (char)('a' + 2 * i);
            
            DeterministicFiniteAutomatonState state = map.getTargetState(c);
            
            assertEquals(states.get(i), state);
        }
    }
}
