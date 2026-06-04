package io.github.coderodde.regex;

import io.github.coderodde.regex.parser.ast.tree.CharacterClassRegexNode;
import io.github.coderodde.regex.parser.ast.tree.ConcatenationRegexNode;
import io.github.coderodde.regex.parser.ast.tree.DotRegexNode;
import io.github.coderodde.regex.parser.ast.tree.KleeneStarRegexNode;
import io.github.coderodde.regex.parser.ast.tree.LiteralRegexNode;
import io.github.coderodde.regex.parser.ast.tree.OptionalRegexNode;
import io.github.coderodde.regex.parser.ast.tree.PlusRegexNode;
import io.github.coderodde.regex.parser.ast.tree.RegexNode;
import io.github.coderodde.regex.parser.ast.tree.UnionRegexNode;
import java.util.List;
import java.util.Objects;

public final class NondeterministicFiniteAutomatonCompiler {
    
    private final RegexNode syntaxTreeRoot;
    private int stateId = 0;
    
    public NondeterministicFiniteAutomatonCompiler(RegexNode syntaxTreeRoot) {
        this.syntaxTreeRoot =
            Objects.requireNonNull(
                syntaxTreeRoot, 
                "The abstract syntax tree root is null.");
    }
    
    public NondeterministicFiniteAutomaton compile() {
        Fragment fragment = build(syntaxTreeRoot);
        
        NondeterministicFiniteAutomaton nfa =
            new NondeterministicFiniteAutomaton();
        
        nfa.setInitialState(fragment.start);
        nfa.addAcceptingState(fragment.accept);
        
        return nfa;
    }
    
    private Fragment build(RegexNode node) {
        if (node instanceof LiteralRegexNode n) {
            return literal(n.codePoint());
        }
        
        if (node instanceof DotRegexNode) {
            return dot();
        }
        
        if (node instanceof CharacterClassRegexNode n) {
            return characterClass(n.ranges());
        }
        
        if (node instanceof ConcatenationRegexNode n) {
            return concatenate(build(n.left()), build(n.right()));
        }
        
        if (node instanceof UnionRegexNode n) {
            return union(build(n.left()), build(n.right()));
        }
        
        if (node instanceof KleeneStarRegexNode n) {
            return kleeneStar(build(n.child()));
        } 
        
        if (node instanceof PlusRegexNode n) {
            return plus(build(n.child()));
        }
        
        if (node instanceof OptionalRegexNode n) {
            return optional(build(n.child()));
        }
        
        throw new IllegalArgumentException("Unknown AST node: " + node);
    }
    
    private Fragment literal(int codePoint) {
        NondeterministicFiniteAutomatonState start  = newState();
        NondeterministicFiniteAutomatonState accept = newState();
        
        start.addTransition(codePoint, accept);
        
        return new Fragment(start, accept);
    }
    
    private Fragment dot() {
        NondeterministicFiniteAutomatonState start  = newState();
        NondeterministicFiniteAutomatonState accept = newState();
        
        start.addDotTransition(accept);
        
        return new Fragment(start, accept);
    }
    
    private Fragment characterClass(List<CodePointRange> ranges) {
        NondeterministicFiniteAutomatonState start  = newState();
        NondeterministicFiniteAutomatonState accept = newState();
        
        for (CodePointRange range : ranges) {
            for (Integer codePoint : range) {
                start.addTransition(codePoint, accept);
            }
        }
        
        return new Fragment(start, accept);
    }
    
    private Fragment concatenate(Fragment left, Fragment right) {
        left.accept.addEpsilonTransition(right.start);
        
        return new Fragment(left.start, right.accept);
    }
    
    private Fragment union(Fragment left, Fragment right) {
        NondeterministicFiniteAutomatonState start  = newState();
        NondeterministicFiniteAutomatonState accept = newState();
        
        start.addEpsilonTransition(left.start);
        start.addEpsilonTransition(right.start);
        
        left.accept.addEpsilonTransition(accept);
        right.accept.addEpsilonTransition(accept);
        
        return new Fragment(start, accept);
    }
    
    private Fragment kleeneStar(Fragment fragment) {
        NondeterministicFiniteAutomatonState start  = newState();
        NondeterministicFiniteAutomatonState accept = newState();
        
        start.addEpsilonTransition(fragment.start);
        start.addEpsilonTransition(accept);
        
        fragment.accept.addEpsilonTransition(fragment.start);
        fragment.accept.addEpsilonTransition(accept);
        
        return new Fragment(start, accept);
    }
    
    private Fragment plus(Fragment fragment) {
        NondeterministicFiniteAutomatonState start  = newState();
        NondeterministicFiniteAutomatonState accept = newState();
        
        start.addEpsilonTransition(fragment.start);
        
        fragment.accept.addEpsilonTransition(fragment.start);
        fragment.accept.addEpsilonTransition(accept);
        
        return new Fragment(start, accept);
    }
    
    private Fragment optional(Fragment fragment) {
        NondeterministicFiniteAutomatonState start  = newState();
        NondeterministicFiniteAutomatonState accept = newState();
        
        start.addEpsilonTransition(fragment.start);
        start.addEpsilonTransition(accept);
        
        fragment.accept.addEpsilonTransition(accept);
        
        return new Fragment(start, accept);
    }
    
    private NondeterministicFiniteAutomatonState newState() {
        return new NondeterministicFiniteAutomatonState(stateId++);
    }
}

final class Fragment {
    final NondeterministicFiniteAutomatonState start;
    final NondeterministicFiniteAutomatonState accept;
    
    Fragment(NondeterministicFiniteAutomatonState start,
             NondeterministicFiniteAutomatonState accept) {
        this.start  = start;
        this.accept = accept;
    }
}
