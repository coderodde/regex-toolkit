package com.github.coderodde.regex;

import com.github.coderodde.regex.RegexCharacter.RegexCharacterType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
    
    public static boolean 
        isSyntacticallyCorrectRegularExpression(String regex) {
        
    }
    
    public static List<RegexCharacter> 
        convertPatternToRegexCharacterList(String text) {
        List<RegexCharacter> list = new ArrayList<>();
        
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            
            switch (ch) {
                case '?':
                    list.add(getRegexChar(RegexCharacterType.QUESTION_MARK));
                    break;
                    
                case '*':
                    list.add(getRegexChar(RegexCharacterType.KLEENE_STAR));
                    break;
                    
                case '+':
                    list.add(getRegexChar(RegexCharacterType.PLUS));
                    break;
                    
                case '|':
                    list.add(getRegexChar(RegexCharacterType.CHOICE));
                    break;
                    
                case '(':
                    list.add(getRegexChar(RegexCharacterType.LEFT_PARENTHESIS));
                    break;
                    
                case ')':
                    list.add(
                            getRegexChar(
                                    RegexCharacterType.RIGHT_PARENTHESIS));
                    break;
            }
            
            
        }
        
        return list;
    }
        
    private static RegexCharacter 
        getRegexChar(RegexCharacterType regexCharacterType) {
        return new RegexCharacter(regexCharacterType);
    }
}
