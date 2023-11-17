package com.github.coderodde.regex;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * This class implements a transition function for a NFA.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 12, 2023)
 * @since 1.6 (Nov 12, 2023)
 */
public final class NondeterministicFiniteAutomatonTransitionFunction {
    
    private final Map<NondeterministicFiniteAutomatonState,
                      Map<Character,
                          Set<NondeterministicFiniteAutomatonState>>> map = 
            new HashMap<>();
    
    private final Map<NondeterministicFiniteAutomatonState, 
                      Set<NondeterministicFiniteAutomatonState>> epsilonMap = 
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
        
        if (!map.get(sourceState).containsKey(character)) {
            map.get(sourceState).put(character, new HashSet<>());
        }
        
        map.get(sourceState).get(character).add(targetState);
    }
    
    public void addEpsilonConnection(
            NondeterministicFiniteAutomatonState fromState, 
            NondeterministicFiniteAutomatonState toState) {
        
        if (!epsilonMap.containsKey(fromState)) {
            epsilonMap.put(fromState, new HashSet<>());
        }
        
        epsilonMap.get(fromState).add(toState);
    }
    
    public Set<NondeterministicFiniteAutomatonState> 
        runTransition(
                NondeterministicFiniteAutomatonState sourceState, 
                Character character) {
            
        if (!map.containsKey(sourceState)) {
            return null;
        }
            
        return map.get(sourceState).get(character);
    }
        
    public Set<NondeterministicFiniteAutomatonState> 
        getEpsilonFollowerStates(NondeterministicFiniteAutomatonState state) {
        
        Set<NondeterministicFiniteAutomatonState> followerStates = epsilonMap.get(state);
        
        if (followerStates == null) {
            return Collections.<NondeterministicFiniteAutomatonState>emptySet();
        }
        
        return followerStates;
    }
}
