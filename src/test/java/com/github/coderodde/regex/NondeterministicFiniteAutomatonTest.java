package com.github.coderodde.regex;

import com.github.coderodde.regex.DeterministicFiniteAutomatonStateTransitionMap.TransitionMapEntry;
import static com.github.coderodde.regex.NondeterministicFiniteAutomaton.epsilonExpand;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class NondeterministicFiniteAutomatonTest {
    
    @Test
    public void epsilonExpansion() {
        NondeterministicFiniteAutomaton nfa =
                new NondeterministicFiniteAutomaton();
        
        NondeterministicFiniteAutomatonState q0 = 
                new NondeterministicFiniteAutomatonState(0);
       
        NondeterministicFiniteAutomatonState q1 = 
                new NondeterministicFiniteAutomatonState(1);
        
        NondeterministicFiniteAutomatonState q2 = 
                new NondeterministicFiniteAutomatonState(2);
        
        nfa.setInitialState(q0);
        
        q0.addTransition('a', q1);
        q0.addEpsilonTransition(q1);
        q1.addEpsilonTransition(q2);
        q2.addEpsilonTransition(q0);
        
        Set<NondeterministicFiniteAutomatonState> startState =
                new HashSet<>(Arrays.asList(q0));
        
        Set<NondeterministicFiniteAutomatonState> epsilonExpandedSet = 
                epsilonExpand(startState);
        
        assertEquals(3, epsilonExpandedSet.size());
        
        assertTrue(epsilonExpandedSet.contains(q0));
        assertTrue(epsilonExpandedSet.contains(q1));
        assertTrue(epsilonExpandedSet.contains(q2));
    }
    
    @Test
    public void match() {
        NondeterministicFiniteAutomaton nfa = 
                new NondeterministicFiniteAutomaton();
        
        NondeterministicFiniteAutomatonState q0 = 
                new NondeterministicFiniteAutomatonState(0);
        
        NondeterministicFiniteAutomatonState q1 = 
                new NondeterministicFiniteAutomatonState(1);
        
        NondeterministicFiniteAutomatonState q2 = 
                new NondeterministicFiniteAutomatonState(2);
        
        NondeterministicFiniteAutomatonState q3 = 
                new NondeterministicFiniteAutomatonState(3);
        
        nfa.setInitialState(q0);
        nfa.setAcceptingState(q3);
        
        q0.addTransition('a', q0);
        q0.addTransition('b', q0);
        q0.addTransition('b', q1);
        q1.addTransition('a', q2);
        q2.addTransition('b', q3);
        q3.addTransition('a', q2);
        
        q1.addEpsilonTransition(q2);
        
        assertTrue(nfa.matches("bb"));
        assertTrue(nfa.matches("abbab"));
        assertTrue(nfa.matches("abab"));
        
        assertFalse(nfa.matches("ba"));
        assertFalse(nfa.matches("ababa"));
    }
    
    @Test
    public void convertToDFA1() {
        NondeterministicFiniteAutomaton nfa =
                new NondeterministicFiniteAutomaton();
        
        NondeterministicFiniteAutomatonState a = 
                new NondeterministicFiniteAutomatonState(0);
        
        NondeterministicFiniteAutomatonState b = 
                new NondeterministicFiniteAutomatonState(1);
        
        NondeterministicFiniteAutomatonState c = 
                new NondeterministicFiniteAutomatonState(2);
        
        NondeterministicFiniteAutomatonState d = 
                new NondeterministicFiniteAutomatonState(3);
        
        nfa.setInitialState(a);
        nfa.setAcceptingState(d);
        
        a.addTransition('0', a);
        a.addTransition('0', b);
        a.addEpsilonTransition(c);
        b.addTransition('1', c);
        c.addTransition('0', c);
        c.addTransition('1', d);
        c.addEpsilonTransition(d);
        
        assertTrue(nfa.matches(""));
        assertTrue(nfa.matches("1"));
        assertTrue(nfa.matches("01"));
        assertTrue(nfa.matches("011"));
        assertTrue(nfa.matches("000"));
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches(""));
        assertTrue(dfa.matches("1"));
        assertTrue(dfa.matches("01"));
        assertTrue(dfa.matches("011"));
        assertTrue(dfa.matches("000"));
    }
    
    @Test
    public void convertToDFA2() {
        NondeterministicFiniteAutomaton nfa = 
                new NondeterministicFiniteAutomaton();
        
        NondeterministicFiniteAutomatonState state1 = 
                new NondeterministicFiniteAutomatonState(0);
        
        NondeterministicFiniteAutomatonState state2 = 
                new NondeterministicFiniteAutomatonState(1);
        
        nfa.setInitialState(state1);
        nfa.setAcceptingState(state2);
        
        state1.addTransition('1', state2);
        state1.addEpsilonTransition(state2);
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(nfa.matches(""));
        assertTrue(nfa.matches("1"));
        assertFalse(nfa.matches("0"));
        
        assertTrue(dfa.matches(""));
        assertTrue(dfa.matches("1"));
        assertFalse(dfa.matches("0"));
    }
    
    @Test
    public void convertSingleCharRegex() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("a");
        
        assertTrue(nfa.matches("a"));
        assertFalse(nfa.matches(""));
        assertFalse(nfa.matches("aa"));
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches("a"));
        assertFalse(dfa.matches(""));
        assertFalse(dfa.matches("aa"));
    }
    
    @Test
    public void convertTwoCharRegex() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("ab");
        
        assertTrue(nfa.matches("ab"));
        
        assertFalse(nfa.matches(""));
        assertFalse(nfa.matches("a"));
        assertFalse(nfa.matches("b"));
        assertFalse(nfa.matches("aa"));
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches("ab"));
        
        assertFalse(dfa.matches(""));
        assertFalse(dfa.matches("a"));
        assertFalse(dfa.matches("b"));
        assertFalse(dfa.matches("aa"));
    }
    
    @Test
    public void convertUnion() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("a|b");
        
        assertTrue(nfa.matches("a"));
        assertTrue(nfa.matches("b"));
        
        assertFalse(nfa.matches(""));
        assertFalse(nfa.matches("ab"));
        assertFalse(nfa.matches("ba"));
        assertFalse(nfa.matches("1"));
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches("a"));
        assertTrue(dfa.matches("b"));
        
        assertFalse(dfa.matches(""));
        assertFalse(dfa.matches("ab"));
        assertFalse(dfa.matches("ba"));
        assertFalse(dfa.matches("1"));
    }
    
    @Test
    public void convertToDFA3() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("(ab|c)*");
        
        assertTrue(nfa.matches(""));
        assertTrue(nfa.matches("c"));
        assertTrue(nfa.matches("ab"));
        assertTrue(nfa.matches("abab"));
        assertTrue(nfa.matches("abc"));
        assertTrue(nfa.matches("abcc"));
        assertTrue(nfa.matches("cc"));
        assertTrue(nfa.matches("ccc"));
        assertTrue(nfa.matches("ccab"));
        
        assertFalse(nfa.matches("a"));
        assertFalse(nfa.matches("b"));
        assertFalse(nfa.matches("ba"));
        assertFalse(nfa.matches("baab"));
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches(""));
        assertTrue(dfa.matches("c"));
        assertTrue(dfa.matches("ab"));
        assertTrue(dfa.matches("abab"));
        assertTrue(dfa.matches("abc"));
        assertTrue(dfa.matches("abcc"));
        assertTrue(dfa.matches("cc"));
        assertTrue(dfa.matches("ccc"));
        assertTrue(dfa.matches("ccab"));
        
        assertFalse(dfa.matches("a"));
        assertFalse(dfa.matches("b"));
        assertFalse(dfa.matches("ba"));
        assertFalse(dfa.matches("baab"));
    }
    
    @Test
    public void convertToDFA4() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("(a|b)*");
        
        assertTrue(nfa.matches(""));
        assertTrue(nfa.matches("a"));
        assertTrue(nfa.matches("b"));
        assertTrue(nfa.matches("ab"));
        assertTrue(nfa.matches("ba"));
        assertTrue(nfa.matches("aba"));
        assertTrue(nfa.matches("abb"));
        assertTrue(nfa.matches("abb"));
        
        assertFalse(nfa.matches("1"));
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches(""));
        assertTrue(dfa.matches("a"));
        assertTrue(dfa.matches("b"));
        assertTrue(dfa.matches("ab"));
        assertTrue(dfa.matches("ba"));
        assertTrue(dfa.matches("aba"));
        assertTrue(dfa.matches("abb"));
        assertTrue(dfa.matches("abb"));
        
        assertFalse(dfa.matches("1"));
    }
    
    @Test
    public void convertToDFA5() {
        NondeterministicFiniteAutomaton nfa =
                NondeterministicFiniteAutomaton.compile("a*");
        
        assertTrue(nfa.matches(""));
        assertTrue(nfa.matches("a"));
        assertTrue(nfa.matches("aa"));
        
        assertFalse(nfa.matches("b"));
        assertFalse(nfa.matches("ab"));
        
        DeterministicFiniteAutomaton dfa =
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches(""));
        assertTrue(dfa.matches("a"));
        assertTrue(dfa.matches("aa"));
        
        assertFalse(dfa.matches("b"));
        assertFalse(dfa.matches("ab"));
    }
    
    @Test
    public void unionOfDotsToDFA() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile(".|.");
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(nfa.matches("a"));
        assertTrue(nfa.matches("b"));
        assertTrue(nfa.matches("c"));
        
        assertFalse(nfa.matches(""));
        assertFalse(nfa.matches("ac"));
        assertFalse(nfa.matches("acb"));
        
        assertTrue(dfa.matches("a"));
        assertTrue(dfa.matches("b"));
        assertTrue(dfa.matches("c"));
        
        assertFalse(dfa.matches(""));
        assertFalse(dfa.matches("ac"));
        assertFalse(dfa.matches("acb"));
    }
    
    @Test
    public void dotNFAToDFA() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile(".");
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches("a"));
        assertTrue(dfa.matches("b"));
        assertTrue(dfa.matches("c"));
    }
    
    @Test
    public void dot1() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("..|.+");
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches("00"));
        assertTrue(dfa.matches("01"));
        assertTrue(dfa.matches("10"));
        assertTrue(dfa.matches("11"));
        assertTrue(dfa.matches("abc'"));
        
        assertFalse(dfa.matches(""));
    }
    
    @Test
    public void dot2() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile(".+");
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertFalse(dfa.matches(""));
        assertTrue(dfa.matches("1"));
        assertTrue(dfa.matches("10"));
        assertTrue(dfa.matches("110"));
    }
    
    @Test
    public void dot3() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("(.|1)+.");
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertFalse(dfa.matches(""));
        assertTrue(dfa.matches("10"));
        assertTrue(dfa.matches("a1b"));
        assertTrue(dfa.matches("ab0"));
    }
    
    @Test
    public void largeRegex() {
        String regex = "((((10)+)?)(((1.)(0|1))((..)|(01))))+";
        
        String acceptingText = "10101010101010101011001101010101010010";
        
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile(regex);
        
        assertTrue(nfa.matches(acceptingText));
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches(acceptingText));
    }
    
    @Test
    public void choiceBetweenOneAndThreeDots() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("1|...");
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(nfa.matches("1"));
        assertTrue(nfa.matches("111"));
        assertFalse(nfa.matches(""));
        assertFalse(nfa.matches("11"));
        assertFalse(nfa.matches("1111"));
        
        assertTrue(dfa.matches("1"));
        assertTrue(dfa.matches("111"));
        assertFalse(dfa.matches(""));
        assertFalse(dfa.matches("11"));
        assertFalse(dfa.matches("1111"));
    }
    
    @Test
    public void qRegex() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("(1|...)?");
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(nfa.matches(""));
        assertTrue(nfa.matches("1"));
        assertTrue(nfa.matches("012"));
        assertFalse(nfa.matches("10"));
        assertFalse(nfa.matches("1001"));
        
        assertTrue(dfa.matches(""));
        assertTrue(dfa.matches("1"));
        assertTrue(dfa.matches("012"));
        assertFalse(dfa.matches("10"));
        assertFalse(dfa.matches("1001"));
    }
    
    @Test
    public void semilargeRegex() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("(1|...)*(0|1)+");
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(nfa.matches("0"));
        assertTrue(nfa.matches("1"));
        assertTrue(nfa.matches("10"));
        assertTrue(nfa.matches("11"));
        assertTrue(nfa.matches("abc0"));
        assertTrue(nfa.matches("abc1"));
        assertTrue(nfa.matches("abc101"));
        assertTrue(nfa.matches("ab10"));
        assertTrue(nfa.matches("ab11"));
        
        assertFalse(nfa.matches("abcd"));
        assertFalse(nfa.matches("ab"));
        assertFalse(nfa.matches("ab0"));
        assertFalse(nfa.matches("ab1"));
        
        assertTrue(dfa.matches("0"));
        assertTrue(dfa.matches("1"));
        assertTrue(dfa.matches("10"));
        assertTrue(dfa.matches("11"));
        assertTrue(dfa.matches("abc0"));
        assertTrue(dfa.matches("abc1"));
        assertTrue(dfa.matches("abc101"));
        assertTrue(dfa.matches("ab10"));
        assertTrue(dfa.matches("ab11"));
        
        assertFalse(dfa.matches("abcd"));
        assertFalse(dfa.matches("ab"));
        assertFalse(dfa.matches("ab0"));
        assertFalse(dfa.matches("ab1"));
    }
    
    @Test
    public void test1() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("(.|...)*");
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(nfa.matches("a"));
        assertTrue(nfa.matches("aaa"));
        assertTrue(nfa.matches("aa"));
        assertTrue(nfa.matches("aaabbb"));
        
        assertTrue(dfa.matches("a"));
        assertTrue(dfa.matches("aaa"));
        assertTrue(dfa.matches("aa"));
        assertTrue(dfa.matches("aaabbb"));
    }
    
    @Test
    public void question1() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("..?.");
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(nfa.matches("ac"));
        assertTrue(nfa.matches("abc"));
        
        assertFalse(nfa.matches("a"));
        assertFalse(nfa.matches("abcd"));
        
        assertTrue(dfa.matches("ac"));
        assertTrue(dfa.matches("abc"));
        
        assertFalse(dfa.matches("a"));
        assertFalse(dfa.matches("abcd"));
    }
    
    @Test
    public void question2() {
        NondeterministicFiniteAutomaton nfa =
                NondeterministicFiniteAutomaton.compile("(a.b)?");
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(nfa.matches(""));
        assertTrue(nfa.matches("a0b"));
        assertTrue(nfa.matches("adb"));
        
        assertFalse(nfa.matches("a"));
        assertFalse(nfa.matches("ab"));
        assertFalse(nfa.matches("abcd"));
        
        assertTrue(dfa.matches(""));
        assertTrue(dfa.matches("a0b"));
        assertTrue(dfa.matches("adb"));
        
        assertFalse(dfa.matches("a"));
        assertFalse(dfa.matches("ab"));
        assertFalse(dfa.matches("abcd"));
    }
    
    @Test
    public void question3() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("((01)|(1|.))?");
        
        assertTrue(nfa.matches("0"));
