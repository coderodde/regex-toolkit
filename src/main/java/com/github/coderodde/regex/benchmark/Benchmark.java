package com.github.coderodde.regex.benchmark;

import com.github.coderodde.regex.DeterministicFiniteAutomaton;
import com.github.coderodde.regex.NondeterministicFiniteAutomaton;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.RegExp;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 27, 2023)
 * @since 1.6 (Nov 27, 2023)
 */
public final class Benchmark {
    
    private static final int MAXIMUM_REGEX_TREE_DEPTH = 5;
    private static final int BENCHMARK_RUNS = 10_000;
    
    public static void main(String[] args) {
//        findFailingNFA();
//        System.exit(0);
        
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        
        System.out.println("Seed = " + seed);
        
        RandomBinaryRegexBuilder builder = new RandomBinaryRegexBuilder(random);
        
        long startTime = System.nanoTime();
        RegexTreeNode root = 
                builder.buildRandomBinaryRegularExpression(
                        MAXIMUM_REGEX_TREE_DEPTH);
        
        long duration = System.nanoTime() - startTime;
        
        System.out.println(
                String.format(
                        "Built the regex tree in %.3f milliseconds.", 
                        duration / 1_000_000.0)
                        .replace(',', '.'));
        
        startTime = System.nanoTime();
        String regex = builder.buildRegexString(root);
        duration = System.nanoTime() - startTime;
        
        System.out.println(
                String.format(
                        "Built the regex in %.3f milliseconds.", 
                        duration / 1_000_000.0)
                        .replace(',', '.'));
        
        System.out.println("The regex is: " + regex);
        System.out.println("The regex length is: " + regex.length());
        
        List<String> benchmarkData  = new ArrayList<>(BENCHMARK_RUNS);
        List<Boolean> nfaResults    = new ArrayList<>(BENCHMARK_RUNS);
        List<Boolean> dfaResults    = new ArrayList<>(BENCHMARK_RUNS);
        List<Boolean> javaResults   = new ArrayList<>(BENCHMARK_RUNS);
        List<Boolean> luceneResults = new ArrayList<>(BENCHMARK_RUNS);
        
        startTime = System.nanoTime();
        
        for (int i = 0; i < BENCHMARK_RUNS; i++) {
            String text  = builder.buildRandomAcceptingText(root);
            benchmarkData.add(text);
        }
        
        duration = System.nanoTime() - startTime;
        
        System.out.println(
                String.format(
                        "Built the benchmark data in %.3f milliseconds.", 
                        duration / 1_000_000.0)
                        .replace(',', '.'));
        
        startTime = System.nanoTime();
        
        NondeterministicFiniteAutomaton nfa = 
                NondeterministicFiniteAutomaton.compile(regex);
        
        duration = System.nanoTime() - startTime;
        
        System.out.println(
                String.format(
                        "Compiled the regex to NFA in %.3f milliseconds.",
                        duration / 1_000_000.0).replace(',', '.'));
        
        startTime = System.nanoTime();
        
        DeterministicFiniteAutomaton dfa = 
                nfa.convertToDetermenisticFiniteAutomaton();
        
        duration = System.nanoTime() - startTime;
        
        System.out.println(
                String.format(
                        "Compiled the NFA to DFA in %.3f milliseconds.", 
                        duration / 1_000_000.0).replace(',', '.'));
        
        System.out.println(
                "Number of states in the NFA: " + nfa.getNumberOfStates());
        
        System.out.println(
                "Number of states in the source DFA: " +
                        dfa.getNumberOfStates());
        
        startTime = System.nanoTime();
        
        Pattern pattern = Pattern.compile(regex);
        
        duration = System.nanoTime() - startTime;
        
        System.out.println(
                String.format("Pattern.compile(...) in %.3f milliseconds.", 
                              duration / 1_000_000.0).replace(',', '.'));
        
        startTime = System.nanoTime();
        
        Automaton luceneAutomaton = new RegExp(regex).toAutomaton();
        
        duration = System.nanoTime() - startTime;
        
        System.out.println(
                String.format(
                        "Lucene automaton in %.3f milliseconds.",
                        duration / 1_000_000.0).replace(',', '.'));
        
        startTime = System.nanoTime();
//        dfa = dfa.minimizeViaHopcroftsAlgorithm();
        duration = System.nanoTime() - startTime;
        
        System.out.println(
                "Number of states in the minimized DFA: " +
                        dfa.getNumberOfStates());
        
        System.out.println(
            String.format(
                "Minimized the DFA via Hopcroft's algorithm in %.3f " +
                        "milliseconds.",
                    duration / 1_000_000.0).replace(',', '.'));
        
        startTime = System.nanoTime();
        
        for (String text : benchmarkData) {
            boolean dfaMatches = dfa.matches(text);
            dfaResults.add(dfaMatches);
        }
        
        duration = System.nanoTime() - startTime;
        
        System.out.println(
                String.format("DFA duration: %.3f milliseconds.", 
                              duration / 1_000_000.0).replace(',', '.'));
        
        startTime = System.nanoTime();
        
        for (String text : benchmarkData) {
            boolean nfaMatches = nfa.matches(text);
            nfaResults.add(nfaMatches);
        }
        
        duration = System.nanoTime() - startTime;
        
        System.out.println(
                String.format("NFA duration: %.3f milliseconds.", 
                              duration / 1_000_000.0).replace(',', '.'));
        
        startTime = System.nanoTime();
        
        for (String text : benchmarkData) {
            boolean javaRegexMatches = pattern.matcher(text).matches();
            javaResults.add(javaRegexMatches);
        }
        
        duration = System.nanoTime() - startTime;
        
        System.out.println(
                String.format(
                        "java.util.regex.Pattern duration: %.3f milliseconds.", 
                        duration / 1_000_000.0).replace(',', '.'));
        
        startTime = System.nanoTime();
        
        for (String text : benchmarkData) {
            boolean luceneRegexMatches = matchViaLucene(luceneAutomaton, text);
            luceneResults.add(luceneRegexMatches);
        }
        
        duration = System.nanoTime() - startTime;
        
        System.out.println(
                String.format(
                        "Lucene Automaton duration: %.3f milliseconds.", 
                        duration / 1_000_000.0).replace(',', '.'));
        
        System.out.println(
                "Algorithms agree: " + 
                        (nfaResults.equals(javaResults) && 
                         javaResults.equals(luceneResults) &&
                         luceneResults.equals(dfaResults)));
    }
    
