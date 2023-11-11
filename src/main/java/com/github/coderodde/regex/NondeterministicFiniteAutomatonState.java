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
    
    /**
     * Constructs a state in a NFA.
     * 
     * @param name the name of the state.
     */
    public NondeterministicFiniteAutomatonState(String name) {
        super(name);
    }
}
