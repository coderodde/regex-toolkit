package io.github.coderodde.regex.parser.ast.tree;

/**
 * This record defines the node for the Kleene star operator.
 */
public record KleeneStarRegexNode(RegexNode child) implements RegexNode {
    
}
