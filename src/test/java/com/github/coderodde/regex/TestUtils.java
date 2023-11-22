package com.github.coderodde.regex;

class TestUtils {
    
    static RegexToken getCharToken(char ch) {
        return new RegexToken(RegexTokenType.CHARACTER, ch);
    }
    
    static RegexToken getConcatenation() {
        return new RegexToken(RegexTokenType.CONCAT);
    }
    
    static RegexToken getUnion() {
        return new RegexToken(RegexTokenType.UNION);
    }
    
    static RegexToken getKleeneStar() {
        return new RegexToken(RegexTokenType.KLEENE_STAR);
    }
    
    static RegexToken getLeftParenthesis() {
        return new RegexToken(RegexTokenType.LEFT_PARENTHESIS);
    }
    
    static RegexToken getRightParenthesis() {
        return new RegexToken(RegexTokenType.RIGHT_PARENTHESIS);
    }   
    
    static RegexToken getQuestion() {
        return new RegexToken(RegexTokenType.QUESTION);
    }
    
    static RegexToken getDot() {
        return new RegexToken(RegexTokenType.DOT);
    }
}
