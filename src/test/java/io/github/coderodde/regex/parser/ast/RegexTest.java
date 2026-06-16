package io.github.coderodde.regex.parser.ast;

import io.github.coderodde.regex.DeterministicFiniteAutomaton;
import static io.github.coderodde.regex.DeterministicFiniteAutomaton.MinimizationAlgorithm.HOPCROFT;
import static io.github.coderodde.regex.DeterministicFiniteAutomaton.MinimizationAlgorithm.MOORE;
import io.github.coderodde.regex.NondeterministicFiniteAutomaton;
import io.github.coderodde.regex.NondeterministicFiniteAutomatonCompiler;
import io.github.coderodde.regex.parser.ast.tokens.RegexToken;
import io.github.coderodde.regex.parser.ast.tree.RegexNode;
import io.github.coderodde.regex.parser.ast.tree.UnionRegexNode;
import io.github.coderodde.regex.tokenizer.RegexTokenizer;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class RegexTest {
    
    private final List<RegexToken> tokens = new ArrayList<>();
    
    @After
    public void afterUnitTest() {
        tokens.clear();
    }
    
    @Test
    public void test1() {
        String regex = "a|bc*d?(ef|gh)+";
        RegexTokenizationResult tokenization = 
                new RegexTokenizer().tokenize(regex);
        
        RegexParser parser = new RegexParser(tokenization.tokens());
        RegexNode root = parser.parse();
        
        assertTrue(root instanceof UnionRegexNode);
        
        NondeterministicFiniteAutomaton nfa = 
                new NondeterministicFiniteAutomatonCompiler(root)
                    .compile(tokenization);
        
        assertTrue(nfa.matches("a"));
        assertTrue(nfa.matches("bef"));
        assertTrue(nfa.matches("bcef"));
        assertTrue(nfa.matches("bcccdgh"));
        assertTrue(nfa.matches("bccefefgh"));
        
        assertFalse(nfa.matches("b"));
        assertFalse(nfa.matches("bc"));
        assertFalse(nfa.matches("bd"));
        assertFalse(nfa.matches("ef"));
        assertFalse(nfa.matches("bccd"));
        assertFalse(nfa.matches("aa"));
        
        DeterministicFiniteAutomaton dfa =
            nfa.convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches("a"));
        assertTrue(dfa.matches("bef"));
        assertTrue(dfa.matches("bcef"));
        assertTrue(dfa.matches("bcccdgh"));
        assertTrue(dfa.matches("bccefefgh"));
        
        assertFalse(dfa.matches("b"));
        assertFalse(dfa.matches("bc"));
        assertFalse(dfa.matches("bd"));
        assertFalse(dfa.matches("ef"));
        assertFalse(dfa.matches("bccd"));
        assertFalse(dfa.matches("aa"));
        
        DeterministicFiniteAutomaton dfah = dfa.minimize(HOPCROFT);
        DeterministicFiniteAutomaton dfam = dfa.minimize(MOORE);
        
        assertTrue(dfah.matches("a"));
        assertTrue(dfah.matches("bef"));
        assertTrue(dfah.matches("bcef"));
        assertTrue(dfah.matches("bcccdgh"));
        assertTrue(dfah.matches("bccefefgh"));
        
        assertFalse(dfah.matches("b"));
        assertFalse(dfah.matches("bc"));
        assertFalse(dfah.matches("bd"));
        assertFalse(dfah.matches("ef"));
        assertFalse(dfah.matches("bccd"));
        assertFalse(dfah.matches("aa"));
        
        assertTrue(dfam.matches("a"));
        assertTrue(dfam.matches("bef"));
        assertTrue(dfam.matches("bcef"));
        assertTrue(dfam.matches("bcccdgh"));
        assertTrue(dfam.matches("bccefefgh"));
        
        assertFalse(dfam.matches("b"));
        assertFalse(dfam.matches("bc"));
        assertFalse(dfam.matches("bd"));
        assertFalse(dfam.matches("ef"));
        assertFalse(dfam.matches("bccd"));
        assertFalse(dfam.matches("aa"));
        
        System.out.println(dfa.computeRegularExression());
        System.out.println(dfam.computeRegularExression());
        System.out.println(dfah.computeRegularExression());
    }
    
    @Test
    public void startEndOfLineSymbol() {
        RegexTokenizer tokenizer = new RegexTokenizer();
        RegexTokenizationResult tokenization = tokenizer.tokenize("^abc$");
        RegexParser parser = new RegexParser(tokenization.tokens());
        
        DeterministicFiniteAutomaton dfa = 
            new NondeterministicFiniteAutomatonCompiler(parser.parse())
                .compile(tokenization)
                .convertToDetermenisticFiniteAutomaton();
        
        assertTrue(dfa.matches("abc"));
        
        assertFalse(dfa.matches("adc"));
        assertFalse(dfa.matches("xabc"));
        assertFalse(dfa.matches("abcy"));
        assertFalse(dfa.matches("xabcy"));
    }
    
    @Test
    public void characterClass1() {
        RegexTokenizer tokenizer = new RegexTokenizer();
        RegexTokenizationResult res = tokenizer.tokenize("[abc][a-cg-m]");
        List<RegexToken> tokens = res.tokens();
        System.out.println("oh fuck yeah");
        System.out.println(tokens);
    }
}
