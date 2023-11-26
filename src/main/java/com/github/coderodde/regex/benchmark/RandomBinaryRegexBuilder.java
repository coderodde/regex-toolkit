package com.github.coderodde.regex.benchmark;

import com.github.coderodde.regex.RegexToken;
import com.github.coderodde.regex.RegexTokenType;
import java.util.Random;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 26, 2023)
 * @since 1.6 (Nov 26, 2023)
 */
public final class RandomBinaryRegexBuilder {
    
    public String buildRandomBinaryRegularExpression(Random random, int depth) {
        return "";
    }
    
    private static int getRandomNumberOfOperators(Random random) {
        return random.nextInt(3);
    }
    
    private static String constructRegexFromTree(RegexTreeNode root, 
                                                 int depth) {
        
        StringBuilder sb = new StringBuilder();
        constructRegexFromTreeImpl(root, sb);
        return sb.toString();
    }
    
    private static void constructRegexFromTreeImpl(RegexTreeNode root, 
                                                   StringBuilder sb) {
        
    }
    
    private static RegexToken getRandomBitRegexToken(Random random) {
        return new RegexToken(RegexTokenType.CHARACTER,
                              (random.nextBoolean() ? '1' : '0'));
    }
}

class RegexTreeNode {
    
    private RegexToken regexToken;
    private RegexTreeNode leftRegexTreeNode;
    private RegexTreeNode rightRegexTreeNode;
    
    RegexToken getRegexToken() {
        return regexToken;
    }
    
    RegexTreeNode getLeftRegexTreeNode() {
        return leftRegexTreeNode;
    }
    
    RegexTreeNode getRightRegexTreeNode() {
        return rightRegexTreeNode;
    }
    
    void setRegexToken(RegexToken regexToken) {
        this.regexToken = regexToken;
    }
    
    void setLeftRegexTreeNode(RegexTreeNode regexTreeNode) {
        this.leftRegexTreeNode = regexTreeNode;
    }
    
    void setRightRegexTreeNode(RegexTreeNode regexTreeNode) {
        this.rightRegexTreeNode = regexTreeNode;
    }
}