package com.github.coderodde.regex;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * This class implements a deterministic finite automaton.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 11, 2023)
 * @since 1.6 (Nov 11, 2023)
 */
public final class DeterministicFiniteAutomaton {
    
    /**
     * The initial state of the DFA.
     */
    private DeterministicFiniteAutomatonState initialState;
    
    /**
     * The set of accepting states.
     */
    private final DeterministicFiniteAutomatonAcceptingStateSet 
            acceptingStateSet = 
              new DeterministicFiniteAutomatonAcceptingStateSet();
    
    public void setInitialState(
            DeterministicFiniteAutomatonState initialState) {
        this.initialState =
                Objects.requireNonNull(
                        initialState,
                        "The input initial state is null.");
    }
    
    /**
     * Exposes the accepting state set.
     * 
     * @return the accepting state set.
     */
    public DeterministicFiniteAutomatonAcceptingStateSet
         getAcceptingStateSet() {
        return acceptingStateSet;
    }
    
    /**
     * Validates an input string.
     * 
     * @param text the text to validate.
     * @return {@code true} only if the input string belongs to the regular 
     *         language recognized by this DFA.
     */
    public boolean matches(String text) {
        DeterministicFiniteAutomatonState state = deltaStar(text);
        
        if (state == null) {
            return false;
        }
        
        return acceptingStateSet.getAcceptingStateSet().contains(state);
    }
    
    public int getNumberOfStates() {
        return getAllReachableStates().size();
    }
    
    public DeterministicFiniteAutomaton minimizeViaHopcroftAlgorithm() {
        
        int stateId = 0;
        
        Set<Set<DeterministicFiniteAutomatonState>> encodedStates =
                minimizeViaHopcroftAlgorithmImpl();
        
        DeterministicFiniteAutomaton dfa = new DeterministicFiniteAutomaton();
        
        Map<Set<DeterministicFiniteAutomatonState>, 
            DeterministicFiniteAutomatonState> stateMap = new HashMap<>();
        
        for (Set<DeterministicFiniteAutomatonState> encodedState : 
                encodedStates) {
            
            DeterministicFiniteAutomatonState dfaState = 
                    new DeterministicFiniteAutomatonState(stateId++);
            
            stateMap.put(encodedState, dfaState);
        }
        
        for (Set<DeterministicFiniteAutomatonState> encodedState :
                encodedStates) {
            
            DeterministicFiniteAutomatonState currentDFAState =
                    stateMap.get(encodedState);
            
            Set<DeterministicFiniteAutomatonState> nextDFAStateSet =
                    new HashSet<>();
            
            Character character =
                    encodedState
                            .iterator()
                            .next()
                            .followerMap
                            .keySet()
                            .iterator()
                            .next();
                
            DeterministicFiniteAutomatonState state = 
                    currentDFAState.traverse(character);

            nextDFAStateSet.add(state);
            
            DeterministicFiniteAutomatonState nextDFAState = 
                    stateMap.get(nextDFAStateSet);
            
            currentDFAState.addFollowerState(character, nextDFAState);
            
            if (this.getAcceptingStateSet()
                    .getAcceptingStateSet()
                    .contains(currentDFAState)) {
                
                dfa.getAcceptingStateSet()
                   .addDeterministicFiniteAutomatonState(currentDFAState);
            }
            
        }
        
        return dfa;
    }
    
    private Set<Set<DeterministicFiniteAutomatonState>>
         minimizeViaHopcroftAlgorithmImpl() {
             
        Set<Set<DeterministicFiniteAutomatonState>> p = new HashSet<>();
        Set<Set<DeterministicFiniteAutomatonState>> w = new HashSet<>();
        Set<DeterministicFiniteAutomatonState> reachableStates = 
                getAllReachableStates();
        
        p.add(acceptingStateSet.getAcceptingStateSet());
        
        p.add(Utils.difference(
                reachableStates, 
                acceptingStateSet.getAcceptingStateSet()));
        
        w.add(acceptingStateSet.getAcceptingStateSet());
        
        w.add(Utils.difference(
                reachableStates, 
                acceptingStateSet.getAcceptingStateSet()));
        
        while (!w.isEmpty()) {
            Set<DeterministicFiniteAutomatonState> a = w.iterator().next();
            
            w.remove(a);
            
            for (Character character : getLocalAlphabet(a)) {
                Set<DeterministicFiniteAutomatonState> x = 
                        getX(character, 
                             reachableStates, 
                             a);
                
                for (Set<DeterministicFiniteAutomatonState> y : p) {
                    Set<DeterministicFiniteAutomatonState> intersection = 
                            Utils.intersection(x, y);
                    
                    if (intersection.isEmpty()) {
                        continue;
                    }
                    
                    Set<DeterministicFiniteAutomatonState> difference = 
                            Utils.difference(x, y);
                    
                    if (difference.isEmpty()) {
                        continue;
                    }
                    
                    p.remove(y);
                    p.add(intersection);
                    p.add(difference);
                    
                    if (w.contains(y)) {
                        w.remove(y);
                        w.add(intersection);
                        w.add(difference);
                    } else {
                        if (intersection.size() <= difference.size()) {
                            w.add(intersection);
                        } else {
                            w.add(difference);
                        }
                    }
                }
            }
        }
        
        return p;
    }
    
    private Set<DeterministicFiniteAutomatonState> 
        getX(Character c, 
             Set<DeterministicFiniteAutomatonState> allStates,
             Set<DeterministicFiniteAutomatonState> a) {
            
        Set<DeterministicFiniteAutomatonState> x = new HashSet<>();
        
        for (DeterministicFiniteAutomatonState state : allStates) {
            if (a.contains(state.traverse(c))) {
                x.add(state);
            }
        }
        
        return x;
    }
    
    private Set<Character> 
        getLocalAlphabet(Set<DeterministicFiniteAutomatonState> stateSet) {
            
        Set<Character> localAlphabet = new HashSet<>();
        
        for (DeterministicFiniteAutomatonState state : stateSet) {
            localAlphabet.addAll(state.followerMap.keySet());
        }
        
        return localAlphabet;
    }
    
    public DeterministicFiniteAutomaton minimizeViaMooresAlgorithm() {
        
        return null;
    }
    
    /**
     * Implements the actual validation.
     * 
     * @param text the text to validate.
     * @return the resulting state reached after processing {@code text}. A
     *         value of {@code null} may be returned in case {@code text}
     *         contains characters not present in the regular expression used to
     *         construct this DFA.
     */
    private DeterministicFiniteAutomatonState deltaStar(String text) {
        int n = text.length();
        int textCharacterIndex = 0;
        DeterministicFiniteAutomatonState currentState = initialState;
        
        while (textCharacterIndex != n) {
            currentState = 
                    currentState.traverse(text.charAt(textCharacterIndex++));
            
            if (currentState == null) {
                break;
            }
        }
        
        return currentState;
    }
    
    private Set<DeterministicFiniteAutomatonState> getAllReachableStates() {
        Deque<DeterministicFiniteAutomatonState> queue = new ArrayDeque<>();
        Set<DeterministicFiniteAutomatonState> visited = new HashSet<>();
        
        queue.addLast(initialState);
        visited.add(initialState);
        
        while (!queue.isEmpty()) {
            DeterministicFiniteAutomatonState state = queue.removeFirst();
            
            for (DeterministicFiniteAutomatonState follower : 
                    state.followerMap.values()) {
                if (!visited.contains(follower)) {
                    visited.add(follower);
                    queue.addLast(follower);
                }
            }
        }
        
        return visited;
    }
}
