package com.github.coderodde.regex;

/**
 * This class implements the state in a non-deterministic finite automaton.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 11, 2023)
 * @since 1.6 (Nov 11, 2023)
 */
public final class NondeterministicFiniteAutomatonState 
        extends DeterministicFiniteAutomatonState {
    
    final NondeterministicFiniteAutomatonTransitionFunction transitionFunction;
    
    /**
     * Constructs a state for a nondeterministic finite automaton.
     * 
     * @param id the ID of the state.
     */
    NondeterministicFiniteAutomatonState(
            int id, 
            NondeterministicFiniteAutomatonTransitionFunction 
                    transitionFunction) {
        super(id);
        this.transitionFunction = transitionFunction;
    }
}
