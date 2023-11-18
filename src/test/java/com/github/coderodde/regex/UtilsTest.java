package com.github.coderodde.regex;

import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class UtilsTest {
    
    @Test(expected = InvalidRegexException.class)
    public void throwsOnValidateRegularExpressionParentheses1() {
        Utils.validateRegularExpressionParentheses("(");
    }
    
    @Test(expected = InvalidRegexException.class)
    public void throwsOnValidateRegularExpressionParentheses2() {
        Utils.validateRegularExpressionParentheses("((");
    }
    
    @Test(expected = InvalidRegexException.class)
    public void throwsOnValidateRegularExpressionParentheses3() {
        Utils.validateRegularExpressionParentheses(")(");
    }
    
    @Test(expected = InvalidRegexException.class)
    public void throwsOnValidateRegularExpressionParentheses4() {
        Utils.validateRegularExpressionParentheses(")a(");
    }
    
    @Test()
    public void validateValidRegularExpressionParentheses1() {
        Utils.validateRegularExpressionParentheses("");
    }
    
    @Test()
    public void validateValidRegularExpressionParentheses2() {
        Utils.validateRegularExpressionParentheses("()");
    }
    
    @Test()
    public void validateValidRegularExpressionParentheses3() {
        Utils.validateRegularExpressionParentheses("(())");
    }
    
    @Test()
    public void validateValidRegularExpressionParentheses4() {
        Utils.validateRegularExpressionParentheses("(()())");
    }
    
    @Test()
    public void validateValidRegularExpressionParentheses5() {
        Utils.validateRegularExpressionParentheses("((a)bc(d)e)");
    }
}
