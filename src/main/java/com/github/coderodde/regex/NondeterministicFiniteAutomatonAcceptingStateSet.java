package com.github.coderodde.regex;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class implements sets for storing the accepting states of a DFA.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 11, 2023)
 * @since 1.6 (Nov 11, 2023)
 */
public class NondeterministicFiniteAutomatonAcceptingStateSet {
    
    private final Set<NondeterministicFiniteAutomatonState> acceptingStateSet = 
            new HashSet<>();
    
    public void addNondeterministicFiniteAutomatonState(
            NondeterministicFiniteAutomatonState state) {
        acceptingStateSet.add(state);
    }
    
    public Set<NondeterministicFiniteAutomatonState> getStates() {
        return Collections.<NondeterministicFiniteAutomatonState>
                unmodifiableSet(acceptingStateSet);
    }
    
    public void clear() {
        acceptingStateSet.clear();
    }
}
