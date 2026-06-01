package io.github.coderodde.regex.parser.ast.tree;

import io.github.coderodde.regex.CodePointRange;
import java.util.List;

/**
 * This record defines the node the character classes (i.e., "[a-d]").
 */
public record CharacterClassRegexNode(List<CodePointRange> ranges) 
implements RegexNode {
    
}
