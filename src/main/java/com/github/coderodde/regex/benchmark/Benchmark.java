package com.github.coderodde.regex.benchmark;

import java.util.Random;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 27, 2023)
 * @since 1.6 (Nov 27, 2023)
 */
public final class Benchmark {
    
    public static void main(String[] args) {
        long seed = 76555066686700L;//System.nanoTime();
        Random random = new Random(seed);
        
        System.out.println("Seed = " + seed);
        
        RandomBinaryRegexBuilder builder = new RandomBinaryRegexBuilder();
        // (((1|0)?)((1.)+))((0(00))+)
        System.out.println(
                builder.buildRandomBinaryRegularExpression(random, 4));
    }
}
