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
        long seed = System.nanoTime();
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
        
        startTime = System.nanoTime();
        String text  = builder.buildRandomAcceptingText(random, root);
        duration = System.nanoTime() - startTime;
        
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
        
        startTime = System.nanoTime();
        
        duration = System.nanoTime() - startTime;
    }
}
