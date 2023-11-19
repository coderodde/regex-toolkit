package com.github.coderodde.regex;

import static com.github.coderodde.regex.TestUtils.getCharToken;
import static com.github.coderodde.regex.TestUtils.getConcatenation;
import static com.github.coderodde.regex.TestUtils.getKleeneStar;
import static com.github.coderodde.regex.TestUtils.getLeftParenthesis;
import static com.github.coderodde.regex.TestUtils.getRightParenthesis;
import static com.github.coderodde.regex.TestUtils.getUnion;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class RegexTokenizerTest {
    
    private final RegexTokenizer tokenizer = new RegexTokenizer();
    private List<RegexToken> tokens;
    private List<RegexToken> expectedTokens;
    
    @Test
    public void returnsEmptyListOnEmptyRegex() {
        tokens = tokenizer.tokenize("");
        assertTrue(tokens.isEmpty());
    }
    
    @Test
    public void acceptsOnSingleChar() {
        tokens = tokenizer.tokenize("a");
        assertEquals(1, tokens.size());
        assertEquals(getCharToken('a'), tokens.get(0));
    }
    
    @Test
    public void acceptsOnTwoChars() {
        tokens = tokenizer.tokenize("ab");
        expectedTokens =
                Arrays.asList(
                        getCharToken('a'), 
                        getConcatenation(), 
                        getCharToken('b'));
        
        assertEquals(3, tokens.size());
        assertEquals(expectedTokens, tokens);
    }
    
    @Test
    public void acceptsOnThreeChars() {
        tokens = tokenizer.tokenize("abc");
        expectedTokens =
                Arrays.asList(
                        getCharToken('a'), 
                        getConcatenation(), 
                        getCharToken('b'),
                        getConcatenation(),
                        getCharToken('c'));
        
        assertEquals(5, tokens.size());
        assertEquals(expectedTokens, tokens);
    }
    
    @Test(expected = InvalidRegexException.class)
    public void throwsOnBrokenParenthesesAreConcatenated() {
        tokenizer.tokenize(")(");
    }
    
    @Test(expected = InvalidRegexException.class) 
    public void throwsOnBrokenComplexParentheses() {
        tokenizer.tokenize("(()())(");
    }
    
    @Test
    public void tokenizesDoubleParentheses() {
        tokens = tokenizer.tokenize("(())");
        expectedTokens = 
                Arrays.asList(getLeftParenthesis(),
                              getLeftParenthesis(),
                              getRightParenthesis(),
                              getRightParenthesis());
        
        assertEquals(expectedTokens, tokens);
    }
    
    @Test
    public void tokenizesTripleParentheses() {
        tokens = tokenizer.tokenize("((()))");
        expectedTokens = 
                Arrays.asList(getLeftParenthesis(),
                              getLeftParenthesis(),
                              getLeftParenthesis(),
                              getRightParenthesis(),
                              getRightParenthesis(),
                              getRightParenthesis());
        
        assertEquals(expectedTokens, tokens);
    }
    
    @Test
    public void tokenizesTripleParenthesesInterleaved() {
        tokens = tokenizer.tokenize("(()())");
        expectedTokens = 
                Arrays.asList(getLeftParenthesis(),
                              getLeftParenthesis(),
                              getRightParenthesis(),
                              getConcatenation(),
                              getLeftParenthesis(),
                              getRightParenthesis(),
                              getRightParenthesis());
        
        assertEquals(expectedTokens, tokens);
    }
    
    @Test
    public void testKleeneStar() {
        tokens = tokenizer.tokenize("a*");
        expectedTokens = 
                Arrays.asList(getCharToken('a'),
                              getKleeneStar());
        
        assertEquals(expectedTokens, tokens);
    }
    
    @Test
    public void testKleeneStarWithPreceedingChar() {
        tokens = tokenizer.tokenize("ba*");
        expectedTokens = 
                Arrays.asList(getCharToken('b'),
                              getConcatenation(),
                              getCharToken('a'),
                              getKleeneStar());
        
        assertEquals(expectedTokens, tokens);
    }
    
    @Test
    public void testKleeneStarWithDoubleChar() {
        tokens = tokenizer.tokenize("(ac)*");
        expectedTokens = 
                Arrays.asList(getLeftParenthesis(),
                              getCharToken('a'),
                              getConcatenation(),
                              getCharToken('c'),
                              getRightParenthesis(),
                              getKleeneStar());
        
        assertEquals(expectedTokens, tokens);
    }
    
    @Test
    public void testUnion() {
        tokens = tokenizer.tokenize("a|b");
        expectedTokens =
                Arrays.asList(getCharToken('a'),
                              getUnion(),
                              getCharToken('b'));
        
        assertEquals(expectedTokens, tokens);
    }
    
    @Test
    public void testUnionTwoPreceedingChars() {
        tokens = tokenizer.tokenize("ab|c");
        expectedTokens =
                Arrays.asList(getCharToken('a'),
                              getConcatenation(),
                              getCharToken('b'),
                              getUnion(),
                              getCharToken('c'));
        
        assertEquals(expectedTokens, tokens);
    }
    
    @Test
    public void testUnionTwoPreceedingCharsInParentheses() {
        tokens = tokenizer.tokenize("(ab)|c");
        expectedTokens =
                Arrays.asList(getLeftParenthesis(),
                              getCharToken('a'),
                              getConcatenation(),
                              getCharToken('b'),
                              getRightParenthesis(),
                              getUnion(),
                              getCharToken('c'));
        
        assertEquals(expectedTokens, tokens);
    }
    
    @Test(expected = InvalidRegexException.class)
    public void throwsOnAddConcatenationBeforeLeftParenthesis() {
        tokenizer.tokenize("a(");
    }
    
    @Test(expected = InvalidRegexException.class)
    public void throwsOnAddConcatenationAfterKleeneStar() {
        tokenizer.tokenize("*(");
    }
    
    @Test
    public void addConcatenationAfterKleeneStarBeforeChar() {
        tokens = tokenizer.tokenize("a*b");
        expectedTokens = 
                Arrays.asList(getCharToken('a'),
                              getKleeneStar(),
                              getConcatenation(),
                              getCharToken('b'));
        
        assertEquals(expectedTokens, tokens);
    }
    
    @Test(expected = InvalidRegexException.class)
    public void
         throwsOnAddConcatenationAfterRightParenthesisAndBeforeCharacter() {
        tokenizer.tokenize(")a");
    }
    
    @Test
    public void addConcatenationAfterKleeneStarBeforeCharacter() {
        tokens = tokenizer.tokenize("a*b");
        expectedTokens = 
                Arrays.asList(getCharToken('a'),
                              getKleeneStar(),
                              getConcatenation(),
                              getCharToken('b'));
        
        assertEquals(expectedTokens, tokens);
    }
}
