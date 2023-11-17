package com.github.coderodde.regex;

import static com.github.coderodde.regex.TestUtils.getCharToken;
import static com.github.coderodde.regex.TestUtils.getConcatenation;
import static com.github.coderodde.regex.TestUtils.getKleeneStar;
import static com.github.coderodde.regex.TestUtils.getLeftParenthesis;
import static com.github.coderodde.regex.TestUtils.getRightParenthesis;
import static com.github.coderodde.regex.TestUtils.getUnion;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class RegexInfixToPostfixConverterTest {
    
    private final RegexInfixToPostfixConverter converter =
              new RegexInfixToPostfixConverter();
    
    private List<RegexToken> inputTokens;
    private Deque<RegexToken> expectedTokens;
    private Deque<RegexToken> tokens;
    
    
    @Test
    public void onEmptyRegex() {
        tokens = converter.convert(Collections.<RegexToken>emptyList());
        assertTrue(tokens.isEmpty());
    }
    
    @Test
    public void onSingleParentheses() {
        inputTokens = Arrays.asList(getLeftParenthesis(),
                                    getRightParenthesis());
        
        tokens = converter.convert(inputTokens);
        expectedTokens = new ArrayDeque<>(); // Empty deque.
        
        assertEq(expectedTokens, tokens);
    }
    
    @Test
    public void onDoubleParentheses() {
        inputTokens = Arrays.asList(getLeftParenthesis(),
                                    getRightParenthesis(),
                                    getLeftParenthesis(),
                                    getRightParenthesis());
        
        tokens = converter.convert(inputTokens);
        expectedTokens = new ArrayDeque<>(); // Empty deque.
        
        assertEq(expectedTokens, tokens);
    }
    
    @Test
    public void onTripleParentheses() {
        inputTokens = Arrays.asList(getLeftParenthesis(),
                                    getLeftParenthesis(),
                                    getRightParenthesis(),
                                    getLeftParenthesis(),
                                    getRightParenthesis(),
                                    getRightParenthesis());
        
        tokens = converter.convert(inputTokens);
        expectedTokens = new ArrayDeque<>(); // Empty deque.
        
        assertEq(expectedTokens, tokens);
    }
    
    @Test
    public void concatenatingEmptyParenthesesReturnsSingleConcatenation() {
        inputTokens = Arrays.asList(getLeftParenthesis(),
                                    getRightParenthesis(),
                                    getConcatenation(),
                                    getLeftParenthesis(),
                                    getRightParenthesis());
        
        tokens = converter.convert(inputTokens);
        expectedTokens = new ArrayDeque<>(Arrays.asList(getConcatenation()));
        
        assertEq(expectedTokens, tokens);
    }
    
    @Test
    public void unionEmptyParenthesesReturnsSingleUnion() {
        inputTokens = Arrays.asList(getLeftParenthesis(),
                                    getRightParenthesis(),
                                    getUnion(),
                                    getLeftParenthesis(),
                                    getRightParenthesis());
        
        tokens = converter.convert(inputTokens);
        expectedTokens = new ArrayDeque<>(Arrays.asList(getUnion()));
        
        assertEq(expectedTokens, tokens);
    }
    
    @Test
    public void onSimpleRegex() {
        // a*|b
        RegexToken a = getCharToken('a');
        RegexToken b = getCharToken('b');
        
        inputTokens = Arrays.asList(a,
                                    getKleeneStar(),
                                    getUnion(),
                                    b);
        
        tokens = converter.convert(inputTokens);
        // a*b|
        expectedTokens = 
                new ArrayDeque<>(
                        Arrays.asList(
                                a, 
                                getKleeneStar(), 
                                b, 
                                getUnion()));
        
        assertEq(expectedTokens, tokens);
    }
    
    @Test
    public void onMiddleSizeRegex() {
        RegexToken a = getCharToken('a');
        RegexToken b = getCharToken('b');
 
        // ab*|b
        inputTokens = Arrays.asList(a,
                                    getConcatenation(),
                                    b,
                                    getKleeneStar(),
                                    getUnion(),
                                    b);
        
        // ab*ob|
        expectedTokens = new ArrayDeque<>(Arrays.asList(a,
                                                        b,
                                                        getKleeneStar(),
                                                        getConcatenation(),
                                                        b,
                                                        getUnion()));
        
        tokens = converter.convert(inputTokens);
        assertEq(expectedTokens, tokens);
    }
    
    @Test
    public void onComplexRegex() {
        RegexToken a = getCharToken('a');
        RegexToken b = getCharToken('b');
        
        // (ab|b)*|(ba*)*
        inputTokens = Arrays.asList(getLeftParenthesis(),
                                    a,
                                    getConcatenation(),
                                    b,
                                    getUnion(),
                                    b,
                                    getRightParenthesis(),
                                    getKleeneStar(),
                                    getUnion(),
                                    getLeftParenthesis(),
                                    b,
                                    getConcatenation(),
                                    a,
                                    getKleeneStar(),
                                    getRightParenthesis(),
                                    getKleeneStar());
        
        // (a b o b | * b b * o * |
        expectedTokens = 
                new ArrayDeque<>(
                        Arrays.asList(a,
                                      b,
                                      getConcatenation(),
                                      b,
                                      getUnion(),
                                      getKleeneStar(),
                                      b,
                                      a,
                                      getKleeneStar(),
                                      getConcatenation(),
                                      getKleeneStar(),
                                      getUnion()));
        
        tokens = converter.convert(inputTokens);
        assertEq(expectedTokens, tokens);
        
//        System.out.println(inputTokens);
//        System.out.println(tokens);
    }
    
    private static void assertEq(Deque<RegexToken> expectedTokens,
                                 Deque<RegexToken> tokens) {
        assertEquals(new ArrayList<>(expectedTokens),
                     new ArrayList<>(tokens));
    }
}
