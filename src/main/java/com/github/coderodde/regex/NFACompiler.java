package com.github.coderodde.regex;

import static com.github.coderodde.regex.RegexTokenType.CHARACTER;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * This class provides a method for converting regular expressions in postfix
 * notation into NFAs.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 17, 2023)
 * @since 1.6 (Nov 17, 2023)
 */
public final class NFACompiler {
    
    private int stateCounter = 0;
    private final Deque<RegexToken> postfixRegex;
    private final Deque<NondeterministicFiniteAutomaton> nfaStack =
            new ArrayDeque<>();
    
    private NFACompiler(Deque<RegexToken> postfixRegex) {
        this.postfixRegex = postfixRegex;
    }
    
    public NFACompiler() {
        this.postfixRegex = null;
    }
    
    public NondeterministicFiniteAutomaton
        construct(Deque<RegexToken> postfixRegex) {
        return new NFACompiler(postfixRegex).compileImpl();
    }
         
    /**
     * This method compiles the input postfix regular expression into a NFA. 
     * This algorithm is known as 
     * <a href="https://en.wikipedia.org/wiki/Thompson%27s_construction">Thompson's construction</a>.
     * 
     * @return an {@code NondeterministicFiniteAutomaton} recognizing the same 
     *         language as the input postfix regex.
     */
    private NondeterministicFiniteAutomaton compileImpl() {
        while (true) {
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
                    
                case KLEENE_STAR:
                    processKleeneStar();
                    break;
                    
                default:
                    throw new IllegalArgumentException(
                            "Unknown regex token type: " + token);
            }
            
            if (postfixRegex.isEmpty()) {
                return nfaStack.getLast();
            }
        }
    }
        
    private void processCharacter(RegexToken token) {
        NondeterministicFiniteAutomaton nfa =
                new NondeterministicFiniteAutomaton();
        
        NondeterministicFiniteAutomatonState initialState = 
                new NondeterministicFiniteAutomatonState(getNextStateName());
        
        NondeterministicFiniteAutomatonState acceptingState = 
                new NondeterministicFiniteAutomatonState(getNextStateName());
        
        nfa.setInitialState(initialState);
        
        nfa.getAcceptingStateSet()
           .addNondeterministicFiniteAutomatonState(acceptingState);
        
        nfa.getTransitionFunction()
           .connect(initialState, 
                    acceptingState, 
                    token.getCharacter());
        
        nfaStack.addLast(nfa);
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
        
        NondeterministicFiniteAutomatonState resultNFAInitialState = 
                nfa1.getInititalState();
        
        NondeterministicFiniteAutomatonState resultNFAAcceptingState = 
                nfa2.getAcceptingStateSet()
                    .getStates()
                    .iterator()
                    .next();
        
        resultNFA.setInitialState(resultNFAInitialState);
        resultNFA.getAcceptingStateSet()
                 .addNondeterministicFiniteAutomatonState(
                         resultNFAAcceptingState);
        
        NondeterministicFiniteAutomatonState nfa1AcceptingState = 
                nfa1.getAcceptingStateSet()
                    .getStates()
                    .iterator()
                    .next();
        
        NondeterministicFiniteAutomatonState nfa2InitialState = 
                nfa2.getInititalState();
        
        resultNFA.getTransitionFunction()
                 .addEpsilonConnection(nfa1AcceptingState, 
                                       nfa2InitialState);
        
        nfaStack.addLast(resultNFA);
    }
    
    private void processKleeneStar() {
        NondeterministicFiniteAutomatonState resultNFAInitialState = 
                new NondeterministicFiniteAutomatonState(getNextStateName());
        
        NondeterministicFiniteAutomatonState resultNFAAcceptingState = 
                new NondeterministicFiniteAutomatonState(getNextStateName());
        
        NondeterministicFiniteAutomaton automaton = nfaStack.removeLast();
        
        NondeterministicFiniteAutomatonState automatonInitialState = 
                automaton.getInititalState();
        
        NondeterministicFiniteAutomatonState automatonAcceptingState = 
                automaton.getAcceptingStateSet().getStates().iterator().next();
        
        automaton.getAcceptingStateSet().clear();
        
        NondeterministicFiniteAutomaton resultNFA = 
                new NondeterministicFiniteAutomaton();
        
        resultNFA.setInitialState(resultNFAInitialState);
        resultNFA.getAcceptingStateSet()
                 .addNondeterministicFiniteAutomatonState(resultNFAAcceptingState);
        
        resultNFA.getTransitionFunction()
                 .addEpsilonConnection(resultNFAInitialState,
                                       automatonInitialState);
        
        resultNFA.getTransitionFunction()
                 .addEpsilonConnection(automatonAcceptingState, 
                                       resultNFAAcceptingState);
        
        resultNFA.getTransitionFunction()
                 .addEpsilonConnection(automatonAcceptingState, 
                                       automatonInitialState);
        
        nfaStack.addLast(resultNFA);
    }
    
    private int getNextStateName() {
        return stateCounter++;
    }
}
