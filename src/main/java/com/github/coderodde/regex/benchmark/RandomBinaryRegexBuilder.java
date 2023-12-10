package com.github.coderodde.regex.benchmark;

import com.github.coderodde.regex.RegexToken;
import com.github.coderodde.regex.RegexTokenType;
import static com.github.coderodde.regex.RegexTokenType.CHARACTER;
import static com.github.coderodde.regex.RegexTokenType.CONCAT;
import static com.github.coderodde.regex.RegexTokenType.DOT;
import static com.github.coderodde.regex.RegexTokenType.KLEENE_STAR;
import static com.github.coderodde.regex.RegexTokenType.PLUS;
import static com.github.coderodde.regex.RegexTokenType.QUESTION;
import static com.github.coderodde.regex.RegexTokenType.UNION;
import java.util.Random;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 26, 2023)
 * @since 1.6 (Nov 26, 2023)
 */
final class RandomBinaryRegexBuilder {
    
    private final Random random;
    
    RandomBinaryRegexBuilder(Random random) {
        this.random = random;
    }
    
    RegexTreeNode buildRandomBinaryRegularExpression(int depth) {
        return buildRandomRegexTree(depth);
    }
    
    RegexTreeNode buildRandomRegexTree(int depth) {
        RegexTreeNode root = 
                new RegexTreeNode(getDoubleParameterRegexToken(random));
        
        constructRegexFromTreeImpl(random,
                                   root,
                                   0, 
                                   depth);
        
        return root;
    }
    
    String buildRegexString(RegexTreeNode root) {
        if (root == null) {
            return "";
        }
        
        if (root.isLeaf()) {
            switch (root.getRegexToken().getTokenType()) {
                case CHARACTER -> {
                    return Character.toString(
                            root.getRegexToken()
                                .getCharacter());
                }
                
                case DOT -> {
                    return ".";
                }
                    
                default -> 
                    throw new IllegalStateException("Should not get here.");
            }
        }
        
        if (root.getRightRegexTreeNode() == null) {
            StringBuilder sb = new StringBuilder();
            String leftRegex = buildRegexString(root.getLeftRegexTreeNode());
            leftRegex = parenthesizeRegex(leftRegex);
            sb.append(leftRegex);
            
            switch (root.getRegexToken().getTokenType()) {
                case KLEENE_STAR:
                    sb.append("*");
                    break;
                    
                case PLUS:
                    sb.append("+");
                    break;
                    
                case QUESTION:
                    sb.append("?");
                    break;
                    
                default:
                    throw new IllegalStateException("Should not get here.");
            }
            
            return sb.toString();
        }
        
        String leftSubtreeString =
                buildRegexString(root.getLeftRegexTreeNode());
        
        String rightSubtreeString = 
                buildRegexString(root.getRightRegexTreeNode());
        
        leftSubtreeString  = parenthesizeRegex(leftSubtreeString);
        rightSubtreeString = parenthesizeRegex(rightSubtreeString);
        
        switch (root.getRegexToken().getTokenType()) {
            case UNION:
                if (leftSubtreeString.equals(rightSubtreeString)) {
                    // The regex is of the form R|R, return only R:
                    return leftSubtreeString;
                } else {
                    return leftSubtreeString + "|" + rightSubtreeString;
                }
                
            case CONCAT:
                return leftSubtreeString + rightSubtreeString;
               
            default:
                throw new IllegalStateException("Should not get here.");
        }
    }
    
    
    String buildRandomAcceptingText(RegexTreeNode root ){
        return buildRandomAcceptingTextImpl(random, root);
    }
    
