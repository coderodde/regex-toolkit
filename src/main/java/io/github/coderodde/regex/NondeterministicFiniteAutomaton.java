package io.github.coderodde.regex;

import io.github.coderodde.regex.NondeterministicFiniteAutomatonState.TransitionFunctionEntry;
import io.github.coderodde.regex.parser.ast.RegexParser;
import io.github.coderodde.regex.parser.ast.RegexTokenizationResult;
import io.github.coderodde.regex.tokenizer.RegexTokenizer;
import io.github.coderodde.regex.parser.ast.tree.RegexNode;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * This class implements a 
 * <a href="https://en.wikipedia.org/wiki/Nondeterministic_finite_automaton">Nondeterministic finite automaton</a>.
 */
public final class NondeterministicFiniteAutomaton
        implements RegularExpressionMatcher {
    
    /**
     * The set of all present states in this automaton.
     */
    private final Set<NondeterministicFiniteAutomatonState> states = 
        new HashSet<>();
    
    private boolean anchoredAtStart;
    private boolean anchoredAtEnd;
    private int stateCounter;

    public NondeterministicFiniteAutomaton(RegexTokenizationResult result) {
        this.anchoredAtStart = result.anchoredAtStart();
        this.anchoredAtEnd   = result.anchoredAtEnd();
    }
    
    public NondeterministicFiniteAutomaton() {
        
    }
    
    public NondeterministicFiniteAutomaton(
           NondeterministicFiniteAutomaton other) {
        
        Set<NondeterministicFiniteAutomatonState> oldStates = 
            other.getAllReachableStates();
        
        Map<NondeterministicFiniteAutomatonState,
            NondeterministicFiniteAutomatonState> m = new HashMap<>();
        
        // Copy states:
        for (NondeterministicFiniteAutomatonState oldState : oldStates) {
            NondeterministicFiniteAutomatonState  newState = createState();
            m.put(oldState, newState);
        }
        
        // Copy initial state:
        setInitialState(m.get(other.getInitialState()));
        
        // Copy accepting states:
        for (NondeterministicFiniteAutomatonState oldAcceptingState 
            : other.acceptingStates) {
            
            if (oldStates.contains(oldAcceptingState)) {
                addAcceptingState(m.get(oldAcceptingState));
            }
        }
        
        // Copy state transitions:
        for (NondeterministicFiniteAutomatonState oldState : oldStates) {
             NondeterministicFiniteAutomatonState newState = m.get(oldState);
             
             for (NondeterministicFiniteAutomatonState epsilonOldState 
                 : oldState.getEpsilonStates()) {
                 
                 newState.addEpsilonTransition(m.get(epsilonOldState));
             }
             
             NondeterministicFiniteAutomatonState dotState = 
                 oldState.getDotTransition();
             
             if (dotState != null) {
                 newState.addDotTransition(m.get(dotState));
             }
             
             for (int i = 0; i < oldState.getTransitionCount(); ++i) {
                 TransitionFunctionEntry tfe = oldState.getTransition(i);
                 
                 for (NondeterministicFiniteAutomatonState goal
                     : tfe.getGoalStates()) {
                     
                     newState.addTransition(tfe.getCodePointRange(), 
                                            m.get(goal));
                 }
             }
        }
    }
    
    public NondeterministicFiniteAutomatonState createState() {
        NondeterministicFiniteAutomatonState state = 
            new NondeterministicFiniteAutomatonState(stateCounter++);
        
        this.states.add(state);
        return state;
    }
    
    public String convertToRegex() {
        NondeterministicFiniteAutomaton nfa =
            new NondeterministicFiniteAutomaton(this);
        
        Set<NondeterministicFiniteAutomatonState> states = 
            nfa.getAllReachableStates();
        
        NondeterministicFiniteAutomatonState gnfaStart  = nfa.createState();
        NondeterministicFiniteAutomatonState gnfaAccept = nfa.createState();
        
        Map<NondeterministicFiniteAutomatonState,
            Map<NondeterministicFiniteAutomatonState, String>> r = 
                new HashMap<>();
        
        for (NondeterministicFiniteAutomatonState q : nfa.states) {
            r.put(q, new HashMap<>());
        }
        
        putUnion(r, gnfaStart, nfa.initialState, "");
        
        for (NondeterministicFiniteAutomatonState acc : nfa.acceptingStates) {
            putUnion(r, acc, gnfaAccept, "");
        }
        
        for (NondeterministicFiniteAutomatonState q : states) {
            
            for (int i = 0; i < q.getTransitionCount(); ++i) {
                var e = q.getTransition(i);
                
                String label = rangeToRegex(e.getCodePointRange());
                
                for (NondeterministicFiniteAutomatonState z 
                    : e.getGoalStates()) {
                    
                    putUnion(r, q, z, label);
                }
            }
            
            NondeterministicFiniteAutomatonState dot = q.getDotTransition();
            
            if (dot != null) {
                putUnion(r, q, dot, ".");
            }
            
            for (NondeterministicFiniteAutomatonState eps 
                : q.getEpsilonStates()) {
                
                putUnion(r, q, eps, "");
            }
        }
        
        Set<NondeterministicFiniteAutomatonState> removable =
            new HashSet<>(nfa.states);
        
        removable.remove(gnfaStart);
        removable.remove(gnfaAccept);
        
        for (NondeterministicFiniteAutomatonState k : removable) {
            String rkk = get(r, k, k);
            
            for (NondeterministicFiniteAutomatonState i : nfa.states) {
                if (i == k) {
                    continue;
                }
                
                String rik = get(r, i, k);
                
                if (rik == null) {
                    continue;
                }
                
                for (NondeterministicFiniteAutomatonState j : nfa.states) {
                    if (j == k) {
                        continue;
                    }
                    
                    String rkj = get(r, k, j);
                    
                    if (rkj == null) {
                        continue;
                    }
                    
                    String old = get(r, i, j);
                    String neu = concat(rik, star(rkk), rkj);
                    
                    putUnion(r, i, j, neu);
                }
            }
            
            r.remove(k);
            
            for (Map<NondeterministicFiniteAutomatonState, String> row 
                : r.values()) {
                
                row.remove(k);
            }
            
            nfa.states.remove(k);
        }
            
        String regex = get(r, gnfaStart, gnfaAccept);
        
        if (regex == null) {
            return "";
        }
        
        if (anchoredAtStart) {
            regex = "^" + regex;
        }
        
        if (anchoredAtEnd) {
            regex = regex + "$";
        }
        
        return regex;
    }
    
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
        
        this.states.add(initialState);
    }
    
    public void addAcceptingState(
            NondeterministicFiniteAutomatonState acceptingState) {
        
        this.acceptingStates.add(acceptingState);
        this.states.add(acceptingState);
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
        convertToDeterministicFiniteAutomaton() {
        return new NFAToDFAConverter(this).convert();
    }
        
    public static NondeterministicFiniteAutomaton compile(String regex) {
        RegexTokenizationResult tokenization = 
            new RegexTokenizer().tokenize(regex);
        
        RegexParser parser = new RegexParser(tokenization.tokens());
        RegexNode abstractSyntaxTree = parser.parse();
        
        return new NondeterministicFiniteAutomatonCompiler(abstractSyntaxTree)
            .compile(tokenization);
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

    @Override
    public boolean find(String text) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        private DeterministicFiniteAutomaton dfa;

        NFAToDFAConverter(NondeterministicFiniteAutomaton nfa) {
            this.nfa = nfa;
        }
        
        DeterministicFiniteAutomaton convert() {
            
            dfa = new DeterministicFiniteAutomaton(anchoredAtStart,
                                                   anchoredAtEnd);
            
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
                    
                    if (intersects(e.getCodePointRange(), range)) {
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
                        state.getTransition(i).getCodePointRange();
                    
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
                new CodePointRange(codePoint),
                new DeterministicFiniteAutomatonState(startId++));
        }
        
        return transitionMap;
    }
        
    private static String 
        get(Map<NondeterministicFiniteAutomatonState,
                Map<NondeterministicFiniteAutomatonState, String>> r, 
            NondeterministicFiniteAutomatonState from,
            NondeterministicFiniteAutomatonState to) {
        
        Map<NondeterministicFiniteAutomatonState, String> row = r.get(from);
        
        if (row == null) {
            return null;
        }
        
        return row.get(to);
    }
        
    private static void putUnion(
        Map<NondeterministicFiniteAutomatonState,
            Map<NondeterministicFiniteAutomatonState, String>> r,
        NondeterministicFiniteAutomatonState from,
        NondeterministicFiniteAutomatonState to,
        String label) {
        
        String old = get(r, from, to);
        
        if (old == null) {
            r.get(from).put(to, label);
        } else if (!old.equals(label)) {
            r.get(from).put(to, union(old, label));
        }
    }
    
    private static String union(String a, String b) {
        if (a.equals(b)) {
            return a;
        }
        
        return "(" + a + "|" + b + ")";
    }
    
    private static String concat(String... parts) {
        StringBuilder sb = new StringBuilder();
        
        for (String p : parts) {
            if (p == null) {
                continue;
            }
            
            // Epsilon:
            if (p.isEmpty()) {
                continue;
            }
            
            sb.append(parenthesizeIfUnion(p));
        }
        
        return sb.toString();
    }
    
    private static String star(String r) {
        if (r == null || r.isEmpty()) {
            return "";
        }
        
        return parenthesizeIfNeeded(r) + "*";
    }
    
    private static String parenthesizeIfNeeded(String r) {
        if (r.length() == 1 || r.equals(".")) {
            return r;
        }
        
        if (r.startsWith("[") && r.endsWith("]")) {
            return r;
        }
        
        return "(" + r + ")";
    }
    
    private static String parenthesizeIfUnion(String r) {
        if (r.contains("|")) {
            return "(" + r + ")";
        }
        
        return r;
    }
    
    private static String rangeToRegex(CodePointRange range) {
        int min = range.getMinimumCodePoint();
        int max = range.getMaximumCodePoint();
        
        if (min == max) {
            return escapeCodePoint(min);
        }
        
        return "[" + escapeInCharClass(min) + "-" + 
                     escapeInCharClass(max) + "]";
    }
    
    private static String escapeCodePoint(int cp) {
        String s = new String(Character.toChars(cp));
        
        // TODO: Do I need ^ $ and friends?
        return switch (s) {
            case "\\", ".", "|", "*", "+", "?", "(", ")", "[", "]", "{", "}", 
                 "^", "$" -> "\\" + s;
                
            default -> s;
        };
    }
    
    private static String escapeInCharClass(int cp) {
        String s = new String(Character.toChars(cp));
        
        return switch (s) {
            case "\\", "]", "^", "-" -> "\\" + s;
            default -> s;
        };
    }
}