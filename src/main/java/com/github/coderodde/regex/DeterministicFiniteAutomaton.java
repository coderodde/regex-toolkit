package com.github.coderodde.regex;

import com.github.coderodde.regex.DeterministicFiniteAutomatonStateTransitionFunction.TransitionFunctionEntry;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * This class implements a deterministic finite automaton.
 */
public final class DeterministicFiniteAutomaton 
        implements RegularExpressionMatcher {
    
    /**
     * This enumeration specifies which DFA minimization algorithm to use.
     */
    public enum MinimizationAlgorithm {
        HOPCROFT,
        MOORE;
    }
    
    /**
     * The initial state of the DFA.
     */
    private DeterministicFiniteAutomatonState initialState;
    
    /**
     * The set of <b>all</b> states in this DFA.
     */
    private final Set<DeterministicFiniteAutomatonState> states =
            new HashSet<>();
    
    /**
     * The set of accepting states.
     */
    private final Set<DeterministicFiniteAutomatonState> acceptingStateSet = 
            new HashSet<>();
    
    /**
     * The state transition function.
     */
    private DeterministicFiniteAutomatonStateTransitionFunction delta;
    
    /**
     * If this Boolean flag is set to {@code true}, upon matching a string, the
     * algorithm will actually simulate that of NFA in order to plausibly deal
     * with dot operators. An empty DFA does not contain a dot operator.
     */
    private boolean containsDotOperator = false;
    
    /**
     * Constructs an empty DFA with no states and transitions.
     */
    public DeterministicFiniteAutomaton() {
        this.delta = new DeterministicFiniteAutomatonStateTransitionFunction();
    }
    
    /**
     * Copy-constructs this DFA which has exactly the same structure as the 
     * {@code other} DFA.
     * 
     * @param other the DFA to copy. 
     */
    public DeterministicFiniteAutomaton(DeterministicFiniteAutomaton other) {
        Objects.requireNonNull(other, "The input DFA is null.");
        
        setInitialState(other.getInitialState());
        
        if (other.containsDotOperator) {
            setContansDotOperator();
        }
        
        this.acceptingStateSet.addAll(other.acceptingStateSet);
        
        this.delta = new DeterministicFiniteAutomatonStateTransitionFunction(
                        other.delta);
        
        
    }
    
    public void addAcceptingState(DeterministicFiniteAutomatonState q) {
        acceptingStateSet.add(q);
    }
    
    public boolean containsDotOperator() {
        return containsDotOperator;
    }
    
    /**
     * Returns the initial state.
     * 
     * @return the initial state.
     */
    public DeterministicFiniteAutomatonState getInitialState() {
        return this.initialState;
    }
    
    /**
     * Sets the initial state.
     * 
     * @param initialState the new initial state.
     */
    public void setInitialState(
        DeterministicFiniteAutomatonState initialState) {
        
        this.initialState =
                Objects.requireNonNull(
                        initialState,
                        "The input initial state is null.");
        
        this.states.add(initialState);
    }
    
    /**
     * Exposes the accepting state set.
     * 
     * @return the accepting state set.
     */
    Set<DeterministicFiniteAutomatonState> getAcceptingStates() {
        return Collections.unmodifiableSet(acceptingStateSet);
    }
    
    /**
     * Returns the set view over all states in this DFA, containing also
     * possible initial state and accepting states.
     * 
     * @return the set view over all states in this DFA. 
     */
    Set<DeterministicFiniteAutomatonState> getAllStates() {
        return Collections.unmodifiableSet(states);
    }
    
    /**
     * Validates an input string.
     * 
     * @param text the text to validate.
     * @return {@code true} only if the input string belongs to the regular 
     *         language recognized by this DFA.
     */
    @Override
    public boolean matches(String text) {
        if (containsDotOperator) {
            return matchesWithDotOperators(text);
        } else {
            return matchesWithoutDotOperators(text);
        }
    }
    
    /**
     * Computes the number of (reachable from the initial state) states in this 
     * DFA.
     * 
     * @return the number of reachable states in this DFA. 
     */
    public int getNumberOfStates() {
        return getAllReachableStates().size();
    }
    
    public DeterministicFiniteAutomaton minimizeViaHopcroftsAlgorithm() {
        
        int stateId = 0;
        
        List<Set<DeterministicFiniteAutomatonState>> P =
                minimizeViaHopcroftsAlgorithmImpl();
        
        // The output DFA:
        DeterministicFiniteAutomaton dfa = new DeterministicFiniteAutomaton();
        
        Map<Set<DeterministicFiniteAutomatonState>, 
            DeterministicFiniteAutomatonState> stateMap = new HashMap<>();
        
        for (Set<DeterministicFiniteAutomatonState> encodedState : 
                P) {
            
            DeterministicFiniteAutomatonState dfaState = 
                    new DeterministicFiniteAutomatonState(stateId++);
            
            stateMap.put(encodedState, dfaState);
            
            if (encodedState.contains(this.initialState)) {
                
                if (dfa.getInitialState() != null) {
                    throw new IllegalStateException(
                        "dfa.getInitialState() != null");
                }
                
                dfa.setInitialState(dfaState);
            }
            
            if (!Utils.intersection(encodedState,
                                    this.getAcceptingStates()).isEmpty()) {
                dfa.getAcceptingStates().add(dfaState);
            }
        }
        
        for (Set<DeterministicFiniteAutomatonState> equivalenceClass :
                P) {
            
            DeterministicFiniteAutomatonState currentDFAState =
                    stateMap.get(equivalenceClass);
            
            for (int codePoint : getLocalAlphabet(equivalenceClass)) {
                
                Set<DeterministicFiniteAutomatonState> 
                    followerEquivalenceClass = 
                        getNextEquivalenceClass(equivalenceClass, 
                                                codePoint);

                Set<DeterministicFiniteAutomatonState> nextEquivalenceClass = 
                    getNext(P, 
                            followerEquivalenceClass);
            
                DeterministicFiniteAutomatonState nextDFAState =
                    stateMap.get(nextEquivalenceClass);
            
                currentDFAState.addFollowerState(codePoint, nextDFAState);
            }
        }
        
        return dfa;
    }
    
    /**
     * Matches the input {@code text} in case this DFA does not contain dot 
     * operator transitions.
     * 
     * @param text the text to match.
     * @return {@code true} if and only if {@code text} belongs to the regular
     *         language recognized by this DFA.
     */
    private boolean matchesWithoutDotOperators(String text) {
        DeterministicFiniteAutomatonState state = deltaStar(text);
        
        if (state == null) {
            return false;
        }
        
        return acceptingStateSet.contains(state);
    }
    
    private boolean matchesWithDotOperators(String text) {
        Set<DeterministicFiniteAutomatonState> state = deltaStarDot(text);
        
        if (state == null || state.isEmpty()) {
            return false;
        }
        
        return !Utils.intersection(state, acceptingStateSet).isEmpty();
    }
    
    /**
     * Computes the next equivalence class starting from the input equivalence
     * class and following the character encoded by {@code codePoint}.
     * 
     * @param currentEquivalenceClass the current equivalence class.
     * @param codePoint               the code point.
     * @return the follower equivalence class.
     */
    private static Set<DeterministicFiniteAutomatonState> 
        getNextEquivalenceClass(
                Set<DeterministicFiniteAutomatonState> currentEquivalenceClass,
                int codePoint) {
            
        Set<DeterministicFiniteAutomatonState> nextDFAStateSet =
                new HashSet<>();
            
        for (DeterministicFiniteAutomatonState state : 
                currentEquivalenceClass) {
            
            nextDFAStateSet.add(state.traverse(codePoint));
        }
        
        return nextDFAStateSet;
    }
        
    public String computeRegularExression() {
        GeneralizedNondeterministicFiniteAutomaton gnfa = 
                new GeneralizedNondeterministicFiniteAutomaton();
        
        populateStatesIn(gnfa);
        
        while (gnfa.getNumberOfStates() > 2) {
            gnfa.rip();
        }
        
        return gnfa.getInitialState()
                .getRegularExpression(
                        gnfa.getAcceptingState());
    }
    
    private void setContansDotOperator() {
        this.containsDotOperator = true;
    }
    
    private static Set<DeterministicFiniteAutomatonState> 
        getNext(List<Set<DeterministicFiniteAutomatonState>> equivalenceClasses,
                Set<DeterministicFiniteAutomatonState> followerState) {
            
        for (Set<DeterministicFiniteAutomatonState> equivalenceClass
                : equivalenceClasses) {
            if (equivalenceClass.containsAll(followerState)) {
                return equivalenceClass;
            }
        }
        
        // TODO: deal with this:
        return Collections.emptySet();
//        throw new IllegalStateException();
    }
    
    private void populateStatesIn(
            GeneralizedNondeterministicFiniteAutomaton gnfa) {
        
        int stateId = 0;
        
        GeneralizedNondeterministicFiniteAutomatonState gnfaInitialState = 
                new GeneralizedNondeterministicFiniteAutomatonState(stateId++);
        
        GeneralizedNondeterministicFiniteAutomatonState gnfaAcceptingState = 
                new GeneralizedNondeterministicFiniteAutomatonState(stateId++);
        
        Map<DeterministicFiniteAutomatonState, 
            GeneralizedNondeterministicFiniteAutomatonState> stateMap = 
                new HashMap<>();
        
        // Create all the GNFA states corresponding to this DFA's states:
        Set<DeterministicFiniteAutomatonState> allDFAStates = 
                this.getAllReachableStates();
        
        for (DeterministicFiniteAutomatonState dfaState : allDFAStates) {
            GeneralizedNondeterministicFiniteAutomatonState gnfaState = 
                new GeneralizedNondeterministicFiniteAutomatonState(stateId++);
            
            stateMap.put(dfaState, gnfaState);
        }
        
        gnfa.setNumberOfStates(2 + allDFAStates.size());
        
        // Initialize all the GNFA state transitions:
        for (DeterministicFiniteAutomatonState dfaState : allDFAStates) {
            GeneralizedNondeterministicFiniteAutomatonState gnfaState = 
                    stateMap.get(dfaState);
            
            DeterministicFiniteAutomatonStateTransitionFunction transitionMap = 
                    dfaState.getTransitionMap();
            
            for (int i = 0; i != transitionMap.size(); i++) {
                TransitionFunctionEntry entry = transitionMap.get(i);
                
                // TODO: Check this:
                GeneralizedNondeterministicFiniteAutomatonState 
                        gnfaFollowerState = stateMap.get(null);
                
                String currentRegex = 
                        gnfaState.getRegularExpression(gnfaFollowerState);
                
                if (currentRegex == null) {
//                    gnfaState.addRegularExpression(
//                            gnfaFollowerState, 
//                            Character.toString(entry.getKey()));
                } else {
                    String tentativeRegex = 
                            gnfaState.getRegularExpression(gnfaFollowerState);
                    
//                    gnfaState.addRegularExpression(
//                            gnfaFollowerState, 
//                            tentativeRegex 
//                                    + "|" 
//                                    + Character.toString(entry.getKey()));
                }
            }
        }
        
        // Handle terminal states:
        gnfa.setInitialState(gnfaInitialState);
        gnfa.setAcceptingState(gnfaAcceptingState);
        gnfaInitialState.addEpsilonTransition(stateMap.get(this.initialState));
        
        for (DeterministicFiniteAutomatonState acceptingDFAState 
                : acceptingStateSet) {
            GeneralizedNondeterministicFiniteAutomatonState gnfaState = 
                    stateMap.get(acceptingDFAState);
            
            gnfaState.addEpsilonTransition(gnfaAcceptingState);
        }
    }
    
    public DeterministicFiniteAutomaton 
        minimize(MinimizationAlgorithm algorithm) {
        
        DeterministicFiniteAutomaton targetDfa = 
                new DeterministicFiniteAutomaton(this);
            
        List<Set<DeterministicFiniteAutomatonState>> equivalenceClasses;
        
        
    }
    
    /**
     * Returns the list of equivalence classes from which a minimized DFA may be
     * built.
     * 
     * @return the list of equivalence classes. 
     */
    private List<Set<DeterministicFiniteAutomatonState>>
         minimizeViaHopcroftsAlgorithmImpl() {
             
        List<Set<DeterministicFiniteAutomatonState>> p = new LinkedList<>();
         Set<Set<DeterministicFiniteAutomatonState>> w = new HashSet<>();
        
        Set<DeterministicFiniteAutomatonState> reachableStates = 
                getAllReachableStates();
        
        pruneUnreachableStates(reachableStates);
        
        Set<DeterministicFiniteAutomatonState> F = getAcceptingStates();
        
        if (!F.isEmpty()) {
            p.add(F);
            w.add(F);
        }
        
        Set<DeterministicFiniteAutomatonState> QminusF = 
            Utils.difference(getAllStates(), F);
        
        if (!QminusF.isEmpty()) {
            p.add(QminusF);
            w.add(QminusF);
        }
        
        while (!w.isEmpty()) {
            Set<DeterministicFiniteAutomatonState> a = w.iterator().next();
            
            w.remove(a);
            
            for (int codePoint : getLocalAlphabet(a)) {
                Set<DeterministicFiniteAutomatonState> x = 
                        getX(codePoint, 
                             reachableStates, 
                             a);
                
                ListIterator<Set<DeterministicFiniteAutomatonState>>
                        pListIterator = p.listIterator();
                
                while (pListIterator.hasNext()) {
                    Set<DeterministicFiniteAutomatonState> y =
                            pListIterator.next();
                    
                    Set<DeterministicFiniteAutomatonState> intersection = 
                            Utils.intersection(x, y);
                    
                    if (intersection.isEmpty()) {
                        continue;
                    }
                    
                    Set<DeterministicFiniteAutomatonState> difference = 
                            Utils.difference(y, x);
                    
                    if (difference.isEmpty()) {
                        continue;
                    }
                    
                    pListIterator.remove();
                    pListIterator.add(intersection);
                    pListIterator.add(difference);
                    
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
        getX(int ch, Set<DeterministicFiniteAutomatonState> a) {
            
        Set<DeterministicFiniteAutomatonState> x = new HashSet<>();
        
        for (DeterministicFiniteAutomatonState q : this.states) {
            if (a.contains(q.traverse(ch))) {
                x.add(q);
            }
        }
        
        return x;
    }
    
    private Set<Integer> 
        getLocalAlphabet(Set<DeterministicFiniteAutomatonState> stateSet) {
            
        Set<Integer> localAlphabet = new HashSet<>();
        
        for (DeterministicFiniteAutomatonState state : stateSet) {
            DeterministicFiniteAutomatonStateTransitionFunction transitionMap = 
                    state.getTransitionMap();
            
            for (TransitionFunctionEntry transitionMapEntry : transitionMap) {
                if (transitionMapEntry != null) {
                    CodePointRange codePointRange = 
                            transitionMapEntry.getCharacterRange();
                    
                    for (int codePoint : codePointRange) {
                        localAlphabet.add(codePoint);
                    }
                }
            }
            
//            if (state.getTransitionMap().get(0))
//            localAlphabet.addAll(state.followerMap.keySet())
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
        int textCodePointIndex = 0;
        DeterministicFiniteAutomatonState currentState = initialState;
        
        while (textCodePointIndex != n) {
            int codePoint = text.codePointAt(textCodePointIndex);
            
            DeterministicFiniteAutomatonState nextState =
                    currentState.traverse(codePoint);
            
            if (nextState == null) {
                return null;
            }
            
            currentState = nextState;
            textCodePointIndex++;
        }
        
        return currentState;
    }
    
    private Set<DeterministicFiniteAutomatonState> deltaStarDot(String text) {
        int n = text.length();
        int textCodePointIndex = 0;
        Set<DeterministicFiniteAutomatonState> currentState = new HashSet<>();
        currentState.add(initialState);
        
        while (textCodePointIndex != n) {
            int codePoint = text.codePointAt(textCodePointIndex);
            Set<DeterministicFiniteAutomatonState> nextStates = new HashSet<>();
            
            for (DeterministicFiniteAutomatonState q : currentState) {
                if (containsDotOperator) {
                    nextStates.add(
                            q.getTransitionMap().getDotTransitionState());
                }
                
                DeterministicFiniteAutomatonState next = 
                        q.getTransitionMap().getTargetState(codePoint);
                
                nextStates.add(next);
            }
            
            currentState = nextStates;
        }
        
        return currentState;
    }
    
    /**
     * A simple, directed BFS starting from he initial state and trying to reach
     * as many states as feasible.
     * 
     * @return a set of states reachable from the initial state.
     */
    private Set<DeterministicFiniteAutomatonState> getAllReachableStates() {
        Deque<DeterministicFiniteAutomatonState> queue = new ArrayDeque<>();
        Set<DeterministicFiniteAutomatonState> visited = new HashSet<>();
        
        queue.addLast(initialState);
        visited.add(initialState);
        
        while (!queue.isEmpty()) {
            DeterministicFiniteAutomatonState state = queue.removeFirst();
            DeterministicFiniteAutomatonStateTransitionFunction transitionMap = 
                    state.getTransitionMap();
            
            for (int i = 0; i != transitionMap.size(); i++) {
                TransitionFunctionEntry transitionMapEntry = transitionMap.get(i);
                DeterministicFiniteAutomatonState followerState = 
                        transitionMapEntry.getFollowerState();
                
                if (!visited.contains(followerState)) {
                    visited.add(followerState);
                    queue.addLast(followerState);
                }
            }
        }
        
        return visited;
    }

    private void pruneUnreachableStates(
        Set<DeterministicFiniteAutomatonState> reachableStates) {
        
        Iterator<DeterministicFiniteAutomatonState> iterator =
                states.iterator();
        
        while (iterator.hasNext()) {
            DeterministicFiniteAutomatonState state = iterator.next();
            
            if (!reachableStates.contains(state)) {
                state.clear();
                iterator.remove();
            }
        }
    }
}
