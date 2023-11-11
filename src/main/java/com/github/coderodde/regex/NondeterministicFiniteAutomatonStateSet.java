package com.github.coderodde.regex;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class implements the set of all states in a DFA.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 11, 2023)
 * @since 1.6 (Nov 11, 2023)
 */
public final class NondeterministicFiniteAutomatonStateSet {
    
    
    private final Map<String, NondeterministicFiniteAutomatonState> stateMap = 
            new HashMap<>();
    
    /**
     * Holds the actual DFA states.
     */
    private final Set<NondeterministicFiniteAutomatonState> stateSet = 
            new HashSet<>();
    
    /**
     * Adds the input state to this state set.
     * 
     * @param state the state to add.
     * @return {@code true} only if the state set has changed, i.e., the state
     *         is added.
     */
    public boolean addDeterministicFiniteAutomatonState(
            NondeterministicFiniteAutomatonState state) {
        
        if (stateSet.contains(state)) {
            return false;
        }
        
        stateSet.add(state);
        stateMap.put(state.getStateName(), state);
        return true;
    }
    
    /**
     * Exposes the entire state set.
     * 
     * @return the entire state set.
     */
    public Set<NondeterministicFiniteAutomatonState> getStateSet() {
        return Collections.<NondeterministicFiniteAutomatonState>
                unmodifiableSet(stateSet);
    }
}
