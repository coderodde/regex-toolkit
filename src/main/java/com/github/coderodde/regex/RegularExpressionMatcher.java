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
    
    public boolean matches(String text);
}
