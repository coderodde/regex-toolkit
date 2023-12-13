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
    
    /**
     * The number of character range mappings in this transition map.
     */
    private int size = 0;
    
    /**
     * The actual array of entries.
     */
    private TransitionMapEntry[] entries = new TransitionMapEntry[DEFAULT_ENTRY_ARRAY_CAPACITY];
    
    void addTransition(CharacterRange characterRange, 
                       DeterministicFiniteAutomatonState followerState,
                       boolean isPeriodWildcardEntry) {
        growIfNeeded();
        
        entries[size++] = new TransitionMapEntry(characterRange, 
                                                 followerState,
                                                 isPeriodWildcardEntry);
        Arrays.sort(entries, 0, size);
    }
    
    void addTransition(Character character,
                       DeterministicFiniteAutomatonState followerState,
                       boolean isPeriodWildcardEntry) {
        
        CharacterRange characterRange = new CharacterRange(character);
        addTransition(characterRange, followerState, isPeriodWildcardEntry);
    }
    
    int size() {
        return size;
    }
    
    TransitionMapEntry get(int index) {
        return entries[index];
    }
    
    DeterministicFiniteAutomatonState
        getFollowerState(char character) {
        int l = 0;
        int r = size - 1;
        
        while (l <= r) {
            int m = l + (r - l) / 2;
            
            if (entries[m].characterRange.characterIsWithinRange(character)) {
                return entries[m].followerState;
            }
            
            if (entries[m].characterRange
                          .characterRangeSmallerThan(character)) {
                l = m + 1;
            } else {
                r = m - 1;
            }
        }
        
        return null;
    }
    
    DeterministicFiniteAutomatonState
         getFollowerState(CharacterRange characterRange) {
         int l = 0;
         int r = size - 1;
         
         while (l <= r) {
             int m = l + (r - l) / 2;
             
             switch (entries[m].characterRange.compareTo(characterRange)) {
                 case -1 -> l = m + 1;
                 case  1 -> r = m - 1;
                 
                 default -> {
                     return entries[m].followerState;
                 }
             }
         }
         
         throw new IllegalStateException("Should not get here.");
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
    
    static final class TransitionMapEntry implements Comparable<TransitionMapEntry> {
        private final CharacterRange characterRange;
        private DeterministicFiniteAutomatonState followerState;
        private final boolean isPeriodWildcardEntry;
        
        TransitionMapEntry(CharacterRange characterRange, 
                           DeterministicFiniteAutomatonState followerState,
                           boolean isPeriodEntry) {
            this.characterRange = characterRange;
            this.followerState = followerState;
            this.isPeriodWildcardEntry = isPeriodEntry;
        }
        
        CharacterRange getCharacterRange() {
            return characterRange;
        }
        
        DeterministicFiniteAutomatonState getFollowerState() {
            return followerState;
        }
        
        boolean isPeriodWildcardEntry() {
            return isPeriodWildcardEntry;
        }

        void setFollowerState(DeterministicFiniteAutomatonState followerState) {
            this.followerState = followerState;
        }
        
        @Override
        public String toString() {
            return "[TransitionMapEntry: range = '" 
                    + characterRange.getMinimumCharacter() 
                    + "' - '" 
                    + characterRange.getMaximumCharacter() 
                    + "', period = " 
                    + isPeriodWildcardEntry 
                    + ", state ID = " 
                    + followerState.getId() 
                    + "}";
        }
        
        @Override
        public int compareTo(TransitionMapEntry o) {
            return this.characterRange.compareTo(o.characterRange);
        }
    }
}
