package com.github.coderodde.regex;

import java.util.Arrays;

/**
 * This class implements a transition map mapping character ranges to the 
 * following DFA states.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 10, 2023)
 * @since 1.6 (Dec 10, 2023)
 */
final class DeterministicFiniteAutomatonStateTransitionMap {
    
    private static final int DEFAULT_ENTRY_ARRAY_CAPACITY = 8;
    
    private static final CodePointRange CODE_POINT_RANGE = new CodePointRange();
    
    /**
     * The number of character range mappings in this transition map.
     */
    private int size = 0;
    
    /**
     * The actual array of entries.
     */
    private TransitionMapEntry[] entries = new TransitionMapEntry[DEFAULT_ENTRY_ARRAY_CAPACITY];
    
    private DeterministicFiniteAutomatonState dotTransitionState;
    
    void addDotTransitionState(DeterministicFiniteAutomatonState state) {
        this.dotTransitionState = state;
    }
    
    DeterministicFiniteAutomatonState getDotTransitionState() {
        return this.dotTransitionState;
    }
    
    void addTransition(CodePointRange characterRange, 
                       DeterministicFiniteAutomatonState sourceState,
                       DeterministicFiniteAutomatonState targetState) {
        growIfNeeded();
        TransitionMapEntry targetTransitionMapEntry =  
                new TransitionMapEntry(characterRange, 
                                       sourceState,
                                       targetState);
    
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
    
    void addTransition(int codePoint,
                       DeterministicFiniteAutomatonState sourceState,
                       DeterministicFiniteAutomatonState targetState,
                       boolean isPeriodWildcardEntry) {
        
        CodePointRange characterRange = new CodePointRange(codePoint);
        
        addTransition(characterRange,
                      sourceState,
                      targetState);
    }
    
    int size() {
        return size;
    }
    
    TransitionMapEntry get(int index) {
        return entries[index];
    }
    
    DeterministicFiniteAutomatonState getTargetState(int codePoint) {
        int l = 0;
        int r = size - 1;
        
        while (l <= r) {
            int m = l + (r - l) / 2;
            
            if (entries[m].codePointRange.codePointIsWithinRange(codePoint)) {
                return entries[m].sourceState;
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
        
    TransitionMapEntry getTransitionMapEntry(Character character) {
        
        CODE_POINT_RANGE.setMinimumCodePoint(character);
        CODE_POINT_RANGE.setMaximumCodePoint(character);
        
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
     * @param characterRange the character range on which to traverse the map.
     * 
     * @return the target state.
     */
    DeterministicFiniteAutomatonState 
        getTargetState(CodePointRange characterRange) {
        int l = 0;
        int r = size - 1;
         
        while (l <= r) {
            int m = l + (r - l) / 2;
             
            switch (entries[m].codePointRange.compareTo(characterRange)) {
                case -1 -> l = m + 1;
                case  1 -> r = m - 1;
                 
                default -> {
                    return entries[m].sourceState;
                }
            }
        }
       
        return null;
    }
    
    private void growIfNeeded() {
        if (size == entries.length) {
            TransitionMapEntry[] newEntries = new TransitionMapEntry[(size * 3) / 2];
            
            System.arraycopy(this.entries,
                             0,
                             newEntries,
                             0, 
                             size);
            
            this.entries = newEntries;
        }
    }
    
    static final class TransitionMapEntry 
            implements Comparable<TransitionMapEntry> {
        
        private final CodePointRange codePointRange;
        private DeterministicFiniteAutomatonState sourceState;
        private DeterministicFiniteAutomatonState targetState;
        
        TransitionMapEntry(CodePointRange characterRange, 
                           DeterministicFiniteAutomatonState sourceState,
                           DeterministicFiniteAutomatonState targetState) {
            this.codePointRange = characterRange;
            this.sourceState = sourceState;
            this.targetState = targetState;
        }
        
        CodePointRange getCharacterRange() {
            return codePointRange;
        }
        
        DeterministicFiniteAutomatonState getFollowerState() {
            return sourceState;
        }

        void setFollowerState(DeterministicFiniteAutomatonState followerState) {
            this.sourceState = followerState;
        }
        
        @Override
        public String toString() {
            return "[TransitionMapEntry: range = '" 
                    + codePointRange.getMinimumCodePoint() 
                    + "' - '" 
                    + codePointRange.getMaximumCodePoint() 
                    + ", state ID = " 
                    + sourceState.getStateId() 
                    + "}";
        }
        
        @Override
        public int compareTo(TransitionMapEntry o) {
            return this.codePointRange.compareTo(o.codePointRange);
        }
    }
}
