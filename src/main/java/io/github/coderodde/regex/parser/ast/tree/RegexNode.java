package io.github.coderodde.regex.parser.ast.tree;

/**
 * This interface defines the type of a node in a regular expression AST 
 * (abstract syntax tree).
 */
public sealed interface RegexNode permits UnionRegexNode,
                                          ConcatenationRegexNode, 
                                          KleeneStarRegexNode,
                                          PlusRegexNode,
                                          OptionalRegexNode,
                                          LiteralRegexNode,
                                          DotRegexNode,
                                          CharacterClassRegexNode {
    
}
