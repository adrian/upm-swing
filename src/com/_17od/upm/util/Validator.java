/*
 * Universal Password Manager
 * Copyright (C) 2005-2013 Adrian Smith
 *
 * This file is part of Universal Password Manager.
 *   
 * Universal Password Manager is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Universal Password Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Universal Password Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com._17od.upm.util;

/**
 * Validator is inspired by Google Guava as linked below and is used in lieu of
 * adding the full Google Guava library package to this project.
 * 
 * https://code.google.com/p/guava-libraries/wiki/PreconditionsExplained
 * 
 * This class helps to add precondition argument checks to public methods to
 * avoid Null as much as possible as also inspired by Google Guava here:
 * 
 * https://code.google.com/p/guava-libraries/wiki/UsingAndAvoidingNullExplained
 * 
 * @author markyv
 * @version 0.0.4
 */
public class Validator {

    private Validator() {
        throw new AssertionError("Cannot instantiate this class");
    }
    
    /**
     * Validates that an expression is true and throws an error if it false.
     * This is meant to be used in validating arguments for public methods.
     * 
     * @param expression The expression to evaluate as true, an assertion
     * @param errorMessage The error message to provide for the thrown exception
     * @throws IllegalArgumentException 
     *         with the provided errorMessage if expression is false
     */
    public static void checkArgument(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

    /**
     * Convenience method to validate an argument is not null. Rather than throw
     * a NullPointerError, throw an
     * 
     * @param toCheck The object to confirm is not null
     * @param errorMessage The error message to provide for the thrown exception
     * @return toCheck as a convenience for assignment
     * @throws IllegalArgumentException
     *             to make it clear that there is a problem with the argument
     *             being supplied to the method rather than a bug in the method
     *             itself.
     * @see Validator#checkArgument(boolean, String)
     */
    public static void checkNotNull(Object toCheck, String errorMessage) {
        checkArgument(null != toCheck, errorMessage);
        // After upgrade to >JRE 1.4, use generics to return the object
    }
}
