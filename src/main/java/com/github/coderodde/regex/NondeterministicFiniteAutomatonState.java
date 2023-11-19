package com.github.coderodde.regex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class implements the state in a non-deterministic finite automaton.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 11, 2023)
 * @since 1.6 (Nov 11, 2023)
 */
public final class NondeterministicFiniteAutomatonState 
        extends DeterministicFiniteAutomatonState {
    
    final Map<Character, Set<NondeterministicFiniteAutomatonState>> map = 
            new HashMap<>();
    
    private final Set<NondeterministicFiniteAutomatonState> epsilonSet = 
            new HashSet<>();
    
    /**
     * Constructs a state for a nondeterministic finite automaton.
     * 
     * @param id the ID of the state.
     */
    NondeterministicFiniteAutomatonState(int id) {
        super(id);
    }
    
    void addTransition(Character character, 
                       NondeterministicFiniteAutomatonState nextState) {
        if (!map.containsKey(character)) {
            map.put(character, new HashSet<>());
        }
            
        map.get(character).add(nextState);
    }
    
    void addEpsilonTransition(NondeterministicFiniteAutomatonState nextState) {
        epsilonSet.add(nextState);
    }
    
    Set<NondeterministicFiniteAutomatonState> 
        getFollowingStates(Character character) {
        return map.get(character);
    }
        
    Set<NondeterministicFiniteAutomatonState> getEpsilonStates() {
        return epsilonSet;
    }
}
