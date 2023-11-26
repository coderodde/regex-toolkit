package com.github.coderodde.regex;

/**
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 25, 2023)
 * @since 1.6 (Nov 25, 2023)
 */
public final class GeneralizedNondeterministicFiniteAutomaton { // TODO: remove public?
    
    private GeneralizedNondeterministicFiniteAutomatonState initialState;
    private GeneralizedNondeterministicFiniteAutomatonState acceptingState;
    private int numberOfStates;
    
    GeneralizedNondeterministicFiniteAutomatonState getInitialState() {
        return initialState;
    }
    
    GeneralizedNondeterministicFiniteAutomatonState getAcceptingState() {
        return acceptingState;
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
        return null;
    }
    
    private void ripImpl(
            GeneralizedNondeterministicFiniteAutomatonState stateToRip) {
        
    }
}