//        System.out.println(nfa.getNumberOfStates());
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
//        dfa.hasBothDotAndCharacterTransitions();
        
        assertTrue(dfa.matches("0"));
    }
    
    @Test
    public void question4() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("((01)|.)?");
        
        DeterministicFiniteAutomaton dfa =
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(nfa.matches("01"));
        assertTrue(nfa.matches("0"));
        assertTrue(nfa.matches("1"));
        assertTrue(nfa.matches(""));
        
        assertFalse(nfa.matches("011"));
        assertFalse(nfa.matches("0110"));
        
        assertTrue(dfa.matches("01"));
        assertTrue(dfa.matches("0"));
        assertTrue(dfa.matches("1"));
        assertTrue(dfa.matches(""));
        
        assertFalse(dfa.matches("011"));
        assertFalse(dfa.matches("0110"));
    }
    
    @Test
    public void question5() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("(.|1|0)?");
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(nfa.matches("2"));
        assertTrue(nfa.matches("1"));
        assertTrue(nfa.matches("0"));
        assertTrue(nfa.matches(""));
        assertFalse(nfa.matches("10"));
        
        assertTrue(dfa.matches("2"));
        assertTrue(dfa.matches("1"));
        assertTrue(dfa.matches("0"));
        assertTrue(dfa.matches(""));
        assertFalse(dfa.matches("10"));
    }
    
    @Test
    public void pleaseWork() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile("(1|.)?");
        
        assertTrue(nfa.matches("1"));
        assertTrue(nfa.matches("0"));
        assertTrue(nfa.matches(""));
        
        assertFalse(nfa.matches("11"));
        assertFalse(nfa.matches("111"));
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches("1"));
        assertTrue(dfa.matches("0"));
        assertTrue(dfa.matches(""));
        
        assertFalse(dfa.matches("11"));
        assertFalse(dfa.matches("111"));
    }
    
    @Test
    public void debugOnSmallRegex() {
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile(".(0|.)");
        
        assertFalse(nfa.matches("a"));
        assertFalse(nfa.matches("abc"));
        
        assertTrue(nfa.matches("11"));
        assertTrue(nfa.matches("01"));
        assertTrue(nfa.matches("10"));
        assertTrue(nfa.matches("00"));
    }
    
    @Test
    public void computeTransitionMapWithoutPeriodWildcard() {
        Set<Character> alphabet = new HashSet<>(Arrays.asList('A', 'D', 'E'));
        
        DeterministicFiniteAutomatonStateTransitionMap transitionMap = 
                NondeterministicFiniteAutomaton
                        .computeTransitionMapWithoutPeriodWildcard(alphabet);
        
        DeterministicFiniteAutomatonState stateA = 
                new DeterministicFiniteAutomatonState(0);
        
        DeterministicFiniteAutomatonState stateB = 
                new DeterministicFiniteAutomatonState(1);
        
        DeterministicFiniteAutomatonState stateC = 
                new DeterministicFiniteAutomatonState(2);
        
        TransitionMapEntry transitionMapEntry = transitionMap.get(0);
        CharacterRange expectedCharacterRange = new CharacterRange('A');
        
        assertEquals(expectedCharacterRange, 
                     transitionMapEntry.getCharacterRange());
        
        transitionMapEntry = transitionMap.get(1);
        expectedCharacterRange = new CharacterRange('D');
        
        assertEquals(expectedCharacterRange, 
                     transitionMapEntry.getCharacterRange());
        
        transitionMapEntry = transitionMap.get(2);
        expectedCharacterRange = new CharacterRange('E');
        
        assertEquals(expectedCharacterRange, 
                     transitionMapEntry.getCharacterRange());
    }
}
