package com.github.coderodde.regex;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class implements sets for storing the accepting states of a DFA.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 11, 2023)
 * @since 1.6 (Nov 11, 2023)
 */
public class NondeterministicFiniteAutomatonAcceptingStateSet {
    
    private final Map<String, DeterministicFiniteAutomatonState> stateMap = 
            new HashMap<>();
    
    private final Set<NondeterministicFiniteAutomatonState> acceptiongStateSet = 
            new HashSet<>();
    
    public boolean addNondeterministicFiniteAutomatonState(
            NondeterministicFiniteAutomatonState state) {
        
        if (acceptiongStateSet.contains(state)) {
            return false;
        }
        
        acceptiongStateSet.add(state);
        stateMap.put(state.getStateName(), state);
        return true;
    }
    
    public Set<NondeterministicFiniteAutomatonState> getStates() {
        return Collections.<NondeterministicFiniteAutomatonState>
                unmodifiableSet(acceptiongStateSet);
    }
}
