package io.github.coderodde.regex;

import io.github.coderodde.regex.GeneralizedNondeterministicFiniteAutomaton.CharacterClassString;
import java.util.HashMap;
import java.util.HashSet;
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
            incomingStates = new HashSet<>();
    
    GeneralizedNondeterministicFiniteAutomatonState(int id) {
        this.id = id;
    }
    
    @Override
    public boolean equals(Object o) {
        return o instanceof 
            GeneralizedNondeterministicFiniteAutomatonState other 
            && other.id == id;
    }
    
    @Override
    public int hashCode() {
        return id;
    }
    
    String getRegularExpression(
        GeneralizedNondeterministicFiniteAutomatonState followerState) {
        
        return map.get(followerState);
    }
    
    void setRegularExpression(
        GeneralizedNondeterministicFiniteAutomatonState followerState, 
        String regex) {
        
        map.put(followerState, regex);
    }
    
    void removeRegularExpression(
        GeneralizedNondeterministicFiniteAutomatonState targetState) {
        
        String removeRegex = map.remove(targetState);
    
        if (removeRegex != null) {
            targetState.incomingStates.remove(this);
        }
    }
    
    void addEpsilonTransition(
        GeneralizedNondeterministicFiniteAutomatonState nextState) {
        setRegularExpression(nextState, null);
    }
    
    void clearTransitions() {
        for (GeneralizedNondeterministicFiniteAutomatonState target 
            : new HashSet<>(map.keySet())) {
            
            removeRegularExpression(target);
        }
        
        for (GeneralizedNondeterministicFiniteAutomatonState source
            : new HashSet<>(incomingStates)) {
            
            source.removeRegularExpression(this);
        }
        
        incomingStates.clear();
    }
    
    Set<GeneralizedNondeterministicFiniteAutomatonState> getIncomingStates() {
        return incomingStates;
    }
    
    Set<GeneralizedNondeterministicFiniteAutomatonState> getOutgoingStates() {
        return map.keySet();
    }
}
