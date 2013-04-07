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
package com._17od.upm.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;

import junit.framework.TestCase;


public class TestAccountInformation extends TestCase {

    public void testFlatPack() throws IOException {
        AccountInformation ai = new AccountInformation("Hotmail",
                "this is the userid",
                "this is the password",
                "this is the url",
                "this is the notes");
        
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ai.flatPack(os);
        
        String s = os.toString();
        assertEquals("0007Hotmail" +
                     "0018this is the userid" +
                     "0020this is the password" +
                     "0015this is the url" +
                     "0017this is the notes", s); 
    }


    public void testAssemble() throws IOException, ProblemReadingDatabaseFile {

        String s = new String("0007Hotmail" +
                              "0018this is the userid" +
                              "0020this is the password" +
                              "0015this is the url" +
                              "0017this is the notes"); 
        byte[] b = s.getBytes();
        ByteArrayInputStream is = new ByteArrayInputStream(b);
        AccountInformation ai = new AccountInformation(is);
        
        assertEquals("Hotmail", ai.getAccountName());
        assertEquals("this is the userid", new String(ai.getUserId()));
        assertEquals("this is the password", new String(ai.getPassword()));
        assertEquals("this is the url", new String(ai.getUrl()));
        assertEquals("this is the notes", new String(ai.getNotes()));
    }

    
    public void testAssembleCharsInFieldLength() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream("bad input".getBytes());
        try {
            AccountInformation ai = new AccountInformation(is);
            fail("Should have got an ProblemReadingDatabaseFile exception now");
        } catch (ProblemReadingDatabaseFile e ) {
            //ok to get here
        }
    }
    

    public void testAssembleBadFieldLength() throws IOException, ProblemReadingDatabaseFile {
        ByteArrayInputStream is = new ByteArrayInputStream("0".getBytes());
        try {
            AccountInformation ai = new AccountInformation(is);
            fail("Should have got an EOFException exception now");
        } catch (EOFException e ) {
            //ok to get here
        }
    }

    
    public void testAssembleBadFieldContents() throws IOException, ProblemReadingDatabaseFile {
        ByteArrayInputStream is = new ByteArrayInputStream("0004".getBytes());
        try {
            AccountInformation ai = new AccountInformation(is);
            fail("Should have got an EOFException exception now");
        } catch (EOFException e ) {
            //ok to get here
        }
    }
    
}
