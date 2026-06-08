package io.github.coderodde.regex;

/**
 * This interface defines the API for regular expression matchers.
 */
public sealed interface RegularExpressionMatcher 
        permits DeterministicFiniteAutomaton, 
                NondeterministicFiniteAutomaton {
    
    /**
     * Tests the input {@code text} against a regular expression matcher 
     * {@code M} and returns {@code true} if and only if {@code text} belongs to
     * the regular language recognized by {@code M}.
     * 
     * @param text the text to test.
     * @return {@code true} if and only if {@code M} accepts {@code text}.
     */
    public boolean matches(String text);
    
    /**
     * Tests whether the input text contains a pattern recognizable by this 
     * matcher.
     * 
     * @param text the text to search for.
     * @return {@code true} if the input text contains a pattern recognizable by 
     *         the matcher.
     */
    public boolean find(String text);
}
