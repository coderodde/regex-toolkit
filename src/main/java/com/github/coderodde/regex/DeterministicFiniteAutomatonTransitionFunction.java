package com.github.coderodde.regex;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author rodio
 */
public final class DeterministicFiniteAutomatonTransitionFunction {
    
    private final Map<DeterministicFiniteAutomatonState,
                      Map<Character, DeterministicFiniteAutomatonState>> map =
            new HashMap<>();
    
    public void connect(DeterministicFiniteAutomatonState sourceState,
                        DeterministicFiniteAutomatonState targetState,
                        Character character) {
        Objects.requireNonNull(sourceState, "The source state is null.");
        Objects.requireNonNull(targetState, "The target state is null.");
        Objects.requireNonNull(character, "The input character is null.");
        
        if (!map.containsKey(sourceState)) {
            map.put(sourceState, new HashMap<>());
        }
        
        map.get(sourceState).put(character, targetState);
    }
    
    public DeterministicFiniteAutomatonState 
        runTransition(DeterministicFiniteAutomatonState sourceState, 
                      Character character) {
        
        if (!map.containsKey(sourceState)) {
            return null;
        }
        
        return map.get(sourceState).get(character);
    }
}
