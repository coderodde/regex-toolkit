package com.github.coderodde.regex;

import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

public class NondeterministicFiniteAutomatonTest {
    
    private static final NondeterministicFiniteAutomaton nfa = 
            new NondeterministicFiniteAutomaton();
    
    @BeforeClass
    public static void beforeClass() {
        NondeterministicFiniteAutomatonState q0 = 
                new NondeterministicFiniteAutomatonState("0");
        
        NondeterministicFiniteAutomatonState q1 = 
                new NondeterministicFiniteAutomatonState("1");
        
        NondeterministicFiniteAutomatonState q2 = 
                new NondeterministicFiniteAutomatonState("2");
        
        NondeterministicFiniteAutomatonState q3 = 
                new NondeterministicFiniteAutomatonState("3");
        
        nfa.setInitialState(q0);
        
        nfa.getStateSet().addNondeterministicFiniteAutomatonState(q1);
        nfa.getStateSet().addNondeterministicFiniteAutomatonState(q2);
        nfa.getStateSet().addNondeterministicFiniteAutomatonState(q3);
        
        nfa.getAcceptingStateSet().addNondeterministicFiniteAutomatonState(q3);
        
        NondeterministicFiniteAutomatonTransitionFunction f = 
                nfa.getTransitionFunction();
        
        f.connect(q0, q0, 'a');
        f.connect(q0, q0, 'b');
        f.connect(q0, q1, 'a'); 
        f.connect(q1, q2, 'a');
        f.connect(q2, q3, 'b');
        f.connect(q3, q2, 'a');
        
        f.addEpsilonConnection(q1, q2);
    }
    
    @Test
    public void acceptsStrings() {
        assertTrue(nfa.matches("abbab"));
    }
}
