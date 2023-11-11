package com.github.coderodde.regex;

import java.util.Objects;

/**
 *
 * @author rodio
 */
public final class DeterministicFiniteAutomaton {
    
    private final DeterministicFiniteAutomatonState initialState;
    private final DeterministicFiniteAutomatonStateSet stateSet = 
              new DeterministicFiniteAutomatonStateSet();
    
    private final DeterministicFiniteAutomatonTransitionFunction 
            transitionFunction =
            new DeterministicFiniteAutomatonTransitionFunction();
    
    public DeterministicFiniteAutomaton(
            DeterministicFiniteAutomatonState initialState) {
        this.initialState = 
                Objects.requireNonNull(
                        initialState,
                        "The input initial state is null.");
    }
    
    public boolean matches(String text) {
        
        return false;
    }
}
