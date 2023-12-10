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
    private Entry[] entries = new Entry[DEFAULT_ENTRY_ARRAY_CAPACITY];
    
    void addTransition(CharacterRange characterRange, 
                       DeterministicFiniteAutomatonState followerState) {
        growIfNeeded();
        entries[size++] = new Entry(characterRange, followerState);
        Arrays.sort(entries, 0, size);
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
            Entry[] newEntries = 
                    new Entry[(DEFAULT_ENTRY_ARRAY_CAPACITY * 3) / 2];
            
            System.arraycopy(this.entries,
                             0,
                             newEntries,
                             0, 
                             size);
            
            this.entries = newEntries;
        }
    }
    
    private static final class Entry implements Comparable<Entry> {
        CharacterRange characterRange;
        DeterministicFiniteAutomatonState followerState;
        
        Entry(CharacterRange characterRange, 
              DeterministicFiniteAutomatonState followerState) {
            this.characterRange = characterRange;
            this.followerState = followerState;
        }

        @Override
        public int compareTo(Entry o) {
            return this.characterRange.compareTo(o.characterRange);
        }
    }
}
