//package com.github.coderodde.regex;
//
//import java.util.ArrayDeque;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Deque;
//import java.util.List;
//import java.util.Objects;
//
///**
// *
// * @author rodio
// * @version 1.6 (Nov 12, 2023)
// * @since 1.6 (Nov 12, 2023)
// */
//public final class RegularExpressionToNFAConverter {
//    
//    private final String regularExpression;
//    
//    public RegularExpressionToNFAConverter(String regularExpression) {
//        this.regularExpression = 
//                Objects.requireNonNull(
//                        regularExpression, 
//                        "The input regular expression is null.");
//        
//        Utils.validateRegularExpressionParentheses(regularExpression);
//    }
//    
//    public NondeterministicFiniteAutomaton convert() {
//        List<RegexCharacter> regexCharacters = 
//                convertImpl(0, regularExpression.length());
//        
//        System.out.println("yeah: " + regexCharacters);
//        
//        return null;
//    }
//    
//    private List<RegexCharacter> convertImpl(int startIndex, int toIndex) {
//        List<RegexCharacter> list = new ArrayList<>();
//        boolean previousCharacterWasLetter = false;
//        
//        for (int i = startIndex; i < toIndex; i++) {
//            char ch = regularExpression.charAt(i);
//            
//            switch (ch) {
//                case '?':
//                    list.addAll(processQuestionMark(i));
//                    previousCharacterWasLetter = false;
//                    break;
//                    
//                case '*':
//                    list.addAll(processKleeneStar(i));
//                    previousCharacterWasLetter = false;
//                    break;
//                    
//                case '+':
////                    list.add(getRegexChar(RegexCharacter.RegexCharacterType.PLUS));
//                    previousCharacterWasLetter = false;
//                    break;
//                    
//                case '|':
////                    list.add(getRegexChar(RegexCharacter.RegexCharacterType.CHOICE));
//                    previousCharacterWasLetter = false;
//                    break;
//                    
//                case '(':
//                    list.add(getRegexChar(RegexCharacter.RegexCharacterType.LEFT_PARENTHESIS));
//                    previousCharacterWasLetter = false;
//                    break;
//                    
//                case ')':
//                    list.add(
//                            getRegexChar(
//                                    RegexCharacter.RegexCharacterType.RIGHT_PARENTHESIS));
//                    
//                    previousCharacterWasLetter = false;
//                    break;
//                    
//                case '\0':
//                    list.add(getRegexChar(RegexCharacter.RegexCharacterType.EPSILON));
//                    previousCharacterWasLetter = false;
//                    // What??
//                    break;
//                    
//                default:
//                    RegexCharacter regexCharacter = 
//                            new RegexCharacter(RegexCharacter.RegexCharacterType.CHARACTER);
//                    
//                    regexCharacter.setCharacter(ch);
//                    list.add(regexCharacter);
//                    previousCharacterWasLetter = true;
//                    break;
//            }
//            
//            if (previousCharacterWasLetter) {
//                list.add(new RegexCharacter(RegexCharacter.RegexCharacterType.CONCATENATION));
//            }
//        }
//        
//        return list;
//    }
//    
//    private List<RegexCharacter> processQuestionMark(int i) {
//            
//        if (i == 0) {
//            throw new IllegalStateException(
//                    "The ? operator cannot be the first character in a " + 
//                    "regular expression.");
//        }
//        
//        char preceedingCharacter = regularExpression.charAt(i - 1);
//        List<RegexCharacter> ret = new ArrayList<>();
//        
//        if (preceedingCharacter == ')') {
//            ret.addAll(getQuestionSubstring(i));
//        } else {
//            ret.addAll(getQuestionCharacters(preceedingCharacter));
//        }
//        
//        return ret;
//    }
//    
//    private List<RegexCharacter> processKleeneStar(int i) {
//        return new ArrayList<>();
//    }
//    
//    private List<RegexCharacter> getQuestionSubstring(int questionMarkIndex) {
//        Deque<Character> stack = new ArrayDeque<>();
//        int j = questionMarkIndex - 1;
//        
//        for (; j >= 0; j--) {
//            char ch = regularExpression.charAt(j);
//            
//            if (ch == ')') {
//                stack.addLast(ch);
//            } else if (ch == '(') {
//                stack.removeLast();
//                
//                if (stack.isEmpty()) {
//                    break;
//                }
//            }
//        }
//        
//        if (j == questionMarkIndex - 1) {
//            return Collections.<RegexCharacter>emptyList();
//        }
//        
//        return convertImpl(j, questionMarkIndex - 1);
//    }
//        
//    private static RegexCharacter 
//        getRegexChar(RegexCharacter.RegexCharacterType regexCharacterType) {
//        return new RegexCharacter(regexCharacterType);
//    }
//            
//    private static List<RegexCharacter> 
//        getQuestionCharacters(char preceedingCharacter) {
//        RegexCharacter leftParenthesis = 
//                new RegexCharacter(RegexCharacter.RegexCharacterType.LEFT_PARENTHESIS);
//        
//        RegexCharacter rightParenthesis = 
//                new RegexCharacter(RegexCharacter.RegexCharacterType.RIGHT_PARENTHESIS);
//        
//        RegexCharacter epsilon = new RegexCharacter(RegexCharacter.RegexCharacterType.EPSILON);
//        RegexCharacter choice = new RegexCharacter(RegexCharacter.RegexCharacterType.CHOICE);
//        RegexCharacter letter = 
//                new RegexCharacter(RegexCharacter.RegexCharacterType.CHARACTER);
//        
//        letter.setCharacter(preceedingCharacter);
//        
//        return new ArrayList<>(
//                Arrays.asList(
//                        leftParenthesis,
//                        epsilon, 
//                        choice, 
//                        letter, 
//                        rightParenthesis));
//    }
//        
//    public static void main(String[] args) {
//        RegularExpressionToNFAConverter converter =
//                new RegularExpressionToNFAConverter("a?");
//        
//        converter.convert();
//    }
//}
