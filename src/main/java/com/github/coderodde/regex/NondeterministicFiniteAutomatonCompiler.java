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
public final class NondeterministicFiniteAutomatonCompiler {
    
    private int stateIDCounter = 0;
    private final Deque<RegexToken> postfixRegex;
    private final Deque<NondeterministicFiniteAutomaton> nfaStack =
            new ArrayDeque<>();
    
    private NondeterministicFiniteAutomatonCompiler(
            Deque<RegexToken> postfixRegex) {
        this.postfixRegex = postfixRegex;
    }
    
    public NondeterministicFiniteAutomatonCompiler() {
        this.postfixRegex = null;
    }
    
    public NondeterministicFiniteAutomaton
        compile(Deque<RegexToken> postfixRegex) {
        return new NondeterministicFiniteAutomatonCompiler(postfixRegex)
                .compileImpl();
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
                    
                case CONCAT: 
                    processConcatenationOperator();
                    break;
                    
                case DOT:
                    processPeriodWildcard();
                    break;
                    
                case UNION: 
                    processUnionOperator();
                    break;
                    
                case QUESTION:
                    processQuestionMarkOperator();
                    break;
                    
                case KLEENE_STAR: 
                    processKleeneStar();
                    break;
                    
                case PLUS:
                    processPlusOperator();
                    break;
                    
                case EPSILON:
                    processEpsilonTransition();
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
                new NondeterministicFiniteAutomatonState(
                        getNextStateId());
        
        NondeterministicFiniteAutomatonState acceptingState = 
                new NondeterministicFiniteAutomatonState(
                        getNextStateId());
        
        nfa.setInitialState(initialState);
        nfa.setAcceptingState(acceptingState);
        initialState.addTransition(token.getCharacter(), acceptingState);
        
        nfaStack.addLast(nfa);
    }
    
    private void processPeriodWildcard() {
        NondeterministicFiniteAutomaton nfa =
                new NondeterministicFiniteAutomaton();
        
        NondeterministicFiniteAutomatonState initialState = 
                new NondeterministicFiniteAutomatonState(
                        getNextStateId());
        
        NondeterministicFiniteAutomatonState acceptingState = 
                new NondeterministicFiniteAutomatonState(
                        getNextStateId());
        
        nfa.setInitialState(initialState);
        nfa.setAcceptingState(acceptingState);
        initialState.addDotTransition(acceptingState);
        
        nfaStack.addLast(nfa);
    }
    
    private void processQuestionMarkOperator() {
        if (nfaStack.isEmpty()) {
            throw new InvalidRegexException();
        }
        
        NondeterministicFiniteAutomaton nfa = nfaStack.removeLast();
        NondeterministicFiniteAutomaton resultNFA = 
                new NondeterministicFiniteAutomaton();
        
        NondeterministicFiniteAutomatonState initialState =
                new NondeterministicFiniteAutomatonState(getNextStateId());
        
        NondeterministicFiniteAutomatonState acceptingState = 
                new NondeterministicFiniteAutomatonState(getNextStateId());
        
        resultNFA.setInitialState(initialState);
        resultNFA.setAcceptingState(acceptingState);
        
        initialState.addEpsilonTransition(acceptingState);
        initialState.addEpsilonTransition(nfa.getInitialState());
        nfa.getAcceptingState().addEpsilonTransition(acceptingState);
        nfa.setAcceptingState(null);
        
        nfaStack.addLast(resultNFA);
    }
    
    private void processUnionOperator() {
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
                new NondeterministicFiniteAutomatonState(
                        getNextStateId());
        
        NondeterministicFiniteAutomatonState acceptingState =
                new NondeterministicFiniteAutomatonState(
                        getNextStateId());
        
        resultNFA.setInitialState(initialState);
        resultNFA.setAcceptingState(acceptingState);
        
        initialState.addEpsilonTransition(nfa1.getInitialState());
        initialState.addEpsilonTransition(nfa2.getInitialState());
        
        nfa1.getAcceptingState().addEpsilonTransition(acceptingState);
        nfa2.getAcceptingState().addEpsilonTransition(acceptingState);
        
        nfa1.setAcceptingState(null);
        nfa2.setAcceptingState(null);
        
        nfaStack.addLast(resultNFA);
    }
    
