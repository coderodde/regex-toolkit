package com.github.coderodde.regex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 25, 2023)
 * @since 1.6 (Nov 25, 2023)
 */
final class GeneralizedNondeterministicFiniteAutomatonState {
    
    private final int id;
    
    private final Map<GeneralizedNondeterministicFiniteAutomatonState, 
                      String> map = new HashMap<>();
    
    private final Set<GeneralizedNondeterministicFiniteAutomatonState>
            incomingStates = new HashSet<>();
    
    private final Set<GeneralizedNondeterministicFiniteAutomatonState>
            epsilonSet = new HashSet<>();

    GeneralizedNondeterministicFiniteAutomatonState(int id) {
        this.id = id;
    }
    
    @Override
    public boolean equals(Object o) {
        GeneralizedNondeterministicFiniteAutomatonState other = 
                (GeneralizedNondeterministicFiniteAutomatonState) o;
        
        return id == other.id;
    }
    
    @Override
    public int hashCode() {
        return id;
    }
    
    void addRegularExpression(
            GeneralizedNondeterministicFiniteAutomatonState followerState, 
            String regularExpression) {
        
        map.put(followerState, regularExpression);
        followerState.incomingStates.add(this);
    }
   
    void addEpsilonTransition(
            GeneralizedNondeterministicFiniteAutomatonState state) {
        
        epsilonSet.add(state);
    }
    
    Set<GeneralizedNondeterministicFiniteAutomatonState> getIncomingStates() {
        return incomingStates;
    }
    
    Set<GeneralizedNondeterministicFiniteAutomatonState> getOutgoingStates() {
        return map.keySet();
    }
    
    String getRegularExpression(
            GeneralizedNondeterministicFiniteAutomatonState state) {
        return map.get(state);
    }
}
