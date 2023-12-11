package com.github.coderodde.regex;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class DeterministicFiniteAutomatonStateTransitionMapTest {
    
    @Test
    public void returnsNullOnNoMatchingCharacterRange() {
        DeterministicFiniteAutomatonStateTransitionMap map = 
                new DeterministicFiniteAutomatonStateTransitionMap();
        
        DeterministicFiniteAutomatonState state0 = 
                new DeterministicFiniteAutomatonState(0);
        
        DeterministicFiniteAutomatonState state1 = 
                new DeterministicFiniteAutomatonState(1);
        
        map.addTransition(new CharacterRange('a', 'c'), state0, false);
        map.addTransition(new CharacterRange('g', 'h'), state1, false);
        
        assertEquals(state0, map.getFollowerState('a'));
        assertEquals(state0, map.getFollowerState('b'));
        assertEquals(state0, map.getFollowerState('c'));
        
        assertNull(map.getFollowerState('d'));
        assertNull(map.getFollowerState('e'));
        assertNull(map.getFollowerState('f'));
        
        assertEquals(state1, map.getFollowerState('g'));
        assertEquals(state1, map.getFollowerState('h'));
        
        assertNull(map.getFollowerState('i'));
        assertNull(map.getFollowerState('j'));
        assertNull(map.getFollowerState('k'));
    }
    
    @Test
    public void doesNotThrowOnLargeData() {
        DeterministicFiniteAutomatonStateTransitionMap map =
                new DeterministicFiniteAutomatonStateTransitionMap();
        
        List<DeterministicFiniteAutomatonState> states = new ArrayList<>(1000);
        
        for (int i = 0; i < 1_000; i++) {
            char c = (char)('a' + 2 * i);
            
            DeterministicFiniteAutomatonState state = 
                    new DeterministicFiniteAutomatonState(i);
            
            map.addTransition(new CharacterRange(c), state, false);
        }
        
        for (int i = 0; i < states.size(); i++) {
            char c = (char)('a' + 2 * i);
            
            DeterministicFiniteAutomatonState state = map.getFollowerState(c);
            
            assertEquals(states.get(i), state);
        }
    }
}
