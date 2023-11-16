package com.github.coderodde.regex;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author PotilasKone
 */
public final class NondeterministicFiniteAutomaton {
    
    private NondeterministicFiniteAutomatonState initialState;
    
    private final NondeterministicFiniteAutomatonStateSet stateSet = 
              new NondeterministicFiniteAutomatonStateSet();
    
    private final NondeterministicFiniteAutomatonAcceptingStateSet 
                  acceptingStateSet = 
              new NondeterministicFiniteAutomatonAcceptingStateSet();
    
    private final NondeterministicFiniteAutomatonTransitionFunction
                  transitionFunction = 
              new NondeterministicFiniteAutomatonTransitionFunction();
    
    public void setInitialState(
            NondeterministicFiniteAutomatonState initialState) {
        this.initialState = 
                Objects.requireNonNull(
                        initialState,
                        "The input initial state is null.");
        
        stateSet.addDeterministicFiniteAutomatonState(initialState);
    }
    
    public NondeterministicFiniteAutomatonStateSet getStateSet() {
        return stateSet;
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
        Set<NondeterministicFiniteAutomatonState> finalStateSet = runNFA(text);
        
        if (finalStateSet == null) {
            return false;
        }
    
        return isAcceptingStateSet(finalStateSet);
    }
    
    private Set<NondeterministicFiniteAutomatonState> runNFA(String text) {
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
                
                if (nextState == null) {
                    return null;
                }
                
                nextStates.addAll(nextState);
            }
                
            currentStates = epsilonExpand(nextStates);
        }
        
        return currentStates;
    }
    
    private Set<NondeterministicFiniteAutomatonState> 
        epsilonExpand(Set<NondeterministicFiniteAutomatonState> set) {
            
        Set<NondeterministicFiniteAutomatonState> expandedSet = 
                new HashSet<>(set);
        
        Deque<NondeterministicFiniteAutomatonState> queue = new ArrayDeque<>();
        Set<NondeterministicFiniteAutomatonState> visited = new HashSet<>();
        
        for (NondeterministicFiniteAutomatonState state : expandedSet) {
            if (!visited.contains(state)) {
                visited.add(state);
                queue.addLast(state);
            } else {
                System.out.println("yeah");
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
                } else {
                    System.out.println("fuck");
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
                acceptingStateSet.getAcceptingStateSet().size()) {
            smallSet = finalStateSet;
            largeSet = acceptingStateSet.getAcceptingStateSet();
        } else {
            smallSet = acceptingStateSet.getAcceptingStateSet();
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
