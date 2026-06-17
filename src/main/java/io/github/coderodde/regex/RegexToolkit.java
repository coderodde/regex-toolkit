package io.github.coderodde.regex;

import io.github.coderodde.regex.parser.ast.RegexParser;
import io.github.coderodde.regex.parser.ast.RegexTokenizationResult;
import io.github.coderodde.regex.parser.ast.tokens.RegexToken;
import io.github.coderodde.regex.parser.ast.tree.RegexNode;
import io.github.coderodde.regex.tokenizer.RegexTokenizer;
import java.util.List;

/**
 * This class defines the regular expression matcher creation API.
 */
public final class RegexToolkit {
    
    private RegexToolkit() {
        
    }
    
    /**
     * Compiles the input regular expression to a regex matcher (DFA in this 
     * case).
     * 
     * @param regex     the regular expression to match.
     * @param algorithm the minimization algorithm choice. If set to 
     *                  {@code null}, no minimization on the DFA is done.
     * @return the regex matcher.
     */
    public static RegularExpressionMatcher 
        compile(
            String regex, 
            DeterministicFiniteAutomaton.MinimizationAlgorithm algorithm) {
            
        RegexTokenizationResult tokenizationResult = 
            new RegexTokenizer().tokenize(regex);
        
        List<RegexToken> tokens = tokenizationResult.tokens();
        
        RegexNode root = new RegexParser(tokens).parse();
            
        NondeterministicFiniteAutomaton nfa = 
            new NondeterministicFiniteAutomatonCompiler(root)
                .compile(tokenizationResult);
        
        DeterministicFiniteAutomaton dfa = 
            nfa.convertToDeterministicFiniteAutomaton();
        
        if (algorithm != null) {
            switch (algorithm) {
                case MOORE:
                case HOPCROFT:
                    dfa = dfa.minimize(algorithm);
                    break;
                    
                default:
                    throw new EnumConstantNotPresentException(
                        DeterministicFiniteAutomaton
                            .MinimizationAlgorithm
                            .class, 
                        algorithm.toString());
            }
        }
        
        return dfa;
    }
       
    /**
     * Compiles the input regular expression to a regex matcher (DFA in this 
     * case). No minimization is done on the resulting DFA.
     * 
     * @param regex the target regular language.
     * @return the DFA matching {@code regex}.
     */
    public static RegularExpressionMatcher compile(String regex) {
        return compile(regex, null);
    }
}
