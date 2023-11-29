package com.github.coderodde.regex.benchmark;

import com.github.coderodde.regex.DeterministicFiniteAutomaton;
import com.github.coderodde.regex.NondeterministicFiniteAutomaton;
import java.util.Random;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 27, 2023)
 * @since 1.6 (Nov 27, 2023)
 */
public final class Benchmark {
    
    private static final int MAXIMUM_REGEX_TREE_DEPTH = 5;
    
    public static void main(String[] args) {
        // 1701257229722, 5
        long seed = 2361781411800L;
//        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        
        System.out.println("Seed = " + seed);
        
        RandomBinaryRegexBuilder builder = new RandomBinaryRegexBuilder();
        
        long startTime = System.nanoTime();
        RegexTreeNode root = 
                builder.buildRandomBinaryRegularExpression(
                        random, 
                        MAXIMUM_REGEX_TREE_DEPTH);
        
        long duration = System.nanoTime() - startTime;
        
        System.out.println(
                String.format(
                        "Built the regex tree in %1.3f milliseconds.", 
                        duration / 1_000_000.0)
                        .replace(',', '.'));
        
        startTime = System.nanoTime();
        String regex = builder.buildRegexString(root);
        duration = System.nanoTime() - startTime;
        
        System.out.println(
                String.format(
                        "Built the regex in %1.3f milliseconds.", 
                        duration / 1_000_000.0)
                        .replace(',', '.'));
        
        System.out.println("The regex is: " + regex);
        System.out.println("The regex length is: " + regex.length());
        
        startTime = System.nanoTime();
        String text  = builder.buildRandomAcceptingText(random, root);
        duration = System.nanoTime() - startTime;
        
        System.out.println("Accepting text: " + text);
        System.out.println("Accepting text length: " + text.length());
        
        System.out.println(
                String.format(
                        "Built the accepting text in %1.3f milliseconds.", 
                        duration / 1_000_000.0)
                        .replace(',', '.'));
        
        startTime = System.nanoTime();
        
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile(regex);
        
        duration = System.nanoTime() - startTime;
        
        System.out.println(
                String.format(
                        "Compiled the regex to NFA in %1.3f milliseconds.",
                        duration / 1_000_000.0).replace(',', '.'));
        
        startTime = System.nanoTime();
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        duration = System.nanoTime() - startTime;
        
        System.out.println(
                String.format(
                        "Compiled the NFA to DFA in %1.3f milliseconds.", 
                        duration / 1_000_000.0).replace(',', '.'));
        
        System.out.println(
                "Number of states in the NFA: " + nfa.getNumberOfStates());
        
        System.out.println(
                "Number of states in the source DFA: " +
                        dfa.getNumberOfStates());
        
        startTime = System.nanoTime();
//        dfa = dfa.minimizeViaHopcroftsAlgorithm();
        duration = System.nanoTime() - startTime;
        
        System.out.println(
                "Number of states in the minimized DFA: " +
                        dfa.getNumberOfStates());
        
        System.out.println(
            String.format(
                "Minimized the DFA via Hopcroft's algorithm in %1.3f " +
                        "milliseconds.",
                    duration / 1_000_000.0).replace(',', '.'));
        
        startTime = System.nanoTime();
        
        boolean dfaMatches = dfa.matches(text);
        
        duration = System.nanoTime() - startTime;
        
        System.out.println(
                String.format(
                        "DFA matches: " 
                                + dfaMatches 
                                + ", duration: %1.3f milliseconds.", 
                        duration / 1_000_000.0).replace(',', '.'));
        
        startTime = System.nanoTime();
        
        boolean nfaMatches = nfa.matches(text);
        
        duration = System.nanoTime() - startTime;
        
        System.out.println(
                String.format(
                        "NFA matches: " 
                                + nfaMatches 
                                + ", duration: %1.3f milliseconds.", 
                        duration / 1_000_000.0).replace(',', '.'));
    }
}
