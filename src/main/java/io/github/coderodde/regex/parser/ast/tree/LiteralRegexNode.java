package io.github.coderodde.regex.parser.ast.tree;

/**
 * This record defines the node for a literal.
 */
public record LiteralRegexNode(int codePoint) implements RegexNode {
    
    @Override
    public String toString() {
        return "[Literal '" + new String(Character.toChars(codePoint)) + "']";
    }
}
