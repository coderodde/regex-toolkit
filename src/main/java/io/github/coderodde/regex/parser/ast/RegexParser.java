package io.github.coderodde.regex.parser.ast;

import io.github.coderodde.regex.parser.ast.tree.RegexNode;
import io.github.coderodde.regex.parser.ast.tree.OptionalRegexNode;
import io.github.coderodde.regex.parser.ast.tree.DotRegexNode;
import io.github.coderodde.regex.parser.ast.tree.CharacterClassRegexNode;
import io.github.coderodde.regex.parser.ast.tree.UnionRegexNode;
import io.github.coderodde.regex.parser.ast.tree.PlusRegexNode;
import io.github.coderodde.regex.parser.ast.tree.KleeneStarRegexNode;
import io.github.coderodde.regex.parser.ast.tree.ConcatenationRegexNode;
import io.github.coderodde.regex.InvalidRegexException;
import io.github.coderodde.regex.parser.ast.tokens.RegexToken;
import io.github.coderodde.regex.parser.ast.tree.LiteralRegexNode;
import java.util.List;

/**
 * This class implements building abstract syntax trees (AST, for short) for 
 * regular expressions.
 */
public final class RegexParser {
    
    private final List<RegexToken> tokens;
    private int index;
    
    public RegexParser(List<RegexToken> tokens) {
        this.tokens = tokens;
    }
    
    public RegexNode parse() {
        RegexNode node = parseUnion();
        
        if (!isAtEnd()) {
            throw new InvalidRegexException("Unexpected token: " + peek());
        }
        
        return node;
    }
    
    private RegexNode parseUnion() {
        RegexNode left = parseConcat();
        
        while (match(RegexTokenType.UNION)) {
            RegexNode right = parseConcat();
            left = new UnionRegexNode(left, right);
        }
        
        return left;
    }
    
    private RegexNode parseConcat() {
        RegexNode left = parsePostfix();
        
        while (match(RegexTokenType.CONCATENATION)) {
            RegexNode right = parsePostfix();
            left = new ConcatenationRegexNode(left, right);
        }
        
        return left;
    }
    
    private RegexNode parsePostfix() {
        RegexNode node = parseAtom();
        
        while (true) {
            if (match(RegexTokenType.KLEENE_STAR)) {
                node = new KleeneStarRegexNode(node);
            } else if (match(RegexTokenType.PLUS)) {
                node = new PlusRegexNode(node);
            } else if (match(RegexTokenType.QUESTION)) {
                node = new OptionalRegexNode(node);
            } else {
                return node;
            }
        }
    }
    
    private RegexNode parseAtom() {
        if (match(RegexTokenType.LITERAL)) {
            return new LiteralRegexNode(previous().codePoint());
        }
        
        if (match(RegexTokenType.DOT)) {
            return new DotRegexNode();
        }
        
        if (match(RegexTokenType.CHARACTER_CLASS)) {
            return new CharacterClassRegexNode(previous().ranges());
        }
        
        if (match(RegexTokenType.LEFT_PARENTHESIS)) {
            RegexNode node = parseUnion();
            
            if (!match(RegexTokenType.RIGHT_PARENTHESIS)) {
                throw new InvalidRegexException("Missing ')'.");
            }
            
            return node;
        }
        
        throw new InvalidRegexException("Expected atom, got: " + peek());
    }
    
    private boolean startsAtom(RegexTokenType type) {
        return type == RegexTokenType.LITERAL
            || type == RegexTokenType.DOT
            || type == RegexTokenType.CHARACTER_CLASS
            || type == RegexTokenType.LEFT_PARENTHESIS;
    }
    
    private boolean match(RegexTokenType type) {
        if (peekType() != type) {
            return false;
        }
        
        ++index;
        return true;
    }
    
    private RegexToken previous() {
        return tokens.get(index - 1);
    }
    
    private RegexToken peek() {
        return tokens.get(index);
    }
    
    private RegexTokenType peekType() {
        if (isAtEnd()) {
            return RegexTokenType.EOF;
        }
        
        return peek().type();
    }
    
    private boolean isAtEnd() {
        return index >= tokens.size() ||
               tokens.get(index).type() == RegexTokenType.EOF;
    }
}
