package com.github.coderodde.regex;

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
    }
}
