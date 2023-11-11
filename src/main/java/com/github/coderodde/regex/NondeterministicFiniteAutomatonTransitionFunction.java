package com.github.coderodde.regex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author PotilasKone
 */
public final class NondeterministicFiniteAutomatonTransitionFunction {
    
    private final Map<NondeterministicFiniteAutomatonState,
                      Map<Character,
                          Set<NondeterministicFiniteAutomatonState>>> map = 
            new HashMap<>();
    
    public void connect(NondeterministicFiniteAutomatonState sourceState,
                        NondeterministicFiniteAutomatonState targetState,
                        Character character) {
        
        Objects.requireNonNull(sourceState, "The source state is null.");
        Objects.requireNonNull(targetState, "The target state is null.");
        Objects.requireNonNull(character, "The input character is null.");
        
        if (!map.containsKey(sourceState)) {
            map.put(sourceState, new HashMap<>());
            map.get(sourceState).put(
                    character, 
                    new HashSet<>(Arrays.asList(targetState)));
        }
        
        map.get(sourceState).get(character).add(targetState);
    }
}
