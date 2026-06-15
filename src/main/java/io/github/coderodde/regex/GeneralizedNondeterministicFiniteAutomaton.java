package io.github.coderodde.regex;

import java.util.HashMap;
import java.util.HashSet;
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
        
        for (DeterministicFiniteAutomatonState q : dfa.getAcceptingStates()) {
            m.get(q).addEpsilonTransition(acceptingState);
        }
        
        // Copy state transitions:
        for (DeterministicFiniteAutomatonState q : dfa.getAllStates()) {
            GeneralizedNondeterministicFiniteAutomatonState qq = 
                 m.get(q);
            
            DeterministicFiniteAutomatonStateTransitionFunction tf = 
                q.getTransitionFunction();
            
            for (int i = 0; i < tf.size(); ++i) {
                DeterministicFiniteAutomatonStateTransitionFunction
                    .TransitionFunctionEntry tfe = tf.get(i);
                
                DeterministicFiniteAutomatonState next = tfe.getGoalState();
                GeneralizedNondeterministicFiniteAutomatonState nextGnfa = 
                    m.get(next);
                
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
            new HashSet<>(rippedState.getIncomingStates());
        
        Set<GeneralizedNondeterministicFiniteAutomatonState> outgoingStates = 
            new HashSet<>(rippedState.getOutgoingStates());
        
        for (GeneralizedNondeterministicFiniteAutomatonState incomingState 
            : incomingStates) {
            
            if (incomingState.equals(rippedState)) {
                continue;
            }
            
            for (GeneralizedNondeterministicFiniteAutomatonState outgoingState 
                : outgoingStates) {
                
                if (outgoingState.equals(rippedState)) {
                    continue;
                }
                
                ripImpl(incomingState, rippedState, outgoingState);
            }
        }
        
        for (GeneralizedNondeterministicFiniteAutomatonState q 
            : new HashSet<>(stateSet)) {
            
            q.removeRegularExpression(rippedState);
        }
        
        rippedState.clearTransitions();
        stateSet.remove(rippedState);
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
    
    private void ripImpl(
            GeneralizedNondeterministicFiniteAutomatonState incomingState,
            GeneralizedNondeterministicFiniteAutomatonState rippedState,
            GeneralizedNondeterministicFiniteAutomatonState outgoingState) {
        
        String rij = incomingState.getRegularExpression(outgoingState);
        String rir = incomingState.getRegularExpression(rippedState);
        String rrr = rippedState.getRegularExpression(rippedState);
        String rrj = rippedState.getRegularExpression(outgoingState);
        
        StringBuilder sb = new StringBuilder();
        
        if (rij != null && !rij.isEmpty()) {
            sb.append("(").append(rij).append(")|");
        }
        
        sb.append("(").append(rir).append(")");
        
        if (rrr != null && !rrr.isEmpty()) {
            sb.append("(").append(rrr).append(")*");
        }
        
        sb.append("(").append(rrj).append(")");
        
        incomingState.setRegularExpression(outgoingState, sb.toString());
        
        for (GeneralizedNondeterministicFiniteAutomatonState q : stateSet) {
            q.removeRegularExpression(rippedState);
        }
        
        rippedState.clearTransitions();
        stateSet.remove(rippedState);
    }
}
