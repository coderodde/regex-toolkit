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
}
