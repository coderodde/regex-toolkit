package com.github.coderodde.regex;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 26, 2023)
 * @since 1.6 (Nov 26, 2023)
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
}
