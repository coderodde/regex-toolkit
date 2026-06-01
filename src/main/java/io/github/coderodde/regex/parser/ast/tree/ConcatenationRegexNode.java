package io.github.coderodde.regex.parser.ast.tree;

/**
 * This record defines the node for the concatenation operator.
 */
public record ConcatenationRegexNode(RegexNode left, RegexNode right) 
        implements RegexNode {
    
}
