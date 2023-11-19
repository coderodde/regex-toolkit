package com.github.coderodde.regex;

import java.util.Objects;

/**
 * This class implements a deterministic finite automaton.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 11, 2023)
 * @since 1.6 (Nov 11, 2023)
 */
public final class DeterministicFiniteAutomaton {
    
    /**
     * The initial state of the DFA.
     */
    private DeterministicFiniteAutomatonState initialState;
    
    /**
     * The set of accepting states.
     */
    private final DeterministicFiniteAutomatonAcceptingStateSet 
            acceptingStateSet = 
              new DeterministicFiniteAutomatonAcceptingStateSet();
    
    public void setInitialState(
            DeterministicFiniteAutomatonState initialState) {
        this.initialState =
                Objects.requireNonNull(
                        initialState,
                        "The input initial state is null.");
    }
    
    /**
     * Exposes the accepting state set.
     * 
     * @return the accepting state set.
     */
    public DeterministicFiniteAutomatonAcceptingStateSet
         getAcceptingStateSet() {
        return acceptingStateSet;
    }
    
    /**
     * Validates an input string.
     * 
     * @param text the text to validate.
     * @return {@code true} only if the input string belongs to the regular 
     *         language recognized by this DFA.
     */
    public boolean matches(String text) {
        DeterministicFiniteAutomatonState state = deltaStar(text);
        
        if (state == null) {
            return false;
        }
        
        return acceptingStateSet.getAcceptingStateSet().contains(state);
    }
    
    /**
     * Implements the actual validation.
     * 
     * @param text the text to validate.
     * @return the resulting state reached after processing {@code text}. A
     *         value of {@code null} may be returned in case {@code text}
     *         contains characters not present in the regular expression used to
     *         construct this DFA.
     */
    private DeterministicFiniteAutomatonState deltaStar(String text) {
        int n = text.length();
        int textIndex = 0;
        
        DeterministicFiniteAutomatonState previousState = initialState;
        DeterministicFiniteAutomatonState currentState = null;
        
        while (n > 0) {
            currentState = previousState.traverse(text.charAt(textIndex));
            
            if (currentState == null) {
                break;
            }
            
            n--;
            textIndex++;
            previousState = currentState;
        }
        
        return currentState;
    }
}
