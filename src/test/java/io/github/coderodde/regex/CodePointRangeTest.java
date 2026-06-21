package io.github.coderodde.regex;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

public class CodePointRangeTest {

    @Test
    public void combine() {
        CodePointRange cpr1 = new CodePointRange('a', 'f');
        CodePointRange cpr2 = new CodePointRange('d');
        CodePointRange cpr3 = new CodePointRange('b');
        CodePointRange cpr4 = new CodePointRange(true, 'x', 'y');
        
        CodePointRange[] combined = CodePointRange.combine(cpr1,
                                                           cpr2,
                                                           cpr3,
                                                           cpr4);
        
        System.out.println(Arrays.toString(combined));
        
        assertEquals(1, combined.length);
        
        assertEquals(cpr4, combined[0]);
    }
    
    @Test
    public void includes1() {
        CodePointRange r1 = new CodePointRange(true, 'c', 'f');
        CodePointRange r2 = new CodePointRange(true, 'a', 'g');
        assertTrue(r1.includes(r2));
    }
    
    @Test
    public void includes2() {
        CodePointRange r1 = new CodePointRange(true, 'c', 'f');
        CodePointRange r2 = new CodePointRange(true, 'd', 'g');
        assertFalse(r1.includes(r2));
    }
    
    @Test
    public void includes3() {
        CodePointRange r1 = new CodePointRange(true, 'c', 'g');
        CodePointRange r2 = new CodePointRange(true, 'a', 'f');
        assertFalse(r1.includes(r2));
    }
    
    @Test
    public void includes4() {
        CodePointRange r1 = new CodePointRange('a', 'c');
        CodePointRange r2 = new CodePointRange(true, 'a', 'f');
        assertFalse(r1.includes(r2));
    }
    
    @Test
    public void includes5() {
        CodePointRange r1 = new CodePointRange('b', 'c');
        CodePointRange r2 = new CodePointRange('a', 'c');
        assertFalse(r1.includes(r2));
    }
    
    @Test
    public void includes6() {
        CodePointRange r1 = new CodePointRange('a', 'd');
        CodePointRange r2 = new CodePointRange('b', 'c');
        assertTrue(r1.includes(r2));
    }
    
    @Test
    public void includes7() {
        CodePointRange r1 = new CodePointRange(true, 'd', 'f');
        CodePointRange r2 = new CodePointRange('b', 'c');
        assertTrue(r1.includes(r2));
    }
    
    @Test
    public void includes8() {
        CodePointRange r1 = new CodePointRange(true, 'd', 'f');
        CodePointRange r2 = new CodePointRange('g', 'm');
        assertTrue(r1.includes(r2));
    }
    
    @Test
    public void includes9() {
        CodePointRange r1 = new CodePointRange(true, 'd', 'f');
        CodePointRange r2 = new CodePointRange('f', 'm');
        assertFalse(r1.includes(r2));
    }
    
    @Test
    public void includes10() {
        CodePointRange r1 = new CodePointRange(true, 'd', 'f');
        CodePointRange r2 = new CodePointRange('a', 'e');
        assertFalse(r1.includes(r2));
    }
    
    @Test
    public void includes11() {
        CodePointRange r1 = new CodePointRange(true, 'd', 'f');
        CodePointRange r2 = new CodePointRange('d', 'f');
        assertFalse(r1.includes(r2));
    }
    
    @Test
    public void includes12() {
        CodePointRange ths = new CodePointRange(true, 'd', 'f');
        CodePointRange other = new CodePointRange('d', 'g');
        assertFalse(ths.includes(other));
    }
    
    @Test
    public void includes13() {
        CodePointRange ths = new CodePointRange(true, 'd', 'f');
        CodePointRange other = new CodePointRange('g', 'h');
        assertTrue(ths.includes(other));
    }
    
    @Test
    public void includes14() {
        CodePointRange ths = new CodePointRange(true, 'd', 'f');
        CodePointRange other = new CodePointRange('g', 'h');
        assertTrue(ths.includes(other));
    }
    
    @Test
    public void includes15() {
        CodePointRange ths = new CodePointRange('d', 'f');
        CodePointRange other = new CodePointRange(true, 'h', 'm');
        assertTrue(ths.includes(other));
    }
    
    @Test
    public void includes16() {
        CodePointRange ths = new CodePointRange('n', 'p');
        CodePointRange other = new CodePointRange(true, 'h', 'm');
        assertTrue(ths.includes(other));
    }
}
