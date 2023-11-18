package com.github.coderodde.regex;

import static com.github.coderodde.regex.RegexTokenType.CHARACTER;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 17, 2023)
 * @since 1.6 (Nov 17, 2023)
 */
public final class NFAConstructor {
    
    private Deque<RegexToken> postfixRegex;
    private int stateCounter = 0;
    private final Deque<NondeterministicFiniteAutomaton> nfaStack =
            new ArrayDeque<>();
    
    public NondeterministicFiniteAutomaton
         construct(Deque<RegexToken> postfixRegex) {
        NFAConstructor nfaConstructor = new NFAConstructor();
        nfaConstructor.postfixRegex = postfixRegex;
        return nfaConstructor.constructImpl(); 
    }
         
    private NondeterministicFiniteAutomaton constructImpl() {
        while (!postfixRegex.isEmpty()) {
            RegexToken token = postfixRegex.removeFirst();
            
            switch (token.getTokenType()) {
                case CHARACTER:
                    processCharacter(token);
                    break;
                    
                case CONCATENATION:
                    processConcatenation();
                    break;
                    
                case UNION:
                    processUnion();
                    break;
            }
        }
        
        return nfaStack.getLast();
    }
        
    private void processCharacter(RegexToken token) {
        NondeterministicFiniteAutomaton nfa =
                new NondeterministicFiniteAutomaton();
        
        NondeterministicFiniteAutomatonState initialState = 
                new NondeterministicFiniteAutomatonState(getNextStateName());
        
        
        NondeterministicFiniteAutomatonState acceptingState = 
                new NondeterministicFiniteAutomatonState(getNextStateName());
        
        nfa.setInitialState(initialState);
        
        nfa.getStateSet()
           .addNondeterministicFiniteAutomatonState(acceptingState);
        
        nfa.getAcceptingStateSet()
           .addNondeterministicFiniteAutomatonState(acceptingState);
        
        nfa.getTransitionFunction()
           .connect(initialState, 
                    acceptingState, 
                    token.getCharacter());
    }
    
    private void processUnion() {
        if (nfaStack.isEmpty()) {
            throw new InvalidRegexException();
        }
        
        NondeterministicFiniteAutomaton nfa1 = nfaStack.removeLast();
        
        if (nfaStack.isEmpty()) {
            throw new InvalidRegexException();
        }
        
        NondeterministicFiniteAutomaton nfa2 = nfaStack.removeLast();
        NondeterministicFiniteAutomaton resultNFA =
                new NondeterministicFiniteAutomaton();
        
        NondeterministicFiniteAutomatonState initialState = 
                new NondeterministicFiniteAutomatonState(getNextStateName());
        
        NondeterministicFiniteAutomatonState acceptingState =
                new NondeterministicFiniteAutomatonState(getNextStateName());
        
        resultNFA.setInitialState(initialState);
        resultNFA.getAcceptingStateSet()
                 .addNondeterministicFiniteAutomatonState(acceptingState);
        
        resultNFA.getStateSet()
                 .addNondeterministicFiniteAutomatonState(acceptingState);
        
        resultNFA.getTransitionFunction()
                 .addEpsilonConnection(initialState, 
                                       nfa1.getInititalState());
        resultNFA.getTransitionFunction()
                 .addEpsilonConnection(initialState, 
                                       nfa2.getInititalState());
        
        // TODO: remove for?
        for (NondeterministicFiniteAutomatonState as : 
                nfa1.getAcceptingStateSet().getStates()) {
            
            resultNFA.getTransitionFunction()
                     .addEpsilonConnection(as, acceptingState);
        }
        
        for (NondeterministicFiniteAutomatonState as :
                nfa2.getAcceptingStateSet().getStates()) {
            
            resultNFA.getTransitionFunction()
                     .addEpsilonConnection(as, acceptingState);
        }
        
        nfaStack.addLast(resultNFA);
    }
    
    private void processConcatenation() {
        if (nfaStack.isEmpty()) {
            throw new InvalidRegexException();
        }
        
        NondeterministicFiniteAutomaton nfa1 = nfaStack.removeLast();
        
        if (nfaStack.isEmpty()) {
            throw new InvalidRegexException();
        }
        
        NondeterministicFiniteAutomaton nfa2 = nfaStack.removeLast();
        NondeterministicFiniteAutomaton resultNFA =
                new NondeterministicFiniteAutomaton();
        
        resultNFA.setInitialState(nfa1.getInititalState());
        resultNFA.getAcceptingStateSet()
                 .addNondeterministicFiniteAutomatonState(
                         nfa2.getAcceptingStateSet()
                             .getStates()
                             .iterator()
                             .next());
        
        NondeterministicFiniteAutomatonState nfa1AcceptingState = 
                nfa1.getAcceptingStateSet()
                    .getStates()
                    .iterator()
                    .next();
        
        nfa2.setInitialState(nfa1AcceptingState);
        
        nfa1.getTransitionFunction().connect(nfa1AcceptingState, nfa1.getInititalState(), Character.MIN_VALUE);
        
    }
    
    private String getNextStateName() {
        return new StringBuilder()
                .append(stateCounter++)
                .toString();
    }
}
