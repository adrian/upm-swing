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
import java.io.IOException;

import com._17od.upm.database.DatabaseHeader;
import com._17od.upm.database.ProblemReadingDatabaseFile;

import junit.framework.TestCase;

public class TestDatabaseHeader extends TestCase {

    public void testFlatPack() throws IOException {
        
        //Create a new DatabaseHeader
        DatabaseHeader dh = new DatabaseHeader(0, 1, 3);
        
        //Flat pack it
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        dh.flatPack(os);

        //Now make sure the bytes that were flat packed are correct
        String s = os.toString();
        assertEquals("00010" +
                     "00011" +
                     "00013", s); 
    }
    

    public void testAssemble() throws IOException, ProblemReadingDatabaseFile {

        String s = new String("00010" +
                              "00011" +
                              "00013"); 
        byte[] b = s.getBytes();
        ByteArrayInputStream is = new ByteArrayInputStream(b);
        DatabaseHeader dh = new DatabaseHeader(is);
        
        assertEquals(0, dh.getMajorVersion());
        assertEquals(1, dh.getMinorVersion());
        assertEquals(3, dh.getPatchVersion());
    }
    
}