    private static String 
        buildRandomAcceptingTextImpl(Random random,RegexTreeNode root) {
            
        if (root == null) {
            return "";
        }
        
        switch (root.getRegexToken().getTokenType()) {
            case QUESTION:
                return buildRandomAcceptingTextImplQuestion(random, root);
                
            case UNION:
                return buildRandomAcceptingTextImplUnion(random, root);
                
            case DOT:
                return getRandomBinaryCharacter(random);
                
            case CHARACTER:
                return Character.toString(root.getRegexToken().getCharacter());
                
            case CONCAT:
                return buildRandomAcceptingTextConcat(random, root);
                
            case KLEENE_STAR:
                return buildRandomAcceptingTextKleeneStar(random, root);
                
            case PLUS:
                return buildRandomAcceptingTextPlusOperator(random, root);
                
            default:
                throw new IllegalStateException("Should not get here.");
        }
    }
    
    private static String 
        buildRandomAcceptingTextPlusOperator(Random random, 
                                             RegexTreeNode root) {
            
        int repetitions = random.nextInt(9) + 1; // ... + 1 so that at least
                                                 // one iteration.
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i != repetitions; i++) {
            String subRegex = 
                    buildRandomAcceptingTextImpl(random,
                                                 root.getLeftRegexTreeNode());
            sb.append(subRegex);
        }
        
