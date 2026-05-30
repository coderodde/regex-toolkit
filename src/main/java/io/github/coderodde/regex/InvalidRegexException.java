package io.github.coderodde.regex;

/**
 * This class defines the exception type instances of which are thrown when the
 * regular expressions contain invalid parenthesation.
 */
public final class InvalidRegexException extends RuntimeException {
    
    public InvalidRegexException() {
        super();
    }
    
    public InvalidRegexException(String exceptionMessage) {
        super(exceptionMessage);
    }
}
