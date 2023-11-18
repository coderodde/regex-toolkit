package com.github.coderodde.regex;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 17, 2023)
 * @since 1.6 (Nov 17, 2023)
 */
public final class NondeterministicFiniteAutomaton {
    
    private NondeterministicFiniteAutomatonState initialState;
    
    private final NondeterministicFiniteAutomatonAcceptingStateSet 
                  acceptingStateSet = 
              new NondeterministicFiniteAutomatonAcceptingStateSet();
    
    private final NondeterministicFiniteAutomatonTransitionFunction
                  transitionFunction = 
              new NondeterministicFiniteAutomatonTransitionFunction();
    
    public NondeterministicFiniteAutomatonState getInititalState() {
        return initialState;
    }
    
    public void setInitialState(
            NondeterministicFiniteAutomatonState initialState) {
        this.initialState = 
                Objects.requireNonNull(
                        initialState,
                        "The input initial state is null.");
    }
    
    public NondeterministicFiniteAutomatonAcceptingStateSet 
        getAcceptingStateSet() {
        return acceptingStateSet;
    }
        
    public NondeterministicFiniteAutomatonTransitionFunction 
        getTransitionFunction() {
        return transitionFunction;
    }
        
    public boolean matches(String text) {
        Set<NondeterministicFiniteAutomatonState> finalStateSet = 
                simulateNFA(text);
        
        if (finalStateSet == null) {
            return false;
        }
    
        return isAcceptingStateSet(finalStateSet);
    }
    
    public DeterministicFiniteAutomaton convertToDFA() {
        Set<NondeterministicFiniteAutomatonState> startState =
                new HashSet<>(Arrays.asList(initialState));
        
        startState = epsilonExpand(startState);
        
        Map<Set<NondeterministicFiniteAutomatonState>, 
            DeterministicFiniteAutomatonState> stateMap = new HashMap<>();
        
        return null;
    }
    
    private Set<NondeterministicFiniteAutomatonState> simulateNFA(String text) {
        Set<NondeterministicFiniteAutomatonState> startSet = 
                new HashSet<>(Arrays.asList(initialState));
        
        Set<NondeterministicFiniteAutomatonState> currentStates =
                epsilonExpand(startSet);
        
        for (int i = 0; i != text.length(); i++) {
            char ch = text.charAt(i);
            
            Set<NondeterministicFiniteAutomatonState> nextStates = 
                    new HashSet<>();
        
            for (NondeterministicFiniteAutomatonState q : currentStates) {
                Set<NondeterministicFiniteAutomatonState> nextState = 
                        getTransitionFunction().runTransition(q, ch);
                
                if (nextState != null) {
                    nextStates.addAll(nextState);
                }
            }
                
            currentStates = epsilonExpand(nextStates);
        }
        
        return currentStates;
    }
    
    Set<NondeterministicFiniteAutomatonState> 
        epsilonExpand(Set<NondeterministicFiniteAutomatonState> set) {
            
        Set<NondeterministicFiniteAutomatonState> expandedSet = 
                new HashSet<>(set);
        
        Deque<NondeterministicFiniteAutomatonState> queue = new ArrayDeque<>();
        Set<NondeterministicFiniteAutomatonState> visited = new HashSet<>();
        
        for (NondeterministicFiniteAutomatonState state : expandedSet) {
            if (!visited.contains(state)) {
                visited.add(state);
                queue.addLast(state);
            }
        }
        
        while (!queue.isEmpty()) {
            NondeterministicFiniteAutomatonState state = queue.removeFirst();
            Set<NondeterministicFiniteAutomatonState> epsilonFollowerStates = 
                    getTransitionFunction().getEpsilonFollowerStates(state);
            
            for (NondeterministicFiniteAutomatonState s :
                    epsilonFollowerStates) {
                if (!visited.contains(s)) {
                    visited.add(s);
                    queue.addLast(s);
                    expandedSet.add(s);
                }
            }
        }
        
        return expandedSet;
    }
    
    private boolean isAcceptingStateSet(
            Set<NondeterministicFiniteAutomatonState> finalStateSet) {
        Set<NondeterministicFiniteAutomatonState> smallSet;
        Set<NondeterministicFiniteAutomatonState> largeSet;
        
        if (finalStateSet.size() < 
                acceptingStateSet.getStates().size()) {
            smallSet = finalStateSet;
            largeSet = acceptingStateSet.getStates();
        } else {
            smallSet = acceptingStateSet.getStates();
            largeSet = finalStateSet;
        }
        
        for (NondeterministicFiniteAutomatonState state : smallSet) {
            if (largeSet.contains(state)) {
                return true;
            }
        }
        
        return false;
    }
}
