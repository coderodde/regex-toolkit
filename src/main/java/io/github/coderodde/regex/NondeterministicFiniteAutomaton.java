package io.github.coderodde.regex;

import io.github.coderodde.regex.parser.ast.RegexParser;
import io.github.coderodde.regex.tokenizer.RegexTokenizer;
import io.github.coderodde.regex.parser.ast.tokens.RegexToken;
import io.github.coderodde.regex.parser.ast.tree.RegexNode;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * This class implements a 
 * <a href="https://en.wikipedia.org/wiki/Nondeterministic_finite_automaton">Nondeterministic finite automaton</a>.
 */
public final class NondeterministicFiniteAutomaton
        implements RegularExpressionMatcher {
    
    private NondeterministicFiniteAutomatonState initialState;
    private final Set<NondeterministicFiniteAutomatonState> acceptingStates = 
            new HashSet<>();
    
    public NondeterministicFiniteAutomatonState getInitialState() {
        return initialState;
    }
    
    public Set<NondeterministicFiniteAutomatonState> getAcceptingStates() {
        return Collections.unmodifiableSet(acceptingStates);
    }
    
    public void setInitialState(
            NondeterministicFiniteAutomatonState initialState) {
        
        this.initialState = 
                Objects.requireNonNull(
                        initialState,
                        "The input initial state is null.");
    }
    
    public void addAcceptingState(
            NondeterministicFiniteAutomatonState acceptingState) {
        
        acceptingStates.add(acceptingState);
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
                    : state.followerStateIterable()) {
                
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
        Objects.requireNonNull(text, "The input text is null.");
        
        Set<NondeterministicFiniteAutomatonState> states = 
            epsilonExpand(Set.of(initialState));
        
        for (int cp : text.codePoints().toArray()) {
            Set<NondeterministicFiniteAutomatonState> nextStates =
                new HashSet<>();
            
            for (NondeterministicFiniteAutomatonState state : states) {
                Set<NondeterministicFiniteAutomatonState> followers = 
                    state.getGoalStates(cp);
                
                if (followers != null) {
                    nextStates.addAll(followers);
                }
                
                NondeterministicFiniteAutomatonState dotState = 
                    state.getDotTransition();
                
                if (dotState != null) {
                    nextStates.add(dotState);
                }
            }
            
            if (nextStates.isEmpty()) {
                return false;
            }
            
            states = epsilonExpand(nextStates);
        }
    
        return isAcceptingStateSet(states);
    }
    
    public DeterministicFiniteAutomaton 
        convertToDetermenisticFiniteAutomaton() {
        return new NFAToDFAConverter(this).convert();
    }
        
    public static NondeterministicFiniteAutomaton compile(String regex) {
        List<RegexToken> infixTokens = new RegexTokenizer().tokenize(regex);
        RegexParser parser = new RegexParser(infixTokens);
        RegexNode abstractSyntaxTree = parser.parse();
        
        return new NondeterministicFiniteAutomatonCompiler(abstractSyntaxTree)
            .compile();
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
        
        return !Utils.intersection(finalStateSet, acceptingStates).isEmpty();
    }
    
    private final class NFAToDFAConverter {
        
        private static final int MIN_CP = 0x000000;
        private static final int MAX_CP = 0x10FFFF;
        
        private final Deque<Set<NondeterministicFiniteAutomatonState>> 
                stateQueue = new ArrayDeque<>();
        
        private final Map<Set<NondeterministicFiniteAutomatonState>,
                          DeterministicFiniteAutomatonState> map =
            new HashMap<>();
        
        private final NondeterministicFiniteAutomaton nfa;
        private int stateId = 0;
        private final DeterministicFiniteAutomaton dfa = 
                  new DeterministicFiniteAutomaton();

        NFAToDFAConverter(NondeterministicFiniteAutomaton nfa) {
            this.nfa = nfa;
        }
        
        DeterministicFiniteAutomaton convert() {
            
            Set<NondeterministicFiniteAutomatonState> startSet = 
                epsilonExpand(Set.of(nfa.getInitialState()));
            
            DeterministicFiniteAutomatonState dfaStart = 
                getOrCreateDFAState(startSet);
            
            dfa.setInitialState(dfaStart);
            
            if (isAcceptingStateSet(startSet)) {
                dfa.addAcceptingState(dfaStart);
            }
            
            while (!stateQueue.isEmpty()) {
                
                Set<NondeterministicFiniteAutomatonState> currentSet = 
                    stateQueue.removeFirst();
                
                DeterministicFiniteAutomatonState currentDFAState = 
                    map.get(currentSet);
                
                Set<CodePointRange> alphabet = 
                    computeDisjointAlphabet(currentSet);
                
                for (CodePointRange range : alphabet) {
                    Set<NondeterministicFiniteAutomatonState> nextSet = 
                        move(currentSet, range);
                    
                    if (nextSet.isEmpty()) {
                        continue;
                    }
                    
                    nextSet = epsilonExpand(nextSet);
                    
                    DeterministicFiniteAutomatonState nextDFAState = 
                        getOrCreateDFAState(nextSet);
                    
                    if (isAcceptingStateSet(nextSet)) {
                        dfa.addAcceptingState(nextDFAState);
                    }
                    
                    dfa.addTransition(currentDFAState, range, nextDFAState);
                }
            }
            
            return dfa;
        }
        
        private DeterministicFiniteAutomatonState getOrCreateDFAState(
            Set<NondeterministicFiniteAutomatonState> nfaStateSet) {
            Set<NondeterministicFiniteAutomatonState> key = 
                new HashSet<>(nfaStateSet);
            
            DeterministicFiniteAutomatonState dfaState = map.get(key);
            
            if (dfaState != null) {
                return dfaState;
            }
            
            dfaState = new DeterministicFiniteAutomatonState(stateId++);
            
            map.put(key, dfaState);
            stateQueue.addLast(key);
            
            return dfaState;
        }
        
        private Set<NondeterministicFiniteAutomatonState> move(
                Set<NondeterministicFiniteAutomatonState> states,
                CodePointRange range) {
            
            Set<NondeterministicFiniteAutomatonState> result = new HashSet<>();
            
            for (NondeterministicFiniteAutomatonState state : states) {
                
                for (int i = 0; i < state.getTransitionCount(); ++i) {
                    NondeterministicFiniteAutomatonState
                        .TransitionFunctionEntry e = 
                        state.getTransition(i);
                    
                    if (intersects(e.getCharacterRange(), range)) {
                        result.addAll(e.getGoalStates());
                    }
                }
                
                NondeterministicFiniteAutomatonState dotState = 
                    state.getDotTransition();

                if (dotState != null) {
                    result.add(dotState);
                }
            }
            
            return result;
        }
        
        private Set<CodePointRange> computeDisjointAlphabet(
                Set<NondeterministicFiniteAutomatonState> states) {
            
            Set<Integer> points = new HashSet<>();
            
            boolean hasDot = false;
            
            for (NondeterministicFiniteAutomatonState state : states) {
                if (state.getDotTransition() != null) {
                    hasDot = true;
                }
                
                for (int i = 0; i < state.getTransitionCount(); ++i) {
                    CodePointRange range = 
                        state.getTransition(i).getCharacterRange();
                    
                    points.add(range.getMinimumCodePoint());
                    
                    if (range.getMaximumCodePoint() < MAX_CP) {
                        points.add(range.getMaximumCodePoint() + 1);
                    }
                }
            }
            
            if (hasDot) {
                points.add(MIN_CP);
                points.add(MAX_CP + 1);
            }
            
            Integer[] sorted = points.toArray(Integer[]::new);
            Arrays.sort(sorted);
            
            Set<CodePointRange> result = new HashSet<>();
            
            for (int i = 0; i + 1 < sorted.length; ++i) {
                int left  = sorted[i];
                int right = sorted[i + 1] - 1;
                
                if (left <= right) {
                    result.add(new CodePointRange(left, right));
                }
            }
            
            return result;
        }
        
        private boolean intersects(CodePointRange a, CodePointRange b) {
            return a.getMinimumCodePoint() <= b.getMaximumCodePoint()
                && b.getMinimumCodePoint() <= a.getMaximumCodePoint();
        }
    }
    
    static DeterministicFiniteAutomatonStateTransitionFunction
        computeTransitionMapWithoutPeriodWildcard(Set<Integer> alphabet,
                                                  int startId) {
        
        DeterministicFiniteAutomatonStateTransitionFunction transitionMap = 
                new DeterministicFiniteAutomatonStateTransitionFunction();
        
        for (int codePoint : alphabet) {
            transitionMap.addTransition(
                    codePoint, 
                    new DeterministicFiniteAutomatonState(startId++),
                    null);
        }
        
        return transitionMap;
    }
}