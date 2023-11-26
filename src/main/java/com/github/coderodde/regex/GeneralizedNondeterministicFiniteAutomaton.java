package com.github.coderodde.regex;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 25, 2023)
 * @since 1.6 (Nov 25, 2023)
 */
public final class GeneralizedNondeterministicFiniteAutomaton { // TODO: remove public?
    
    private GeneralizedNondeterministicFiniteAutomatonState initialState;
    private GeneralizedNondeterministicFiniteAutomatonState acceptingState;
    private Set<GeneralizedNondeterministicFiniteAutomatonState> stateSet = 
            new HashSet<>();
    
    private int numberOfStates;
    
    GeneralizedNondeterministicFiniteAutomatonState getInitialState() {
        return initialState;
    }
    
    GeneralizedNondeterministicFiniteAutomatonState getAcceptingState() {
        return acceptingState;
    }
    
    void addState(GeneralizedNondeterministicFiniteAutomatonState state) {
        stateSet.add(state);
    }
    
    void setNumberOfStates(int numberOfStates) {
        this.numberOfStates = numberOfStates;
    }
    
    void setInitialState(
            GeneralizedNondeterministicFiniteAutomatonState acceptingState) {
        this.acceptingState = acceptingState;
    }
    
    void setAcceptingState(
            GeneralizedNondeterministicFiniteAutomatonState initialState) {
        this.initialState = initialState;
    }
    
    int getNumberOfStates() {
        return numberOfStates;
    }
    
    void rip() {
        GeneralizedNondeterministicFiniteAutomatonState rippedState = 
                getStateToRip();
        
        ripImpl(rippedState);
        numberOfStates--;
    }
    
    private GeneralizedNondeterministicFiniteAutomatonState getStateToRip() {
        for (GeneralizedNondeterministicFiniteAutomatonState state : stateSet) {
            
        }
    }
    
    private void ripImpl(
            GeneralizedNondeterministicFiniteAutomatonState stateToRip) {
        
    }
}
