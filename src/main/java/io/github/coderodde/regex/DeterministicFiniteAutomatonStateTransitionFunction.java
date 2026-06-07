package io.github.coderodde.regex;

import io.github.coderodde.regex.DeterministicFiniteAutomatonStateTransitionFunction.TransitionFunctionEntry;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class implements a transition map mapping character ranges to the 
 * following DFA states.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 10, 2023)
 * @since 1.6 (Dec 10, 2023)
 */
final class DeterministicFiniteAutomatonStateTransitionFunction 
implements Iterable<TransitionFunctionEntry> {
    
    /**
     * The default entry array capacity.
     */
    private static final int DEFAULT_ENTRY_ARRAY_CAPACITY = 8;
    
    /**
     * Used as a sentinel in {@link #getTransitionMapEntry(int)}.
     */
    private static final CodePointRange CODE_POINT_RANGE = new CodePointRange();
    
    /**
     * The number of character range mappings in this transition map.
     */
    private int size = 0;
    
    /**
     * The set of all states involved in this transition function.
     */
    private final Set<DeterministicFiniteAutomatonState> stateSet = 
            new HashSet<>();
    
    /**
     * The actual array of entries.
     */
    private TransitionFunctionEntry[] entries = 
        new TransitionFunctionEntry[DEFAULT_ENTRY_ARRAY_CAPACITY];
    
    /**
     * Possible dot transition state.
     */
    private DeterministicFiniteAutomatonState dotTransitionState;

    /**
     * Constructs an empty transition function.
     */
    public DeterministicFiniteAutomatonStateTransitionFunction() {
        entries = new TransitionFunctionEntry[DEFAULT_ENTRY_ARRAY_CAPACITY];
    }
    
    /**
     * Copy-constructs a transition function.
     * 
     * @param other the transition function to copy.
     */
    public DeterministicFiniteAutomatonStateTransitionFunction(
           DeterministicFiniteAutomatonStateTransitionFunction other) {
        
        this.entries = new TransitionFunctionEntry[other.entries.length];
        this.size = other.size  ;
        
        for (int i = 0; i < size; ++i) {
            entries[i] = other.entries[i];
        }
        
        this.stateSet.addAll(other.stateSet);
    }
    
    /**
     * Prunes away all nodes in {@code unreachableStateSet} from this 
     * @param unreachableStateSet 
     */
    void pruneUnreachableStates(
            Set<DeterministicFiniteAutomatonState> unreachableStateSet) {
        
        Iterator<DeterministicFiniteAutomatonState> iterator =
                stateSet.iterator();
    
        while (iterator.hasNext()) {
            DeterministicFiniteAutomatonState state = iterator.next();
            
            state.clear();
            
            if (unreachableStateSet.contains(state)) {
                iterator.remove();
            }
        }
    }
    
    void addDotTransitionState(DeterministicFiniteAutomatonState state) {
        this.dotTransitionState = state;
    }
    
    DeterministicFiniteAutomatonState getDotTransitionState() {
        return this.dotTransitionState;
    }
    
    void addTransition(CodePointRange characterRange, 
                       DeterministicFiniteAutomatonState goalState) {
        
        growIfNeeded();
        
        TransitionFunctionEntry targetTransitionMapEntry =  
            new TransitionFunctionEntry(characterRange, goalState);
    
        entries[size] = targetTransitionMapEntry;
        
        int index = size;
        
        while (index != 0 
                && entries[index - 1].getCharacterRange()
                                     .compareTo(characterRange) == 1) {
            entries[index] = entries[index - 1];
            index--;
        }
        
        entries[index] = targetTransitionMapEntry;
        size++;
    }
    
//    void addTransition(int codePoint,
//                       DeterministicFiniteAutomatonState sourceState,
//                       DeterministicFiniteAutomatonState targetState) {
//        
//        CodePointRange characterRange = new CodePointRange(codePoint);
//        
//        addTransition(characterRange, targetState);
//    }
    
    int size() {
        return size;
    }
    
    void clear() {
        dotTransitionState = null;
        
        for (int i = 0; i != size; ++i) {
            entries[i] = null;
        }
        
        entries = null;
    }
    
    Set<CodePointRange> getAlphabet() {
        Set<CodePointRange> alphabet = new HashSet<>();
        
        for (TransitionFunctionEntry e : entries) {
            if (e == null) {
               break; 
            }
            
            alphabet.add(e.codePointRange);
        }
        
        return alphabet;
    }
    
    TransitionFunctionEntry get(int index) {
        return entries[index];
    }
    
    DeterministicFiniteAutomatonState getTargetState(int codePoint) {
        int l = 0;
        int r = size - 1;
        
        while (l <= r) {
            int m = l + (r - l) / 2;
            
            if (entries[m].codePointRange.codePointIsWithinRange(codePoint)) {
                return entries[m].goalState;
            }
            
            if (entries[m].codePointRange
                          .codePointRangeSmallerThan(codePoint)) {
                l = m + 1;
            } else {
                r = m - 1;
            }
        }
        
        return null;
    }
        
    TransitionFunctionEntry getTransitionMapEntry(int codePoint) {
        
        CODE_POINT_RANGE.setMinimumCodePoint(codePoint);
        CODE_POINT_RANGE.setMaximumCodePoint(codePoint);
        
        int l = 0;
        int r = size - 1;
        
        while (l <= r) {
            int m = l + (r - l) / 2;
            
            switch (entries[m].codePointRange.compareTo(CODE_POINT_RANGE)) {
                case -1 -> l = m + 1;
                case  1 -> r = m - 1;
                default -> { return entries[m]; }
            }
        }
        
        return null;
    }
    
    /**
     * Gets the target state for the input character range.
     * 
     * @param codePointRange the character range on which to traverse the map.
     * 
     * @return the target state.
     */
    DeterministicFiniteAutomatonState 
        getTargetState(CodePointRange codePointRange) {
        int l = 0;
        int r = size - 1;
         
        while (l <= r) {
            int m = l + (r - l) / 2;
             
            switch (entries[m].codePointRange.compareTo(codePointRange)) {
                case -1 -> l = m + 1;
                case  1 -> r = m - 1;
                 
                default -> {
                    return entries[m].goalState;
                }
            }
        }
       
        return null;
    }
    
    private void growIfNeeded() {
        if (size == entries.length) {
            TransitionFunctionEntry[] newEntries = 
                    new TransitionFunctionEntry[(size * 3) / 2];
            
            System.arraycopy(this.entries,
                             0,
                             newEntries,
                             0, 
                             size);
            
            this.entries = newEntries;
        }
    }
    
    @Override
    public Iterator<TransitionFunctionEntry> iterator() {
        
        return new Iterator<>() {
            private int index;
            
            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public TransitionFunctionEntry next() {
                return entries[index++];    
            }
        };
    }
    
    static final class TransitionFunctionEntry 
            implements Comparable<TransitionFunctionEntry> {
        
        private final CodePointRange codePointRange;
        private DeterministicFiniteAutomatonState goalState;
        
        TransitionFunctionEntry(CodePointRange characterRange, 
                           DeterministicFiniteAutomatonState goalState) {
            
            this.codePointRange = characterRange;
            this.goalState = goalState;
        }
        
        CodePointRange getCharacterRange() {
            return codePointRange;
        }
        
        DeterministicFiniteAutomatonState getGoalState() {
            return goalState;
        }

        void setGoalState(DeterministicFiniteAutomatonState goalState) {
            this.goalState = goalState;
        }
        
        @Override
        public String toString() {
            return "[TransitionMapEntry: range = '" 
                    + codePointRange.getMinimumCodePoint() 
                    + "' - '" 
                    + codePointRange.getMaximumCodePoint() 
                    + ", goal state ID = " 
                    + goalState.getStateId() 
                    + "}";
        }
        
        @Override
        public int compareTo(TransitionFunctionEntry o) {
            return this.codePointRange.compareTo(o.codePointRange);
        }
    }
}
