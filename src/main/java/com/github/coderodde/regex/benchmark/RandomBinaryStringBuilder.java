package com.github.coderodde.regex.benchmark;

import java.util.Random;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 26, 2023)
 * @since 1.6 (Nov 26, 2023)
 */
public final class RandomBinaryStringBuilder {
    
    public static String getRandomBinaryString(int length, Random random) {
        StringBuilder sb = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            sb.append((random.nextBoolean() ? '1' : '0'));
        }
        
        return sb.toString();
    }
}
