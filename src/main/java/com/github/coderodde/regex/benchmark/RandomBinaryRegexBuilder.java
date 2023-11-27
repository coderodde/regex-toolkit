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
        RegexTreeNode root = 
                new RegexTreeNode(getDoubleParameterRegexToken(random));
        
        constructRegexFromTreeImpl(random,
                                   root,
                                   0, 
                                   depth);
        
        return buildRegexString(root);
    }
    
    private static String buildRegexString(RegexTreeNode root) {
        if (root == null) {
            return "";
        }
        
        if (root.isLeaf()) {
            if (root.getRegexToken()
                    .getTokenType()
                    .equals(RegexTokenType.CHARACTER)) {
                
                return Character.toString(root.getRegexToken().getCharacter());
            } else if (root.getRegexToken()
                           .getTokenType()
                           .equals(RegexTokenType.DOT)) {
                
                return ".";
            } else {
                throw new IllegalStateException("Should not get here.");
            }
        }
        
        String leftSubtreeString =
                buildRegexString(root.getLeftRegexTreeNode());
        
        String rightSubtreeString = 
                buildRegexString(root.getRightRegexTreeNode());
        
        switch (root.getRegexToken().getTokenType()) {
            case UNION:
                if (leftSubtreeString.length() > 1) {
                    leftSubtreeString = "(" + leftSubtreeString + ")";
                }

                if (rightSubtreeString.length() > 1) {
                    rightSubtreeString = "(" + rightSubtreeString + ")";
                }
                
                return leftSubtreeString + "|" + rightSubtreeString;
                
            case CONCAT:
                return leftSubtreeString + rightSubtreeString;
                
            case KLEENE_STAR:
                return leftSubtreeString + "*" + rightSubtreeString;
                
            case PLUS:
                return leftSubtreeString + "+" + rightSubtreeString;
                
            case QUESTION:
                return leftSubtreeString + "?" + rightSubtreeString;
               
            default:
                throw new IllegalStateException("Should not get here.");
        }
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
        RegexTreeNode leftRegexTreeNode = 
                new RegexTreeNode(new RegexToken(regexTokenType));
        
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
        RegexTreeNode leftRegexTreeNode = 
                new RegexTreeNode(new RegexToken(regexTokenType));
        
        root.setLeftRegexTreeNode(leftRegexTreeNode);
        constructRegexFromTreeImpl(random,
                                   leftRegexTreeNode,
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
    
    private static RegexToken getSingleParameterRegexToken(Random random) {
        double coin = random.nextDouble();
        
        if (coin < 0.2) {
            return new RegexToken(RegexTokenType.QUESTION);
        } else if (coin < 0.6) {
            return new RegexToken(RegexTokenType.PLUS);
        } else {
            return new RegexToken(RegexTokenType.KLEENE_STAR);
        }
    }
}

class RegexTreeNode {
    
    private RegexToken regexToken;
    private RegexTreeNode leftRegexTreeNode;
    private RegexTreeNode rightRegexTreeNode;
    
    RegexTreeNode(RegexToken regexToken) {
        this.regexToken = regexToken;
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