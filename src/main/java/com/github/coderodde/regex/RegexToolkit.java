package com.github.coderodde.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

/**
 *
 * @author rodio
 */
public class RegexToolkit {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        Pattern p = Pattern.compile("(ab)?");
        System.out.println(p.matcher("").matches());
        System.out.println(p.matcher("ab").matches());
        System.out.println(p.matcher("b").matches());
        
        List<Integer> l = new ArrayList<>(Arrays.asList(1, 10));
        
        ListIterator<Integer> i = l.listIterator();
        
        System.out.println(i.next());
        i.add(2);
        i.add(3);
        System.out.println(i.next());
    }
}
