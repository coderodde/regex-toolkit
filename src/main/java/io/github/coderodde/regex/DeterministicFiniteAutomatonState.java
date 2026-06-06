package io.github.coderodde.regex;

import java.util.Objects;

/**
 * This class models the state in deterministic finite automata (DFA for short).
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 10, 2023)
 * @since 1.6 (Nov 10, 2023)
 */
public class DeterministicFiniteAutomatonState {
    
    /**
     * The ID used for distinguishing between the states.
     */
    private final int id;
    
    /**
     * If set, refers to the state to which we transit in case of reading a dot
     * operator.
     */
    private DeterministicFiniteAutomatonState dotTargetState;
    
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
        // TODO: Figure out.
//        transitionMap = null;
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
    
    void addFollowerState(CodePointRange codePointRange,
                          DeterministicFiniteAutomatonState goalState) {
        transitionMap.addTransition(codePointRange, goalState);
    }
    
    void addFollowerState(Integer codePoint, 
                          DeterministicFiniteAutomatonState goalState) {
        
        transitionMap.addTransition(new CodePointRange(codePoint), goalState);
    }
    
    void addDotTransitionState(DeterministicFiniteAutomatonState goalState) {
        dotTargetState =
            Objects.requireNonNull(
                goalState,
                "The input goal state is null.");
    }
    
    DeterministicFiniteAutomatonState getDotTransitionGoal() {
        return dotTargetState;
    }
    
    DeterministicFiniteAutomatonState traverse(int codePoint) {
        return transitionMap.getTargetState(codePoint);
    }
    
    DeterministicFiniteAutomatonState traverse(CodePointRange codePointRange) {
        return transitionMap.getTargetState(codePointRange);
    }
}
