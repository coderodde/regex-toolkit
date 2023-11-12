package com.github.coderodde.regex;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author PotilasKone
 * @version 1.6 (Nov 12, 2023)
 * @since 1.6 (Nov 12, 2023)
 */
public final class Utils {
    
    public static <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        Set<T> set = new HashSet<>(Math.max(set1.size(), set2.size()));
        Set<T> largerSet  = set1.size() > set2.size() ? set1 : set2;
        Set<T> smallerSet = set1.size() <= set2.size() ? set1 : set2;
        
        for (T element : smallerSet) {
            if (largerSet.contains(element)) {
                set.add(element);
            }
        }
        
        return set;
    }
}
