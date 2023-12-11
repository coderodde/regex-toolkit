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
    
    final DeterministicFiniteAutomatonStateTransitionMap transitionMap = 
      new DeterministicFiniteAutomatonStateTransitionMap();
    
    /**
     * Constructs a new deterministic finite automaton state.
     * 
     * @param id the ID of the new state.
     */
    public DeterministicFiniteAutomatonState(int id) {
        this.id = id;
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
    
    void addFollowerState(Character character,
                          DeterministicFiniteAutomatonState nextState) {
        followerMap.put(character, nextState);
    }
    
    void addDotTransition(DeterministicFiniteAutomatonState state) {
        dotTransition = state;
    }
    
    DeterministicFiniteAutomatonState traverse(Character character) {
        return followerMap.get(character);
    }
    
    DeterministicFiniteAutomatonState getDotTransition() {
        return dotTransition;
    }
}
