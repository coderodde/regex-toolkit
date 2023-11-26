package com.github.coderodde.regex;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 25, 2023)
 * @since 1.6 (Nov 25, 2023)
 */
final class GeneralizedNondeterministicFiniteAutomatonState {
    
    private final int id;
    
    final Map<GeneralizedNondeterministicFiniteAutomatonState, String> map = 
            new HashMap<>();

    GeneralizedNondeterministicFiniteAutomatonState(int id) {
        this.id = id;
    }
    
    void addRegularExpression(
            GeneralizedNondeterministicFiniteAutomatonState followerState, 
            String regularExpression) {
        
        map.put(followerState, regularExpression);
    }
   
    String getRegularExpression(
            GeneralizedNondeterministicFiniteAutomatonState state) {
        return map.get(state);
    }
}
