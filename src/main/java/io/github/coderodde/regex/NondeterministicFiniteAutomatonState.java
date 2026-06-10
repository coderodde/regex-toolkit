package io.github.coderodde.regex;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class implements the state in a non-deterministic finite automaton.
 */
public final class NondeterministicFiniteAutomatonState {
    
    private static final int DEFAULT_ENTRY_ARRAY_CAPACITY = 8;
    private static final CodePointRange CODE_POINT_RANGE = new CodePointRange();
    
    private final int id;
    private int size = 0;
    
    private TransitionFunctionEntry[] entries = 
        new TransitionFunctionEntry[DEFAULT_ENTRY_ARRAY_CAPACITY]; 
    
    final Set<NondeterministicFiniteAutomatonState> epsilonSet = 
            new HashSet<>();
    
    private NondeterministicFiniteAutomatonState dotTransition;
    
    /**
     * Constructs a state for a nondeterministic finite automaton.
     * 
     * @param id the ID of the state.
     */
    NondeterministicFiniteAutomatonState(int id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        return "[NFA state " + id + "]";
    }
    
    @Override
    public boolean equals(Object o) {
        NondeterministicFiniteAutomatonState otherState = 
                (NondeterministicFiniteAutomatonState) o;
        
        return id == otherState.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
    
    void addDotTransition(NondeterministicFiniteAutomatonState state) {
        dotTransition = state;
    }
    
    NondeterministicFiniteAutomatonState getDotTransitionState() {
        return dotTransition;
    }
    
    void addTransition(CodePointRange codePointRange,
                       NondeterministicFiniteAutomatonState state) {
        Set<NondeterministicFiniteAutomatonState> set = new HashSet<>();
        set.add(state);
        
        addTransition(codePointRange, set);
    }
    
    void addTransition(int codePoint,
                       NondeterministicFiniteAutomatonState state) {
        addTransition(new CodePointRange(codePoint), state);
    }
    
    void addTransition(CodePointRange codePointRange,
                       Set<NondeterministicFiniteAutomatonState> state) {
        
        for (int i = 0; i < size; ++i) {
            if (entries[i].getCodePointRange().equals(codePointRange)) {
                entries[i].getGoalStates().addAll(state);
                return;
            }
        }
        
        growIfNeeded();
        
        TransitionFunctionEntry transitionFunctionEntry = 
            new TransitionFunctionEntry(codePointRange, state);
        
        entries[size] = transitionFunctionEntry;
        
        int index = size;
        
        while (index != 0 
               && entries[index - 1].getCodePointRange()
                                    .compareTo(codePointRange) == 1) {
            
            entries[index] = entries[index - 1];
            --index;
        }
        
        entries[index] = transitionFunctionEntry;
        ++size;
    }
    
    private void growIfNeeded() {
        if (size == entries.length) {
            TransitionFunctionEntry[] newEntries = 
                new TransitionFunctionEntry[(size * 3) / 2];
            
            System.arraycopy(this.entries, 0, newEntries, 0, size);
            
            this.entries = newEntries;
        }
    }
    
    void setDotTransition(NondeterministicFiniteAutomatonState nextState) {
        this.dotTransition = nextState;
    }
    
    void addEpsilonTransition(NondeterministicFiniteAutomatonState nextState) {
        epsilonSet.add(nextState);
    }
    
    Set<NondeterministicFiniteAutomatonState> getGoalStates(int codePoint) {
        CODE_POINT_RANGE.setMinimumCodePoint(codePoint);
        CODE_POINT_RANGE.setMaximumCodePoint(codePoint);
        
        return getGoalStates(CODE_POINT_RANGE);
    }
    
    Set<NondeterministicFiniteAutomatonState> 
        getGoalStates(CodePointRange codePointRange) {
            
        int l = 0;
        int r = size - 1;
        
        while (l <= r) {
            int m = l + (r - l) / 2;
            
            switch (entries[m].codePointRange.compareTo(codePointRange)) {
                case -1 -> l = m + 1;
                case  1 -> r = m - 1;
                
                default -> {
                    return entries[m].goalStates;
                }
            }
        }
        
        return null;
    }
        
    Set<NondeterministicFiniteAutomatonState> getEpsilonStates() {
        return epsilonSet;
    }
        
    NondeterministicFiniteAutomatonState getDotTransition() {
        return dotTransition;
    }
    
    int getTransitionCount() {
        return size;
    }
    
    TransitionFunctionEntry getTransition(int index) {
        return entries[index];
    }

    Iterable<Set<NondeterministicFiniteAutomatonState>> 
        followerStateIterable() {
        return new Iterable<Set<NondeterministicFiniteAutomatonState>>() {
            
            @Override
            public Iterator<Set<NondeterministicFiniteAutomatonState>> 
                iterator() {
                
                return new Iterator<
                        Set<NondeterministicFiniteAutomatonState>>() {
                            
                    private int index = 0;
                    
                    @Override
                    public boolean hasNext() {
                        return index < size;
                    }

                    @Override
                    public Set<NondeterministicFiniteAutomatonState> next() {
                        return entries[index++].goalStates;
                    }
                };
            }
        };
    }
    
    static final class TransitionFunctionEntry 
            implements Comparable<TransitionFunctionEntry> {

        private final CodePointRange codePointRange;
        private final Set<NondeterministicFiniteAutomatonState> goalStates;
        
        TransitionFunctionEntry(
            CodePointRange codePointRange,
            Set<NondeterministicFiniteAutomatonState> goalStates) {
            
            this.codePointRange = codePointRange;
            this.goalStates = goalStates;
        }
        
        CodePointRange getCodePointRange() {
            return codePointRange;
        }
        
        Set<NondeterministicFiniteAutomatonState> getGoalStates() {
            return goalStates;
        }
        
        @Override
        public int compareTo(TransitionFunctionEntry o) {
            return this.codePointRange.compareTo(o.codePointRange);
        }
    }
}
