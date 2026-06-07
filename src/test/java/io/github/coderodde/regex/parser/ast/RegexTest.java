package io.github.coderodde.regex.parser.ast;

import io.github.coderodde.regex.DeterministicFiniteAutomaton;
import static io.github.coderodde.regex.DeterministicFiniteAutomaton.MinimizationAlgorithm.HOPCROFT;
import static io.github.coderodde.regex.DeterministicFiniteAutomaton.MinimizationAlgorithm.MOORE;
import io.github.coderodde.regex.NondeterministicFiniteAutomaton;
import io.github.coderodde.regex.NondeterministicFiniteAutomatonCompiler;
import io.github.coderodde.regex.parser.ast.tokens.RegexToken;
import io.github.coderodde.regex.parser.ast.tokens.RegexTokenLiteral;
import io.github.coderodde.regex.parser.ast.tokens.RegexTokenSimple;
import io.github.coderodde.regex.parser.ast.tree.ConcatenationRegexNode;
import io.github.coderodde.regex.parser.ast.tree.LiteralRegexNode;
import io.github.coderodde.regex.parser.ast.tree.RegexNode;
import io.github.coderodde.regex.parser.ast.tree.UnionRegexNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class RegexTest {
    
    private static final LiteralRegexNode a = new LiteralRegexNode((int) 'a');
    private static final LiteralRegexNode b = new LiteralRegexNode((int) 'b');
    private static final LiteralRegexNode c = new LiteralRegexNode((int) 'c');
    private static final LiteralRegexNode d = new LiteralRegexNode((int) 'd');
    private static final LiteralRegexNode e = new LiteralRegexNode((int) 'e');
    private static final LiteralRegexNode f = new LiteralRegexNode((int) 'f');
    private static final LiteralRegexNode g = new LiteralRegexNode((int) 'g');
    private static final LiteralRegexNode h = new LiteralRegexNode((int) 'h');
    
    private final List<RegexToken> tokens = new ArrayList<>();
    
    @After
    public void afterUnitTest() {
        tokens.clear();
    }
    
    @Test
    public void test1() {
        tokens.addAll(
            Arrays.asList(
                    new RegexTokenLiteral((int) 'a'),
                    new RegexTokenSimple(RegexTokenType.UNION),
                    new RegexTokenLiteral((int) 'b'),
                    new RegexTokenSimple(RegexTokenType.CONCATENATION),
                    new RegexTokenLiteral((int) 'c'),
                    new RegexTokenSimple(RegexTokenType.KLEENE_STAR),
                    new RegexTokenSimple(RegexTokenType.CONCATENATION),
                    new RegexTokenLiteral((int) 'd'),
                    new RegexTokenSimple(RegexTokenType.QUESTION),
                    new RegexTokenSimple(RegexTokenType.CONCATENATION),
                    new RegexTokenSimple(RegexTokenType.LEFT_PARENTHESIS),
                    new RegexTokenLiteral((int) 'e'),
                    new RegexTokenSimple(RegexTokenType.CONCATENATION),
                    new RegexTokenLiteral((int) 'f'),
                    new RegexTokenSimple(RegexTokenType.UNION),
                    new RegexTokenLiteral((int) 'g'),
                    new RegexTokenSimple(RegexTokenType.CONCATENATION),
                    new RegexTokenLiteral((int) 'h'),
                    new RegexTokenSimple(RegexTokenType.RIGHT_PARENTHESIS),
                    new RegexTokenSimple(RegexTokenType.PLUS)));
        
        RegexParser parser = new RegexParser(tokens);
        RegexNode root = parser.parse();
        
        assertTrue(root instanceof UnionRegexNode);
        
        assertEquals(a, ((UnionRegexNode) root).left());
        assertTrue(((UnionRegexNode) root).right() instanceof ConcatenationRegexNode);
        
        NondeterministicFiniteAutomaton nfa = 
                new NondeterministicFiniteAutomatonCompiler(root).compile();
        
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
        
        System.out.println("states: " + dfa.getNumberOfStates());
        
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
        
        System.out.println(dfah.getNumberOfStates());
        System.out.println(dfam.getNumberOfStates());
    }
}
