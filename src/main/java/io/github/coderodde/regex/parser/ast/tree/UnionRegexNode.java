package io.github.coderodde.regex.parser.ast.tree;

/**
 * This record defines the node for the union operator.
 */
public record UnionRegexNode(RegexNode left, RegexNode right) 
implements RegexNode {
    
}
