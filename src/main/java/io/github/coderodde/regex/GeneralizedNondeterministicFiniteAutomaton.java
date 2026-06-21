package io.github.coderodde.regex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class implements GNFA (Generalized Nondeterministic Finite Automaton) 
 * for converting DFAs into their respective regular languages.
 */
public final class GeneralizedNondeterministicFiniteAutomaton { // TODO: remove public?
    
    private final GeneralizedNondeterministicFiniteAutomatonState initialState;
    private final GeneralizedNondeterministicFiniteAutomatonState 
        acceptingState;
    
    private final Set<GeneralizedNondeterministicFiniteAutomatonState> 
        stateSet = new HashSet<>();
    
    private int idCounter;
    
    public GeneralizedNondeterministicFiniteAutomaton(
            DeterministicFiniteAutomaton dfa) {
        
        Map<DeterministicFiniteAutomatonState,
            GeneralizedNondeterministicFiniteAutomatonState> m = 
            new HashMap<>();
        
        // Create/copy states:
        for (DeterministicFiniteAutomatonState q : dfa.getAllStates()) {
            m.put(q, createState());
        }
        
        initialState   = createState();
        acceptingState = createState();
        
        initialState.addEpsilonTransition(m.get(dfa.getInitialState()));
        initialState.setRegularExpression(m.get(dfa.getInitialState()), null);
        
        for (DeterministicFiniteAutomatonState q : dfa.getAcceptingStates()) {
            m.get(q).addEpsilonTransition(acceptingState);
            m.get(q).setRegularExpression(acceptingState, null);
        }
        
        // Copy state transitions:
        for (DeterministicFiniteAutomatonState q : dfa.getAllStates()) {
            GeneralizedNondeterministicFiniteAutomatonState qq = 
                 m.get(q);
            
            DeterministicFiniteAutomatonStateTransitionFunction tf = 
                q.getTransitionFunction();

            CharacterClassString ccs =null; 
//                new CharacterClassString(
//                    tfe.getCharacterRange().isNegated());
            
            for (int i = 0; i < tf.size(); ++i) {
                DeterministicFiniteAutomatonStateTransitionFunction
                    .TransitionFunctionEntry tfe = tf.get(i);
                
                DeterministicFiniteAutomatonState next = tfe.getGoalState();
                GeneralizedNondeterministicFiniteAutomatonState nextGnfa = 
                    m.get(next);
                
                
//                CharacterClassString ccs = new CharacterClassString(false, tfe.getCharacterRange());
                
                CodePointRange cpr = tfe.getCharacterRange();
                qq.setRegularExpression(nextGnfa, codePointRangeToRegex(cpr));
            }
        }
    }
    
    private static String codePointRangeToRegex(CodePointRange cpr) {
        if (cpr.isSingleCodePoint()) {
            return new String(Character.toChars(cpr.getMaximumCodePoint()));
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append(
                new String(Character.toChars(cpr.getMinimumCodePoint())));
            sb.append("-");
            sb.append(
                new String(Character.toChars(cpr.getMaximumCodePoint())));
            sb.append("]");
            return sb.toString();
        }
    }
    
    GeneralizedNondeterministicFiniteAutomatonState createState() {
        GeneralizedNondeterministicFiniteAutomatonState q = 
            new GeneralizedNondeterministicFiniteAutomatonState(idCounter++);
        
        stateSet.add(q);
        return q;
    }
    
    int getNumberOfStates() {
        return stateSet.size();
    }
    
    void rip() {
        GeneralizedNondeterministicFiniteAutomatonState rippedState = 
            getStateToRip();
        
        Set<GeneralizedNondeterministicFiniteAutomatonState> incomingStates = 
            rippedState.getIncomingStates();
        
        Set<GeneralizedNondeterministicFiniteAutomatonState> outgoingStates = 
            rippedState.getOutgoingStates();
        
        for (GeneralizedNondeterministicFiniteAutomatonState in
            : incomingStates) {
            
            if (in.equals(rippedState) || in.equals(initialState)) {
                continue;
            }
            
            for (GeneralizedNondeterministicFiniteAutomatonState out
                : outgoingStates) {
                
                if (out.equals(rippedState) || out.equals(acceptingState)) {
                    continue;
                }
                
                ripImpl(in, rippedState, out);
            }
        }
        
        rippedState.clearTransitions();
        stateSet.remove(rippedState);
    }
    
    private void disconnectFrom(
        GeneralizedNondeterministicFiniteAutomatonState head,
        GeneralizedNondeterministicFiniteAutomatonState tail) {
        
        head.getOutgoingStates().remove(tail);
    }
    
    private void ripImpl(GeneralizedNondeterministicFiniteAutomatonState in,
                         GeneralizedNondeterministicFiniteAutomatonState ripped,
                         GeneralizedNondeterministicFiniteAutomatonState out) {
        if (!in.equals(out)) {
            if (!in.getOutgoingStates().contains(out)) {
                in.setRegularExpression(out, "");
            } else {
                disconnectFrom(in, out);
            }
        }
        
        String rir = in.getRegularExpression(ripped);
        String rrr = ripped.getRegularExpression(ripped);
        String rrj = ripped.getRegularExpression(out);
        
        rir = addParenthesesIfNeeded(rir);
        rrr = addParenthesesIfNeeded(rrr);
        rrj = addParenthesesIfNeeded(rrj);
        
        in.setRegularExpression(out, rir + rrr + "*" + rrj);
    }
    
    private String addParenthesesIfNeeded(String regex) {
        return regex.length() > 1 ? ("(" + regex + ")") : regex;
    }
        
    public String toRegularExpression() {
        while (stateSet.size() > 2) {
            rip();
        }
        
        return initialState.getRegularExpression(acceptingState);
    }
    
    private GeneralizedNondeterministicFiniteAutomatonState getStateToRip() {
        for (GeneralizedNondeterministicFiniteAutomatonState q : stateSet) {
            if (!q.equals(initialState) && !q.equals(acceptingState)) {
                return q;
            }
        }
        
        throw new IllegalStateException("No state to rip.");
    }
    
    static final class CharacterClassString {
        private final boolean negated;
        private final List<CodePointRange> ranges = new ArrayList<>();
        
        CharacterClassString(boolean negated) {
            this.negated = negated;
        }
        
        void addCodePointRange(CodePointRange range) {
            ranges.add(range);
        }
        
        @Override
        public String toString() {
            if (ranges.size() == 1 && ranges.get(0).isSingleCodePoint()) {
                return new String(Character.toChars(
                    ranges.get(0).getMaximumCodePoint()));
            }
            
            StringBuilder sb = new StringBuilder();
            
            ranges.sort((CodePointRange o1, 
                         CodePointRange o2) -> 
                            Integer.compare(o1.getMinimumCodePoint(),
                                            o2.getMinimumCodePoint()));
            
            sb.append("[");
            
            if (negated) {
                sb.append("^");
            }
            
            for (CodePointRange range : ranges) {
                sb.append(range);
            }
            
            return sb.append("]").toString();
        }
    }
}
