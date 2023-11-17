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
    
    private final RegexToken a = getCharToken('a');
    private final RegexToken b = getCharToken('b');
    private final RegexToken star = getKleeneStar();
    private final RegexToken left = getLeftParenthesis();
    private final RegexToken right = getRightParenthesis();
    private final RegexToken union = getUnion();
    private final RegexToken concat = getConcatenation();
    
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
        expectedTokens = deque(); // Empty deque.
        
        assertEq(expectedTokens, tokens);
    }
    
    @Test
    public void onDoubleParentheses() {
        inputTokens = Arrays.asList(left, right, left, right);
        
        tokens = converter.convert(inputTokens);
        expectedTokens = deque(); // Empty deque.
        
        assertEq(expectedTokens, tokens);
    }
    
    @Test
    public void onTripleParentheses() {
        inputTokens = Arrays.asList(left,
                                    left,
                                    right,
                                    left,
                                    right,
                                    right);
        
        tokens = converter.convert(inputTokens);
        expectedTokens = deque(); // Empty deque.
        
        assertEq(expectedTokens, tokens);
    }
    
    @Test
    public void concatenatingEmptyParenthesesReturnsSingleConcatenation() {
        inputTokens = Arrays.asList(left,
                                    right,
                                    concat,
                                    left,
                                    right);
        
        tokens = converter.convert(inputTokens);
        expectedTokens = deque(concat);
        
        assertEq(expectedTokens, tokens);
    }
    
    @Test
    public void unionEmptyParenthesesReturnsSingleUnion() {
        inputTokens = Arrays.asList(left,
                                    right,
                                    union,
                                    left,
                                    right);
        
        tokens = converter.convert(inputTokens);
        expectedTokens = deque(union);
        
        assertEq(expectedTokens, tokens);
    }
    
    @Test
    public void onSimpleRegex() {
        // a*|b
        inputTokens = Arrays.asList(a,
                                    star,
                                    union,
                                    b);
        
        tokens = converter.convert(inputTokens);
        // a*b|
        expectedTokens = deque(a, star, b, union);
        assertEq(expectedTokens, tokens);
    }
    
    @Test
    public void onMiddleSizeRegex() {
        // ab*|b
        inputTokens = Arrays.asList(a,
                                    concat,
                                    b,
                                    star,
                                    union,
                                    b);
        
        // ab*ob|
        expectedTokens = deque(a, b, star, concat, b, union);
        tokens = converter.convert(inputTokens);
        assertEq(expectedTokens, tokens);
    }
    
    @Test
    public void onComplexRegex() {
        // (ab|b)*|(ba*)*
        inputTokens = Arrays.asList(left,
                                    a,
                                    concat,
                                    b,
                                    union,
                                    b,
                                    right,
                                    star,
                                    union,
                                    left,
                                    b,
                                    concat,
                                    a,
                                    star,
                                    right,
                                    star);
        
        // (a b o b | * b b * o * |
        expectedTokens = deque(a,
                               b,
                               concat,
                               b,
                               union,
                               star,
                               b,
                               a,
                               star,
                               concat,
                               star,
                               union);
        
        tokens = converter.convert(inputTokens);
        assertEq(expectedTokens, tokens);
        
//        System.out.println(inputTokens);
//        System.out.println(tokens);
    }
    
    @Test
    public void worksOnSimplePostfix1() {
        tokens = converter.convert(Arrays.asList(a, a, union));
        expectedTokens = deque(a, a, union);
        assertEq(expectedTokens, tokens);
    }
    
    @Test
    public void worksOnSimplePostix2() {
        tokens = converter.convert(Arrays.asList(a, a, concat));
        expectedTokens = deque(a, a, concat);
        assertEq(expectedTokens, tokens);
    }
    
    @Test(expected = BadRegexException.class)
    public void throwsOnSingleLeftParenthesis() {
        converter.convert(Arrays.asList(left));
    }
    
    @Test(expected = BadRegexException.class)
    public void throwsOnSingleRightParenthesis() {
        converter.convert(Arrays.asList(right));
    }
    
    @Test(expected = BadRegexException.class)
    public void throwsOnBadParenthesation1() {
        converter.convert(Arrays.asList(right, left));
    }
    
    @Test(expected = BadRegexException.class)
    public void throwsOnBadParenthesation2() {
        converter.convert(Arrays.asList(left, right, right));
    }
    
    private static void assertEq(Deque<RegexToken> expectedTokens,
                                 Deque<RegexToken> tokens) {
        assertEquals(new ArrayList<>(expectedTokens),
                     new ArrayList<>(tokens));
    }
    
    private static Deque<RegexToken> deque(RegexToken... list) {
        return new ArrayDeque<>(Arrays.asList(list));
    }
}
