//package com.github.coderodde.regex;
//
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * This class implements sets for storing the accepting states of a DFA.
// * 
// * @author Rodion "rodde" Efremov
// * @version 1.6 (Nov 11, 2023)
// * @since 1.6 (Nov 11, 2023)
// */
//public class DeterministicFiniteAutomatonAcceptingStateSet {
//    
//    private final Set<DeterministicFiniteAutomatonState> acceptingStateSet = 
//            new HashSet<>();
//    
//    public void addDeterministicFiniteAutomatonState(
//            DeterministicFiniteAutomatonState state) {
//        acceptingStateSet.add(state);
//    }
//    
//    public Set<DeterministicFiniteAutomatonState> getAcceptingStateSet() {
//        return Collections.<DeterministicFiniteAutomatonState>
//                unmodifiableSet(acceptingStateSet);
//    }
//}
