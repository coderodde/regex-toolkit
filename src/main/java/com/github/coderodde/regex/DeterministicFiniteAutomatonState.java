package com.github.coderodde.regex;

/**
 * This class models the state in deterministic finite automata (DFA for short).
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 10, 2023)
 * @since 1.6 (Nov 10, 2023)
 */
public class DeterministicFiniteAutomatonState {
    
    private final int id;
    
    private DeterministicFiniteAutomatonStateTransitionFunction transitionMap = 
              new DeterministicFiniteAutomatonStateTransitionFunction();
    
    
    /**
     * Constructs a new deterministic finite automaton state.
     * 
     * @param id the ID of the new state.
     */
    public DeterministicFiniteAutomatonState(int id) {
        this.id = id;
    }
    
    int getStateId() {
        return id;
    }
    
    void clear() {
        transitionMap.clear();
        transitionMap = null;
    }
    
    DeterministicFiniteAutomatonStateTransitionFunction getTransitionMap() {
        return transitionMap;
    }
    
    @Override
    public String toString() {
        return "[DFA state " + id + "]";
    }
    
    @Override
    public int hashCode() {
        return id;
    }
    
    @Override
    public boolean equals(Object o) {
        DeterministicFiniteAutomatonState otherState = 
                (DeterministicFiniteAutomatonState) o;
        
        return id == otherState.id;
    }
    
    void addFollowerState(int codePoint,
                          DeterministicFiniteAutomatonState followerState) {
        
        transitionMap.addTransition(new CodePointRange(codePoint), 
                                    this,
                                    followerState);
    }
    
    void addFollowerState(CodePointRange characterRange, 
                          DeterministicFiniteAutomatonState followerState) {
        
        transitionMap.addTransition(characterRange, 
                                    this,
                                    followerState);
    }
    
    DeterministicFiniteAutomatonState traverse(int codePoint) {
        return transitionMap.getTargetState(codePoint);
    }
}