    private void processConcatenationOperator() {
        if (nfaStack.isEmpty()) {
            throw new InvalidRegexException();
        }
        
        NondeterministicFiniteAutomaton nfa2 = nfaStack.removeLast();
        
        if (nfaStack.isEmpty()) {
            throw new InvalidRegexException();
        }
        
        NondeterministicFiniteAutomaton nfa1 = nfaStack.removeLast();
        NondeterministicFiniteAutomaton resultNFA =
                new NondeterministicFiniteAutomaton();
        
        resultNFA.setInitialState(nfa1.getInitialState());
        resultNFA.setAcceptingState(nfa2.getAcceptingState());
        
        nfa1.getAcceptingState().addEpsilonTransition(nfa2.getInitialState());
        nfa1.setAcceptingState(null);
        
        nfaStack.addLast(resultNFA);
    }
    
    private void processEpsilonTransition() {
        if (nfaStack.isEmpty()) {
            throw new InvalidRegexException();
        }
        
        NondeterministicFiniteAutomaton nfa2 = nfaStack.removeLast();
        
        if (nfaStack.isEmpty()) {
            throw new InvalidRegexException();
        }
        
        NondeterministicFiniteAutomaton nfa1 = nfaStack.removeLast();
        NondeterministicFiniteAutomaton resultNFA = 
                new NondeterministicFiniteAutomaton();
        
        resultNFA.setInitialState(nfa1.getInitialState());
        resultNFA.setAcceptingState(nfa2.getAcceptingState());
        
        nfa1.getAcceptingState().addEpsilonTransition(nfa2.getInitialState());
        nfa1.setAcceptingState(null);
        
        nfaStack.addLast(resultNFA);
    }
    
    private void processKleeneStar() {
        if (nfaStack.isEmpty()) {
            throw new InvalidRegexException();
        }
        
        NondeterministicFiniteAutomatonState initialState = 
                new NondeterministicFiniteAutomatonState(getNextStateId());
        
        NondeterministicFiniteAutomatonState acceptingState = 
                new NondeterministicFiniteAutomatonState(getNextStateId());
        
        initialState.addEpsilonTransition(acceptingState);
        
        NondeterministicFiniteAutomaton nfa = nfaStack.removeLast();
        
        initialState.addEpsilonTransition(nfa.getInitialState());
        nfa.getAcceptingState().addEpsilonTransition(acceptingState);
        
        nfa.getAcceptingState().addEpsilonTransition(nfa.getInitialState());
        nfa.setAcceptingState(null);
        
        NondeterministicFiniteAutomaton resultNFA =
                new NondeterministicFiniteAutomaton();
        
        resultNFA.setInitialState(initialState);
        resultNFA.setAcceptingState(acceptingState);
        
        nfaStack.addLast(resultNFA);
    }
    
    private void processPlusOperator() {
        if (nfaStack.isEmpty()) {
            throw new InvalidRegexException();
        }
        
        NondeterministicFiniteAutomatonState initialState = 
                new NondeterministicFiniteAutomatonState(getNextStateId());
        
        NondeterministicFiniteAutomatonState acceptingState = 
                new NondeterministicFiniteAutomatonState(getNextStateId());
        
        NondeterministicFiniteAutomaton nfa = nfaStack.removeLast();
        
        initialState.addEpsilonTransition(nfa.getInitialState());
        nfa.getAcceptingState().addEpsilonTransition(acceptingState);
        
        nfa.getAcceptingState().addEpsilonTransition(nfa.getInitialState());
        nfa.setAcceptingState(null);
        
        NondeterministicFiniteAutomaton resultNFA =
                new NondeterministicFiniteAutomaton();
        
        resultNFA.setInitialState(initialState);
        resultNFA.setAcceptingState(acceptingState);
        
        nfaStack.addLast(resultNFA); 
    }
    
    private int getNextStateId() {
        return stateIDCounter++;
    }
}
