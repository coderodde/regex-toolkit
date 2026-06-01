package io.github.coderodde.regex.parser.ast.tree;

/**
 * This record defines the node for the optional ("?") operator.
 */
public record OptionalRegexNode(RegexNode child) implements RegexNode {
    
}
