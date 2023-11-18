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
    
    private NFAConstructor(Deque<RegexToken> postfixRegex) {
        this.postfixRegex = postfixRegex;
    }
    
    public NondeterministicFiniteAutomaton
        construct(Deque<RegexToken> postfixRegex) {
        return new NFAConstructor(postfixRegex).constructImpl();
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
        
        
        
        nfaStack.addLast(resultNFA);
    }
    
    private String getNextStateName() {
        return new StringBuilder()
                .append(stateCounter++)
                .toString();
    }
}
