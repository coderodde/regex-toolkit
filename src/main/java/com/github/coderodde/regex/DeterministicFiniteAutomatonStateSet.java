package com.github.coderodde.regex;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author rodio
 */
public final class DeterministicFiniteAutomatonStateSet {
    
    private final Map<String, DeterministicFiniteAutomatonState> stateMap = 
            new HashMap<>();
    
    private final Set<DeterministicFiniteAutomatonState> stateSet = 
            new HashSet<>();
    
    public boolean addDeterministicFiniteAutomatonState(
            DeterministicFiniteAutomatonState state) {
        
        if (stateSet.contains(state)) {
            return false;
        }
        
        stateSet.add(state);
        stateMap.put(state.getStateName(), state);
        return true;
    }
    
    public Set<DeterministicFiniteAutomatonState> getStateSet() {
        return Collections.<DeterministicFiniteAutomatonState>
                unmodifiableSet(stateSet);
    }
}
