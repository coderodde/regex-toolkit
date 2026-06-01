package io.github.coderodde.regex.parser.ast.tokens;

import io.github.coderodde.regex.CodePointRange;
import java.util.List;

/**
 * This class defines regex tokens representing character classes.
 */
public record RegexTokenCharacterClass(List<CodePointRange> ranges) 
implements RegexToken {
    
}