        return sb.toString();
    }
    
    
    private static String 
        buildRandomAcceptingTextKleeneStar(Random random, 
                                           RegexTreeNode root) {
        int repetitions = random.nextInt(10);
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i != repetitions; i++) {
            String subRegex = 
                    buildRandomAcceptingTextImpl(random,
                                                 root.getLeftRegexTreeNode());
            sb.append(subRegex);
        }
        
        return sb.toString();
    }
    
    private static String buildRandomAcceptingTextConcat(Random random, 
                                                         RegexTreeNode root) {
        return buildRandomAcceptingTextImpl(random, root.getLeftRegexTreeNode()) 
             + buildRandomAcceptingTextImpl(random, 
                                            root.getRightRegexTreeNode());
    }
    
    private static String 
        buildRandomAcceptingTextImplQuestion(Random random, 
                                             RegexTreeNode root) {
        double coin = random.nextDouble();
        
        if (coin < 0.8) {
            return buildRandomAcceptingTextImpl(random, 
                                                root.getLeftRegexTreeNode());
        } else {
            return "";
        }
    }
        
    private static String
         buildRandomAcceptingTextImplUnion(Random random,
                                           RegexTreeNode root) {
        String leftRegex = 
                buildRandomAcceptingTextImpl(random, 
                                             root.getLeftRegexTreeNode());
        
        double coin = random.nextInt();
        
        if (coin < 0.5) {
            return leftRegex;
        }
        
        return buildRandomAcceptingTextImpl(random, 
                                            root.getRightRegexTreeNode());
    }
    
    private static void constructRegexFromTreeImpl(Random random,
                                                   RegexTreeNode root, 
                                                   int currentDepth,
                                                   int maximumDepth) {
        if (currentDepth == maximumDepth - 1) {
            RegexTreeNode leftLeafRegexTreeNode  = getRegexTreeNodeLeaf(random);
            RegexTreeNode rightLeafRegexTreeNode = getRegexTreeNodeLeaf(random);
            
            RegexToken rootRegexToken = 
                    new RegexToken(
                            (random.nextDouble() < 0.3 ? 
                                    RegexTokenType.UNION : 
                                    RegexTokenType.CONCAT));
            
            root.setRegexToken(rootRegexToken);
            root.setLeftRegexTreeNode(leftLeafRegexTreeNode);
            root.setRightRegexTreeNode(rightLeafRegexTreeNode);
            return;
        }
        
        int numberOfOperands = getRandomNumberOfOperands(random);
        
        switch (numberOfOperands) {
            case 1:
                constructRegexFromTreeImplSingleOperand(random, 
                                                        root,
                                                        currentDepth, 
                                                        maximumDepth);
                return;
                
            case 2:
                constructRegexFromTreeImplDoubleOperand(random, 
                                                        root, 
                                                        currentDepth, 
                                                        maximumDepth);
                return;
                
            default:
                throw new IllegalStateException("Should not get here.");
        }
    }
    
    private static void constructRegexFromTreeImplSingleOperand(
            Random random, 
            RegexTreeNode root, 
            int currentDepth, 
            int maximumDepth) {
        
        if (currentDepth == maximumDepth) {
            return;
        }
        
        RegexTokenType regexTokenType = getSingleOperandRegexTreeType(random);
        root.setRegexToken(new RegexToken(regexTokenType));
        
        RegexTreeNode leftRegexTreeNode = new RegexTreeNode();
        
        root.setLeftRegexTreeNode(leftRegexTreeNode);
        
        constructRegexFromTreeImpl(random,
                                   leftRegexTreeNode,
                                   currentDepth + 1,
                                   maximumDepth);
    }
    
    private static void constructRegexFromTreeImplDoubleOperand(
            Random random, 
            RegexTreeNode root, 
            int currentDepth, 
            int maximumDepth) {
        
        if (currentDepth == maximumDepth) {
            return;
        }
        
        RegexTokenType regexTokenType = getDoubleOperandRegexTreeType(random);
        root.setRegexToken(new RegexToken(regexTokenType));
        
        RegexTreeNode leftRegexTreeNode = new RegexTreeNode();
        
        root.setLeftRegexTreeNode(leftRegexTreeNode);
        
        constructRegexFromTreeImpl(random,
                                   leftRegexTreeNode,
                                   currentDepth + 1,
                                   maximumDepth);
        
        RegexTreeNode rightRegexTreeNode = new RegexTreeNode();
        
        root.setRightRegexTreeNode(rightRegexTreeNode);
        
        constructRegexFromTreeImpl(random, 
                                   rightRegexTreeNode, 
                                   currentDepth + 1, 
                                   maximumDepth);
    }
    
    private static RegexTokenType getSingleOperandRegexTreeType(Random random) {
        double coin = random.nextDouble();
        
        if (coin < 0.3) {
            return RegexTokenType.QUESTION;
        } else if (coin < 0.7) {
            return RegexTokenType.PLUS;
        } else {
            return RegexTokenType.KLEENE_STAR;
        }
    }
    
    private static RegexTokenType getDoubleOperandRegexTreeType(Random random) {
        double coin = random.nextDouble();
        
        if (coin < 0.65) {
            return RegexTokenType.CONCAT;
        } else {
            return RegexTokenType.UNION;
        }
    }
    
    private static RegexToken getRandomBitRegexToken(Random random) {
        return new RegexToken(RegexTokenType.CHARACTER,
                              (random.nextBoolean() ? '1' : '0'));
    }
    
    private static RegexTreeNode getRegexTreeNodeLeaf(Random random) {
        RegexToken regexToken;
        
        if (random.nextDouble() < 0.35) {
            regexToken = new RegexToken(RegexTokenType.DOT);
        } else {
            regexToken = getRandomBitRegexToken(random);
        }
        
        return new RegexTreeNode(regexToken);
    }
    
    private static int getRandomNumberOfOperands(Random random) {
        return random.nextInt(2) + 1;
    }
    
    private static RegexToken getDoubleParameterRegexToken(Random random) {
        double coin = random.nextDouble();
        
        if (coin < 0.4) {
            return new RegexToken(RegexTokenType.CONCAT);
        } else {
            return new RegexToken(RegexTokenType.UNION);
        }
    }
    
    private static String parenthesizeRegex(String regex) {
        if (regex.length() > 1) {
            return "(" + regex + ")";
        } else {
            return regex;
        }
    }
    
    private static String getRandomBinaryCharacter(Random random) {
        return random.nextBoolean() ? "1" : "0";
    }
}

class RegexTreeNode {
    
    private RegexToken regexToken;
    private RegexTreeNode leftRegexTreeNode;
    private RegexTreeNode rightRegexTreeNode;
    
    RegexTreeNode(RegexToken regexToken) {
        this.regexToken = regexToken;
    }
    
    RegexTreeNode() {
        this(null);
    }
    
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
    
    boolean isLeaf() {
        return leftRegexTreeNode == null && rightRegexTreeNode == null;
    }
}