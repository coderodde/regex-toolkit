package com.github.coderodde.regex;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 17, 2023)
 * @since 1.6 (Nov 17, 2023)
 */
public final class NondeterministicFiniteAutomaton
        implements RegularExpressionMatcher {
    
    private NondeterministicFiniteAutomatonState initialState;
    private NondeterministicFiniteAutomatonState acceptingState;
    
    public NondeterministicFiniteAutomatonState getInitialState() {
        return initialState;
    }
    
    public NondeterministicFiniteAutomatonState getAcceptingState() {
        return acceptingState;
    }
    
    public void setInitialState(
            NondeterministicFiniteAutomatonState initialState) {
        this.initialState = 
                Objects.requireNonNull(
                        initialState,
                        "The input initial state is null.");
    }
    
    public void setAcceptingState(
            NondeterministicFiniteAutomatonState acceptingState) {
        this.acceptingState = acceptingState;
    }
    
    public int getNumberOfStates() {
        return getAllReachableStates().size();
    }
    
    private Set<NondeterministicFiniteAutomatonState> getAllReachableStates() {
        Deque<NondeterministicFiniteAutomatonState> queue = new ArrayDeque<>();
        Set<NondeterministicFiniteAutomatonState> visited = new HashSet<>();
        
        queue.addLast(initialState);
        visited.add(initialState);
        
        while (!queue.isEmpty()) {
            NondeterministicFiniteAutomatonState state = queue.removeFirst();
            
            for (Set<NondeterministicFiniteAutomatonState> followerSet 
                    : state.map.values()) {
                for (NondeterministicFiniteAutomatonState follower 
                        : followerSet) {
                    if (!visited.contains(follower)) {
                        visited.add(follower);
                        queue.addLast(follower);
                    }
                }
            }
            
            for (NondeterministicFiniteAutomatonState epsilonFollower
                    : state.epsilonSet) {
                if (!visited.contains(epsilonFollower)) {
                    visited.add(epsilonFollower);
                    queue.addLast(epsilonFollower);
                }
            }
            
            NondeterministicFiniteAutomatonState dotState = 
                    state.getDotTransitionState();
            
            if (dotState != null && !visited.contains(dotState)) {
                visited.add(dotState);
                queue.addLast(dotState);
            }
        }
        
        return visited;
    }
        
    public boolean matches(String text) {
        Set<NondeterministicFiniteAutomatonState> finalStateSet = 
                simulateNFA(text);
        
        if (finalStateSet == null) {
            return false;
        }
    
        return isAcceptingStateSet(finalStateSet);
    }
    
    public DeterministicFiniteAutomaton 
        convertToDetermenisticFiniteAutomaton() {
        return new NFAToDFAConverter(this).convert();
    }
        
    public static NondeterministicFiniteAutomaton compile(String regex) {
        List<RegexToken> infixTokens = new RegexTokenizer().tokenize(regex);
        Deque<RegexToken> postfixTokens = 
                new RegexInfixToPostfixConverter()
                        .convert(infixTokens);
        
        return new NondeterministicFiniteAutomatonCompiler()
                .compile(postfixTokens);
    }
    
    private static Set<Character> 
        getLocalAlphabet(Set<NondeterministicFiniteAutomatonState> states) {
        Set<Character> localAlphabet = new HashSet<>();
        
        for (NondeterministicFiniteAutomatonState state : states) {
            localAlphabet.addAll(state.map.keySet());
        }
        
        return localAlphabet;
    }
    
    private Set<NondeterministicFiniteAutomatonState> simulateNFA(String text) {
        Set<NondeterministicFiniteAutomatonState> startSet = 
                new HashSet<>(Arrays.asList(initialState));
        
        Set<NondeterministicFiniteAutomatonState> currentStates =
                epsilonExpand(startSet);
        
        for (int i = 0; i != text.length(); i++) {
            char ch = text.charAt(i);
            
            Set<NondeterministicFiniteAutomatonState> nextStates = 
                    new HashSet<>();
        
            for (NondeterministicFiniteAutomatonState q : currentStates) {
                Set<NondeterministicFiniteAutomatonState> nextState =
                        q.getFollowingStates(ch);
                
                if (q.getDotTransitionState() != null) {
                    nextStates.add(q.getDotTransitionState());
                }
                
                if (nextState != null) {
                    nextStates.addAll(nextState);
                }
            }
                
            currentStates = epsilonExpand(nextStates);
        }
        
        return currentStates;
    }
    
    static Set<NondeterministicFiniteAutomatonState> 
        epsilonExpand(Set<NondeterministicFiniteAutomatonState> set) {
            
        Set<NondeterministicFiniteAutomatonState> expandedSet = 
                new HashSet<>(set);
        
        Deque<NondeterministicFiniteAutomatonState> queue = new ArrayDeque<>();
        Set<NondeterministicFiniteAutomatonState> visited = new HashSet<>();
        
        for (NondeterministicFiniteAutomatonState state : expandedSet) {
            if (!visited.contains(state)) {
                visited.add(state);
                queue.addLast(state);
            }
        }
        
        while (!queue.isEmpty()) {
            NondeterministicFiniteAutomatonState state = queue.removeFirst();
            Set<NondeterministicFiniteAutomatonState> epsilonFollowerStates = 
                    state.getEpsilonStates();
            
            for (NondeterministicFiniteAutomatonState s :
                    epsilonFollowerStates) {
                if (!visited.contains(s)) {
                    visited.add(s);
                    queue.addLast(s);
                    expandedSet.add(s);
                }
            }
        }
        
        return expandedSet;
    }
    
    private boolean isAcceptingStateSet(
            Set<NondeterministicFiniteAutomatonState> finalStateSet) {
        return finalStateSet.contains(acceptingState);
    }
    
    private final class NFAToDFAConverter {
        
        private final Deque<Set<NondeterministicFiniteAutomatonState>> 
                stateQueue = new ArrayDeque<>();
        
        private final Map<Set<NondeterministicFiniteAutomatonState>,
                          DeterministicFiniteAutomatonState> stateMap = 
                                  new HashMap<>();
        
        private final NondeterministicFiniteAutomaton nfa;
        private int stateID = 0;
        private final DeterministicFiniteAutomaton dfa = 
                  new DeterministicFiniteAutomaton();

        public NFAToDFAConverter(NondeterministicFiniteAutomaton nfa) {
            this.nfa = nfa;
        }
        
        DeterministicFiniteAutomaton convert() {
            init();
            
            while (!stateQueue.isEmpty()) {
                Set<NondeterministicFiniteAutomatonState> currentNFAState = 
                        stateQueue.removeFirst();
                
                DeterministicFiniteAutomatonState currentDFAState = 
                        stateMap.get(currentNFAState);
                
                currentNFAState = epsilonExpand(currentNFAState);
                
                Set<NondeterministicFiniteAutomatonState> dotSet = 
                        computePeriodWildcardSet(currentNFAState);
                
                if (dotSet.isEmpty()) {
                    // Once here, we must populate the transition map only with
                    // arcs with actual labels:
//                    Set<NondeterministicFiniteAutomatonState> nextNFAState = 
                } else {
                    // Once here, we must populate the transition map with all 
                    // the character ranges covering 65536 characters:
                }
                
                if (!dotSet.isEmpty()) {
                    // Once here, we must merge all the outgoing characters with
                    // a single dot operator:
                    Set<NondeterministicFiniteAutomatonState> nextNFAState = 
                            computeCharacterTransitions(currentNFAState);
                    
                    nextNFAState.addAll(dotSet);
                    nextNFAState = epsilonExpand(nextNFAState);
                    
                    DeterministicFiniteAutomatonState nextDFAState = 
                            stateMap.get(nextNFAState);
                    
                    if (nextDFAState == null) {
                        nextDFAState = 
                                new DeterministicFiniteAutomatonState(
                                        getStateID());
                        
                        stateMap.put(nextNFAState, nextDFAState);
                        stateQueue.addLast(nextNFAState);
                    }
                    
                    if (nextNFAState.contains(nfa.getAcceptingState())) {
                        dfa.getAcceptingStates().add(nextDFAState);
                    }
                    // TODO: rework this!
//                    currentDFAState.addDotTransition(nextDFAState);
                } else {
                    Set<Character> localAlphabet = 
                            getLocalAlphabet(currentNFAState);
                    
                    for (Character character : localAlphabet) {
                        Set<NondeterministicFiniteAutomatonState> nextNFAState = 
                                new HashSet<>();
                        
                        for (NondeterministicFiniteAutomatonState state 
                                : currentNFAState) {
                            Set<NondeterministicFiniteAutomatonState>
                                    followingStates = 
                                    state.getFollowingStates(character);
                            
                            if (followingStates != null) {
                                // TODO: can followingStates be null?
                                nextNFAState.addAll(
                                        state.getFollowingStates(character));
                            }
                        }
                        
                        nextNFAState = epsilonExpand(nextNFAState);

                        DeterministicFiniteAutomatonState nextDFAState = 
                                stateMap.get(nextNFAState);

                        if (nextDFAState == null) {
                            nextDFAState = 
                                    new DeterministicFiniteAutomatonState(
                                            getStateID());

                            stateMap.put(nextNFAState, nextDFAState);
                            stateQueue.addLast(nextNFAState);
                        }

                        if (nextNFAState.contains(nfa.getAcceptingState())) {
                            dfa.getAcceptingStates().add(nextDFAState);
                        }

                        currentDFAState.addFollowerState(character, 
                                                         nextDFAState);
                    }
                }
            }
            
            return dfa;
        }
        
        private Set<NondeterministicFiniteAutomatonState> 
        computePeriodWildcardSet(
            Set<NondeterministicFiniteAutomatonState> currentNFAState) {
            Set<NondeterministicFiniteAutomatonState> periodWildcardStates = 
                    new HashSet<>();

            // Try to find dot transitions:
            for (NondeterministicFiniteAutomatonState state :
                    currentNFAState) {
                NondeterministicFiniteAutomatonState periodWildcardState = 
                        state.getDotTransitionState();

                if (periodWildcardState != null) {
                    periodWildcardStates.add(periodWildcardState);
                }
            }
            
            return periodWildcardStates;
        }
        
        private Set<NondeterministicFiniteAutomatonState>
        computeCharacterTransitions(
                Set<NondeterministicFiniteAutomatonState> currentNFAState) {
            
            Set<NondeterministicFiniteAutomatonState> outputStateSet = 
                    new HashSet<>();
            
            Set<Character> localAlphabet = getLocalAlphabet(currentNFAState);
            
            for (Character character : localAlphabet) {
                Set<NondeterministicFiniteAutomatonState> nextNFAState = 
                        new HashSet<>();
                
                for (NondeterministicFiniteAutomatonState state
                        : currentNFAState) {
                    Set<NondeterministicFiniteAutomatonState> followingStates = 
                            state.getFollowingStates(character);
                    
                    if (followingStates != null) {
                        nextNFAState.addAll(
                                state.getFollowingStates(character));
                    }
                }
                
                outputStateSet.addAll(nextNFAState);
            }
            
            return outputStateSet;
        }
        
        private void init() {
            Set<NondeterministicFiniteAutomatonState> startState = 
                    new HashSet<>(Arrays.asList(nfa.getInitialState()));
            
            startState = epsilonExpand(startState);
            
            DeterministicFiniteAutomatonState dfaInitialState = 
                    new DeterministicFiniteAutomatonState(getStateID());
            
            stateMap.put(startState, dfaInitialState);
            stateQueue.addLast(startState);
            dfa.setInitialState(dfaInitialState);
            
            if (startState.contains(nfa.getAcceptingState())) {
                dfa.getAcceptingStates().add(dfaInitialState);
            }
            
            Set<NondeterministicFiniteAutomatonState> emptyNFAState = 
                    new HashSet<>();
            
            DeterministicFiniteAutomatonState emptyDFAState = 
                    new DeterministicFiniteAutomatonState(getNumberOfStates());
            
            stateMap.put(emptyNFAState, emptyDFAState);
//            emptyDFAState.addDotTransition(emptyDFAState);
        }
        
        private int getStateID() {
            return stateID++;
        }
    }
    
    static DeterministicFiniteAutomatonStateTransitionMap
        computeTransitionMapWithoutPeriodWildcard(Set<Character> alphabet,
                                                  int startId) {
        
        DeterministicFiniteAutomatonStateTransitionMap transitionMap = 
                new DeterministicFiniteAutomatonStateTransitionMap();
        
        for (Character character : alphabet) {
            transitionMap.addTransition(
                    character, 
                    new DeterministicFiniteAutomatonState(startId++),
                    false);
        }
        
        return transitionMap;
    }
        
    static DeterministicFiniteAutomatonStateTransitionMap 
        computeTransitionMapWithPeriodWildcard(TreeSet<Character> alphabet) {
        
        DeterministicFiniteAutomatonStateTransitionMap transitionMap = 
                new DeterministicFiniteAutomatonStateTransitionMap();
            
        Iterator<Character> alphabetIterator = alphabet.iterator();
        
        Character leftCharacter  = alphabetIterator.next();
        Character rightCharacter = null;
        
        if (leftCharacter > Character.MIN_VALUE) {
            CharacterRange firstCharacterRange = 
                    new CharacterRange(Character.MIN_VALUE, leftCharacter);
            
            transitionMap.addTransition(firstCharacterRange, null, true);
        }
        
        while (alphabetIterator.hasNext()) {
            rightCharacter = alphabetIterator.next();
            
            CharacterRange characterRange1 =
                    new CharacterRange(leftCharacter);

            CharacterRange characterRange2 = 
                    new CharacterRange(rightCharacter);
            
            if (leftCharacter + 1 < rightCharacter) {
                CharacterRange middleCharacterRange = 
                        new CharacterRange(
                                (char)(leftCharacter + 1), 
                                (char)(rightCharacter - 1));
            } 
            
            transitionMap.addTransition(characterRange1, null, true);
            transitionMap.addTransition(characterRange2, null, true);
            leftCharacter = rightCharacter;
        }
        
        if (rightCharacter < Character.MAX_VALUE) {
            CharacterRange concludingCharacterRange = 
                    new CharacterRange(rightCharacter,
                                       Character.MAX_VALUE);
            
            transitionMap.addTransition(concludingCharacterRange, null, true);
        }
        
        return transitionMap;
    }
}
