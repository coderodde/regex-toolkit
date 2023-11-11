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
public final class DeterministicFiniteAutomatonAcceptingStateSet {
    
    private final Map<String, DeterministicFiniteAutomatonState> stateMap = 
            new HashMap<>();
    
    private final Set<DeterministicFiniteAutomatonState> acceptiongStateSet = 
            new HashSet<>();
    
    public boolean addDeterministicFiniteAutomatonState(
            DeterministicFiniteAutomatonState state) {
        
        if (acceptiongStateSet.contains(state)) {
            return false;
        }
        
        acceptiongStateSet.add(state);
        stateMap.put(state.getStateName(), state);
        return true;
    }
    
    public Set<DeterministicFiniteAutomatonState> getAcceptingStateSet() {
        return Collections.<DeterministicFiniteAutomatonState>
                unmodifiableSet(acceptiongStateSet);
    }
}
