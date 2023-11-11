package com.github.coderodde.regex;

import java.util.Objects;

/**
 * This class models the state in deterministic finite automata (DFA for short).
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 10, 2023)
 * @since 1.6 (Nov 10, 2023)
 */
public final class DeterministicFiniteAutomatonState {
    
    private final String name;
    private final int hashCode;
    
    public DeterministicFiniteAutomatonState(String name) {
        this.name = Objects.requireNonNull(name);
        this.hashCode = name.hashCode();
    }
    
    public String getStateName() {
        return name;
    }
    
    @Override
    public String toString() {
        return "[State \"" + name + "\"]";
    }
    
    @Override
    public int hashCode() {
        return hashCode;
    }
    
    @Override
    public boolean equals(Object o) {
        DeterministicFiniteAutomatonState otherState = 
                (DeterministicFiniteAutomatonState) o;
        
        if (Objects.requireNonNull(otherState).hashCode != this.hashCode) {
            return false;
        }
        
        return getStateName().equals(otherState.getStateName());
    }
}
