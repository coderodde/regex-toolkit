package com.github.coderodde.regex;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 17, 2023)
 * @since 1.6 (Nov 17, 2023)
 */
public final class NondeterministicFiniteAutomaton {
    
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
//            
//        DeterministicFiniteAutomaton dfa = new DeterministicFiniteAutomaton();
//        
//        int stateID = 0;
//        
//        Set<NondeterministicFiniteAutomatonState> startState =
//                new HashSet<>(Arrays.asList(initialState));
//        
//        startState = epsilonExpand(startState);
//        
//        Deque<Set<NondeterministicFiniteAutomatonState>> stateQueue = 
//                new ArrayDeque<>(Arrays.asList(startState));
//        
//        Map<Set<NondeterministicFiniteAutomatonState>, 
//            DeterministicFiniteAutomatonState> stateMap = new HashMap<>();
//        
//        DeterministicFiniteAutomatonState dfaInitialState = 
//                new DeterministicFiniteAutomatonState(stateID++);
//        
//        dfa.setInitialState(dfaInitialState);
//        stateMap.put(startState, dfaInitialState);
//        Set<NondeterministicFiniteAutomatonState> currentNFAState = startState;
//        DeterministicFiniteAutomatonState currentDFAState = dfaInitialState;
//        
//        while (!stateQueue.isEmpty()) {
//            Set<NondeterministicFiniteAutomatonState> currentState = 
//                    stateQueue.removeFirst();
//            
//            if (currentState.isEmpty()) {
//                continue;
//            }
//            
//            Set<Character> localAlphabet = getLocalAlphabet(currentState);
//            
//            for (Character character : localAlphabet) {
//                Set<NondeterministicFiniteAutomatonState> followerStateSet = 
//                        new HashSet<>();
//                
//                for (NondeterministicFiniteAutomatonState s : currentState) {
//                    followerStateSet.addAll(s.getFollowingStates(character));
//                }
//                
//                followerStateSet = epsilonExpand(followerStateSet);
//                
//                if (stateMap.containsKey(followerStateSet)) {
//                    continue;
//                } else {
//                    stateQueue.addLast(followerStateSet);
//                }
//                
//                DeterministicFiniteAutomatonState nextDFAState = 
//                        new DeterministicFiniteAutomatonState(stateID++);
//                
//                currentDFAState.addFollowerState(character, nextDFAState);
//                stateMap.put(currentNFAState, nextDFAState);
//            }
//        }
//        
//        return dfa;
    }
        
    public static NondeterministicFiniteAutomaton compile(String regex) {
        List<RegexToken> infixTokens = new RegexTokenizer().tokenize(regex);
        Deque<RegexToken> postfixTokens = 
                new RegexInfixToPostfixConverter()
                        .convert(infixTokens);
        
        return new NondeterministicFiniteAutomatonCompiler()
                .compile(postfixTokens);
    }
    
    private Set<NondeterministicFiniteAutomatonState> 
        traverse(Set<NondeterministicFiniteAutomatonState> states, 
                 Character character) {
        Set<NondeterministicFiniteAutomatonState> nextStates = 
                new HashSet<>();
        
        for (NondeterministicFiniteAutomatonState state : states) {
            nextStates.addAll(state.getFollowingStates(character));
        }
        
        return nextStates;
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
    
    private static final class NFAToDFAConverter {
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
                
                if (currentNFAState.isEmpty()) {
                    continue;
                }
                
                DeterministicFiniteAutomatonState currentDFAState = 
                        stateMap.get(currentNFAState);
                
                Set<Character> localAlphabet = 
                        getLocalAlphabet(currentNFAState);
                
                for (Character character : localAlphabet) {
                    Set<NondeterministicFiniteAutomatonState> nextNFAState = 
                            new HashSet<>();
                    
                    for (NondeterministicFiniteAutomatonState state : 
                            currentNFAState) {
                        Set<NondeterministicFiniteAutomatonState> 
                                followingStates = 
                                state.getFollowingStates(character);
                        
                        if (followingStates != null) {
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
                    
                    currentDFAState.addFollowerState(character, nextDFAState);
                }
            }
            
            return dfa;
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
        }
        
        private int getStateID() {
            return stateID++;
        }
    }
}