    private static boolean matchViaLucene(Automaton automaton, String text) {
        int state = 0;
        
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            state = automaton.step(state, ch);
            
            if (state == -1) {
                return false;
            }
        }
        
        return automaton.isAccept(state);
    }
    
    private static void bruteForceFindFailing() {
        long seed = 2L;
        Random random = new Random(seed);
        RandomBinaryRegexBuilder regexBuilder = 
                new RandomBinaryRegexBuilder(random);
        
        outerLoop:
        for (int depth = 1; depth <= 5; depth++) {
            for (int i = 0; i < 1000; i++) {
                RegexTreeNode root = 
                        regexBuilder.buildRandomBinaryRegularExpression(depth);
                
                String regex = regexBuilder.buildRegexString(root);
                String text = regexBuilder.buildRandomAcceptingText(root);
                
                NondeterministicFiniteAutomaton nfa = 
                        NondeterministicFiniteAutomaton.compile(regex);
                
                DeterministicFiniteAutomaton dfa = 
                        nfa.convertToDetermenisticFiniteAutomaton();
                
                if (!nfa.matches(text)) {
                    System.out.println("NFA regex: " + regex);
                    System.out.println("NFA text:  " + text);
                    throw new IllegalStateException("NFA did not approve.");
                }
                
                if (!dfa.matches(text)) {
                    System.out.println("Regex: " + regex);
                    System.out.println("Text:  " + text);
                    break outerLoop;
                }
            }
        }
    }
    
    private static void findFailingNFA() {
        long seed = System.nanoTime();
        Random random = new Random(seed);
        
        System.out.println("findFailingNFA, seed = " + seed);
        
        RandomBinaryRegexBuilder regexBuilder =
                new RandomBinaryRegexBuilder(random);
        
        for (int depth = 1; depth <= 5; depth++) {
            for (int regexIteration = 0; 
                     regexIteration < 1_000; 
                     regexIteration++) {
                
                RegexTreeNode root = regexBuilder.buildRandomRegexTree(depth);
                String regex = regexBuilder.buildRegexString(root);
                
                NondeterministicFiniteAutomaton nfa = 
                        NondeterministicFiniteAutomaton.compile(regex);
                
                Pattern pattern = Pattern.compile(regex);
                
                for (int matchIteration = 0; 
                         matchIteration < 100;
                         matchIteration++) {

                    String acceptedText = 
                            regexBuilder.buildRandomAcceptingText(root);
                    
                    Matcher matcher = pattern.matcher(acceptedText);
                    
                    if (!matcher.matches()) {
                        throw new IllegalStateException(
                                "Pattern disagreed on regex = \"" 
                                        + regex 
                                        + "\" and text = \"" 
                                        + acceptedText 
                                        + "\".");
                    }
                    
                    if (!nfa.matches(acceptedText)) {
                        System.err.println(
                                "NFA disagreed on regex \"" 
                                        + regex 
                                        + "\" and text \"" 
                                        + acceptedText 
                                        + "\".");
                        
                        System.exit(1);
                    }
                }
            }
        }
    }
}
