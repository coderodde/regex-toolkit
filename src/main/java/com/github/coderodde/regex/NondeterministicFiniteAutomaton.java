package com.github.coderodde.regex;

import java.util.Objects;
import java.util.Set;

/**
 *
 * @author PotilasKone
 */
public final class NondeterministicFiniteAutomaton {
    
    private NondeterministicFiniteAutomatonState inititalState;
    
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
        this.inititalState = 
                Objects.requireNonNull(
                        initialState,
                        "The input initial state is null.");
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
        return null;
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
