    package io.github.coderodde.regex;

    import io.github.coderodde.regex.DeterministicFiniteAutomatonStateTransitionFunction.TransitionFunctionEntry;
    import java.util.ArrayDeque;
    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.Deque;
    import java.util.HashMap;
    import java.util.HashSet;
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
        
        private boolean anchoredAtStart;
        private boolean anchoredAtEnd;
        private int stateCounter;

        /**
         * The set of accepting states.
         */
        private final Set<DeterministicFiniteAutomatonState> acceptingStateSet = 
                new HashSet<>();
        
        /**
         * Constructs an empty DFA with no states and transitions.
         */
        public DeterministicFiniteAutomaton() {
            this(false, false);
        }
        
        DeterministicFiniteAutomaton(boolean anchoredAtStart,
                                     boolean anchoredAtEnd) {
            this.anchoredAtStart = anchoredAtStart;
            this.anchoredAtEnd   = anchoredAtEnd;
        }

        /**
         * Copy-constructs this DFA which has exactly the same structure as the 
         * {@code other} DFA.
         * 
         * @param other the DFA to copy. 
         */
        public DeterministicFiniteAutomaton(
               DeterministicFiniteAutomaton other) {
            
            Objects.requireNonNull(other, "The input DFA is null.");

            Map<DeterministicFiniteAutomatonState, 
                DeterministicFiniteAutomatonState> stateMap = new HashMap<>();
            
            int id = 0;
            
            // Copy all states:
            for (DeterministicFiniteAutomatonState oldState : other.states) {
                stateMap.put(oldState, 
                             new DeterministicFiniteAutomatonState(id++));
            }
            
            // Set initial state:
            setInitialState(stateMap.get(other.initialState));
            
            // Set accepting states:
            for (DeterministicFiniteAutomatonState oldAcceptingState
                    : other.acceptingStateSet) {
                addAcceptingState(stateMap.get(oldAcceptingState));
            }
            
            // Copy state transitions:
            for (DeterministicFiniteAutomatonState oldState : other.states) {
                DeterministicFiniteAutomatonState newState = 
                    stateMap.get(oldState);
                
                DeterministicFiniteAutomatonStateTransitionFunction tf = 
                    oldState.getTransitionFunction();
                
                for (int i = 0; i < tf.size(); ++i) {
                    TransitionFunctionEntry entry = tf.get(i);
                    
                    DeterministicFiniteAutomatonState newTarget = 
                        stateMap.get(entry.getGoalState());
                    
                    addTransition(newState,
                                  entry.getCharacterRange(), 
                                  newTarget);
                }
            }
        }
        
        public DeterministicFiniteAutomatonState createState() {
            DeterministicFiniteAutomatonState state = 
                new DeterministicFiniteAutomatonState(stateCounter++);
            
            states.add(state);
            return state;
        }
        
        public NondeterministicFiniteAutomaton toNFA() {
            Map<DeterministicFiniteAutomatonState,
                NondeterministicFiniteAutomatonState> m = new HashMap<>();
            
            NondeterministicFiniteAutomaton nfa = 
                new NondeterministicFiniteAutomaton();
            
            for (DeterministicFiniteAutomatonState dfaState : states) {
                m.put(dfaState, nfa.createState());
            }
            
            nfa.setInitialState(m.get(this.initialState));
            
            for (DeterministicFiniteAutomatonState acceptingState 
                    : this.acceptingStateSet) {
                nfa.addAcceptingState(m.get(acceptingState));
            }
            
            for (DeterministicFiniteAutomatonState dfaState : states) {
                
                DeterministicFiniteAutomatonStateTransitionFunction tf = 
                    dfaState.getTransitionFunction();
                
                NondeterministicFiniteAutomatonState nfaState = m.get(dfaState);
                
                for (TransitionFunctionEntry tfe : tf) {
                    nfaState.addTransition(tfe.getCharacterRange(), 
                                           m.get(tfe.getGoalState()));
                }
            }
            
            return nfa;
        }

        /**
         * Adds an accepting state.
         * 
         * @param q an accepting state.
         */
        public void addAcceptingState(DeterministicFiniteAutomatonState q) {
            acceptingStateSet.add(
                Objects.requireNonNull(q, "The input state is null."));

            states.add(q);
        }

        public void addTransition(DeterministicFiniteAutomatonState startState,
                                  CodePointRange codePointRange,
                                  DeterministicFiniteAutomatonState goalState) {
            
            Objects.requireNonNull(startState, "The start state is null.");
            Objects.requireNonNull(
                codePointRange, 
                "The code point range is null.");
            
            Objects.requireNonNull(goalState, "The goal state is null.");

            startState.addFollowerState(codePointRange, goalState);

            states.add(startState);
            states.add(goalState);
        }

        public void addTransition(DeterministicFiniteAutomatonState startState,
                                  int codePoint,
                                  DeterministicFiniteAutomatonState goalState) {

            addTransition(startState, new CodePointRange(codePoint), goalState);
        }

        public void addTransition(DeterministicFiniteAutomatonState startState,
                                  char symbol,
                                  DeterministicFiniteAutomatonState goalState) {

            addTransition(startState, new CodePointRange((int) symbol), goalState);
        }

        public int getNumberOfStates() {
            return states.size();
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
            DeterministicFiniteAutomatonState state = deltaStar(text);

            if (state == null) {
                return false;
            }

            return acceptingStateSet.contains(state);
        }
        
        public boolean find(String text) {
            Objects.requireNonNull(text, "The input text is null.");
            
            int[] codePoints = text.codePoints().toArray();
            
            if (anchoredAtStart) {
                return findStartingAt(codePoints, 0);
            }
            
            for (int startIndex = 0; 
                     startIndex <= codePoints.length;
                     startIndex++) {
                if (findStartingAt(codePoints, startIndex)) {
                    return true;
                }
            }
            
            return false;
        }
        
        private boolean findStartingAt(int[] codePoints, int startIndex) {
            DeterministicFiniteAutomatonState state = initialState;
            
            if (acceptingStateSet.contains(state)) {
                return !anchoredAtEnd || startIndex == codePoints.length;
            }
            
            for (int i = startIndex; i < codePoints.length; i++) {
                state = state.traverse(codePoints[i]);
                
                if (state == null) {
                    return false;
                }
                
                if (acceptingStateSet.contains(state)) {
                    if (!anchoredAtEnd || i + 1 == codePoints.length) {
                        return true;
                    }
                }
            }
            
            return false;
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

                DeterministicFiniteAutomatonStateTransitionFunction 
                        transitionMap = 
                        dfaState.getTransitionFunction();

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

            Objects.requireNonNull(
                algorithm,
                "The minimization algorithm selector is null.");

            DeterministicFiniteAutomaton targetDfa = 
                    new DeterministicFiniteAutomaton(this);

            Set<DeterministicFiniteAutomatonState> reachableStateSet = 
                targetDfa.getAllReachableStates();

            targetDfa.pruneUnreachableStates(reachableStateSet);

            switch (algorithm) {
                case HOPCROFT:
                    return buildDfaImpl(
                        targetDfa.minimizeViaHopcroftsAlgorithmImpl());

                case MOORE:
                    return buildDfaImpl(
                        targetDfa.minimizeViaMooresAlgorithmImpl());

                default:
                    throw new EnumConstantNotPresentException(
                        MinimizationAlgorithm.class, 
                        algorithm.name());
            }
        }

        private DeterministicFiniteAutomaton buildDfaImpl(
            List<Set<DeterministicFiniteAutomatonState>> equivalenceClasses) {

            int stateId = 0;
            DeterministicFiniteAutomaton dfa = new DeterministicFiniteAutomaton();
            Map<Set<DeterministicFiniteAutomatonState>, 
                    DeterministicFiniteAutomatonState> blockToStateMap = 
                    new HashMap<>(equivalenceClasses.size());
            
            Map<DeterministicFiniteAutomatonState,
                Set<DeterministicFiniteAutomatonState>> stateToBlockMap = 
                    new HashMap<>();

            // Create all states of the minimized DFA. Also, sets the initial state
            // and possibly creates the accepting state set:
            for (Set<DeterministicFiniteAutomatonState> equivalenceClass
                    : equivalenceClasses) {

                DeterministicFiniteAutomatonState newState = 
                        new DeterministicFiniteAutomatonState(stateId++);

                blockToStateMap.put(equivalenceClass, newState);
                
                for (DeterministicFiniteAutomatonState oldState
                    : equivalenceClass) {
                    
                    stateToBlockMap.put(oldState, equivalenceClass);
                }
                
                if (equivalenceClass.contains(initialState)) {
                    dfa.setInitialState(newState);
                }
                
                if (!Utils.intersection(
                        equivalenceClass, 
                        acceptingStateSet).isEmpty()) {
                    
                    dfa.addAcceptingState(newState);
                }
            }

            // Builds the state transitions:
            for (Set<DeterministicFiniteAutomatonState> equivalenceClass 
                    : equivalenceClasses) {

                DeterministicFiniteAutomatonState representativeState = 
                    equivalenceClass.iterator().next();
                
                DeterministicFiniteAutomatonState sourceState = 
                    blockToStateMap.get(equivalenceClass);
                
                for (CodePointRange codePointRange 
                    : representativeState.getTransitionFunction().getAlphabet()) {
                    
                    DeterministicFiniteAutomatonState oldTargetState = 
                        representativeState.traverse(codePointRange);
                    
                    Set<DeterministicFiniteAutomatonState> targetBlock = 
                        stateToBlockMap.get(oldTargetState);
                    
                    DeterministicFiniteAutomatonState targetState = 
                        blockToStateMap.get(targetBlock);
                    
                    dfa.addTransition(sourceState, 
                                      codePointRange,
                                      targetState);
                }
            }

            return dfa;
        }

        private List<Set<DeterministicFiniteAutomatonState>>
             minimizeViaMooresAlgorithmImpl() {
             List<Set<DeterministicFiniteAutomatonState>> p = new ArrayList<>();
             
             Set<DeterministicFiniteAutomatonState> bacc = 
                     new HashSet<>(getAcceptingStates());
             
             Set<DeterministicFiniteAutomatonState> brej = 
                 setminus(new HashSet<>(states),
                          new HashSet<>(acceptingStateSet));

             if (!bacc.isEmpty()) {
                 p.add(bacc);
             }

             if (!brej.isEmpty()) {
                 p.add(brej);
             }

             Map<DeterministicFiniteAutomatonState, Integer> blockIdMap = 
                 buildBlockIdMap(p);

             boolean changed = true;

             while (changed) {
                 changed = false;

                 List<Set<DeterministicFiniteAutomatonState>> pNew = 
                     new ArrayList<>();

                 for (Set<DeterministicFiniteAutomatonState> block : p) {
                     
                     Map<List<Integer>, 
                         Set<DeterministicFiniteAutomatonState>> groups =
                             new HashMap<>();

                     for (DeterministicFiniteAutomatonState q : block) {
                         List<Integer> signature = new ArrayList<>();

                         for (CodePointRange codePointRange 
                             : getTotalAlphabet()) {
                             
                             DeterministicFiniteAutomatonState nextState =
                                     q.traverse(codePointRange);

                             Integer blockId = blockIdMap.get(nextState);
                             
                             signature.add(blockId);
                         } 

                         groups.computeIfAbsent(
                             signature, 
                             x -> new HashSet<>()).add(q);
                     }

                     if (groups.size() == 1) {
                         pNew.add(block);
                     } else {
                         changed = true;
                         pNew.addAll(groups.values());
                     }
                 }

                 p = pNew;
                 blockIdMap = buildBlockIdMap(p);
             }

             return p;
        }

        private Map<DeterministicFiniteAutomatonState, Integer>
            buildBlockIdMap(List<Set<DeterministicFiniteAutomatonState>> p) {
                
            Map<DeterministicFiniteAutomatonState, Integer> blockIdMap = 
                    new HashMap<>();

            int id = 0;

            for (Set<DeterministicFiniteAutomatonState> block : p) {
                for (DeterministicFiniteAutomatonState q : block) {
                    blockIdMap.put(q, id);
                }

                ++id;
            }

            return blockIdMap;
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

                for (CodePointRange codePoint : getTotalAlphabet()) {
                    Set<DeterministicFiniteAutomatonState> x = 
                        getPredecessorStates(codePoint, a);

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
            getPredecessorStates(CodePointRange codePointRange,
                 Set<DeterministicFiniteAutomatonState> targetSet) {

            Set<DeterministicFiniteAutomatonState> predecessorStateSet = 
                    new HashSet<>();

            for (DeterministicFiniteAutomatonState q : states) {
                DeterministicFiniteAutomatonState nextState = 
                        q.traverse(codePointRange);
                
                if (targetSet.contains(nextState)) {
                    predecessorStateSet.add(q);
                }
            }

            return predecessorStateSet;
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
                textCodePointIndex += Character.charCount(codePoint);
            }

            return currentState;
        }

        private List<CodePointRange> getTotalAlphabet() {
            List<CodePointRange> alphabet = new ArrayList<>();

            for (DeterministicFiniteAutomatonState q : states) {
                alphabet.addAll(q.getTransitionFunction().getAlphabet());
            }

            return alphabet;
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
                DeterministicFiniteAutomatonStateTransitionFunction 
                    transitionMap = state.getTransitionFunction();

                for (int i = 0; i != transitionMap.size(); i++) {
                    TransitionFunctionEntry transitionMapEntry = 
                            transitionMap.get(i);

                    DeterministicFiniteAutomatonState followerState = 
                            transitionMapEntry.getGoalState();

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
            states.retainAll(reachableStates);
            acceptingStateSet.retainAll(reachableStates);
        }

        /**
         * Returns the set {@code a} setminus {@code b}.
         * 
         * @param a the set to minus from.
         * @param b the set to minus.
         * @return {@code a \setminus b}.
         */
        private static <T> Set<T> setminus(Set<T> a, Set<T> b) {
            a.removeAll(b);
            return a;
        }
    }
