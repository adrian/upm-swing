package com._17od.upm.util;

import junit.framework.TestCase;

public class ValidatorTest extends TestCase {

    private static final String errorMsg = "There is a problem with the provided argument";
    
    public void testTrueConditionDoesntThrowError() {
        Validator.checkArgument(5 > 2, errorMsg);
    }
    
    public void testFalseConditionThrowsError() {
        try {
            Validator.checkArgument(-1 > 2, errorMsg);
            fail("No error was thrown, but expected IllegalArg");
        } catch (IllegalArgumentException e) {
            // Do nothing, it was thrown as expected
        }
    }
    
    public void testNonNullDoesntThrowError() {
        Validator.checkNotNull("I'm not null", errorMsg);
    }
    
    public void testNullThrowsError() {
        try {
            Validator.checkNotNull(null, errorMsg);
            fail("No error was thrown, but expected IllegalArg");
        } catch (IllegalArgumentException e) {
            // Do nothing, it was thrown as expected
        }
    }
}
