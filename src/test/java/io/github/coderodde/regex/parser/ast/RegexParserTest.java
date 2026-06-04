package io.github.coderodde.regex.parser.ast;

import io.github.coderodde.regex.parser.ast.tokens.RegexToken;
import io.github.coderodde.regex.parser.ast.tokens.RegexTokenCharacterClass;
import io.github.coderodde.regex.parser.ast.tokens.RegexTokenLiteral;
import io.github.coderodde.regex.parser.ast.tokens.RegexTokenSimple;
import io.github.coderodde.regex.parser.ast.tree.RegexNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class RegexParserTest {
    
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
                new RegexTokenLiteral((int) 'c'),
                new RegexTokenSimple(RegexTokenType.KLEENE_STAR),
                new RegexTokenLiteral((int) 'd'),
                new RegexTokenSimple(RegexTokenType.QUESTION),
                new RegexTokenSimple(RegexTokenType.LEFT_PARENTHESIS),
                new RegexTokenLiteral((int) 'e'),
                new RegexTokenLiteral((int) 'f'),
                new RegexTokenSimple(RegexTokenType.UNION),
                new RegexTokenLiteral((int) 'g'),
                new RegexTokenLiteral((int) 'h'),
                new RegexTokenSimple(RegexTokenType.RIGHT_PARENTHESIS),
                new RegexTokenSimple(RegexTokenType.PLUS)));
        
        RegexParser parser = new RegexParser(tokens);
        RegexNode root = parser.parse();
    }
}
