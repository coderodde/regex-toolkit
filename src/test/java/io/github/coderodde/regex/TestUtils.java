package io.github.coderodde.regex;

import io.github.coderodde.regex.parser.ast.tokens.RegexToken;
import io.github.coderodde.regex.parser.ast.RegexTokenType;

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
    
    static RegexToken getPlus() {
        return new RegexToken(RegexTokenType.PLUS);
    }
}
