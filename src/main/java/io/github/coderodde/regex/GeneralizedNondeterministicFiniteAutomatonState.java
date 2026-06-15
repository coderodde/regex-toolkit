package io.github.coderodde.regex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class implements the 
 * <a href="https://en.wikipedia.org/wiki/Generalized_nondeterministic_finite_automaton">Generalized Nondeterministic Finite Automaton</a>.
 * It is used for converting DFAs into their respective regular languages.
 */
final class GeneralizedNondeterministicFiniteAutomatonState {
    
    private final int id;
    
    private final Map<GeneralizedNondeterministicFiniteAutomatonState, 
                      String> map = new HashMap<>();
    
    private final Set<GeneralizedNondeterministicFiniteAutomatonState> 
            epsilonSet = new HashSet<>();
    
    private final Set<GeneralizedNondeterministicFiniteAutomatonState>
            incomingStates = new HashSet<>();
    
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
    
    void setRegularExpression(
            GeneralizedNondeterministicFiniteAutomatonState followerState, 
            String regularExpression) {
        
        map.put(followerState, regularExpression);
        followerState.incomingStates.add(this);
    }
    
    void addEpsilonTransition(
        GeneralizedNondeterministicFiniteAutomatonState nextState) {
        
        epsilonSet.add(nextState);
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
