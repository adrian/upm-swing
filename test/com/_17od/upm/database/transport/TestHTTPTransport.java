/*
 * $Id$
 * 
 * Universal Password Manager
 * Copyright (C) 2005 Adrian Smith
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
package com._17od.upm.database.transport;

import java.io.File;
import java.io.FileOutputStream;
import com._17od.upm.util.Preferences;
import junit.framework.TestCase;
import java.util.Arrays;
import java.util.Date;
import java.lang.RuntimeException;


public class TestHTTPTransport extends TestCase {

    public void setUp() throws Exception {
        Preferences.load();
    }
    
    
    public void testPut() throws Exception {

        //Create a test file to upload
        File fileToUpload = File.createTempFile("tmp", ".txt");
        FileOutputStream fos = new FileOutputStream(fileToUpload);
        fos.write("Sample File Contents".getBytes());
        fos.close();
        
        //Upload the file
        HTTPTransport transport = new HTTPTransport();
        transport.put("http://www.17od.com/upload.php", fileToUpload);

    }


    public void testPutExistingFile() throws Exception {

        //Create a test file to upload
        File fileToUpload = File.createTempFile("tmp", ".txt");
        FileOutputStream fos = new FileOutputStream(fileToUpload);
        byte[] b1 = (new Date()).toString().getBytes();
        fos.write(b1);
        fos.close();
        
        //Upload the file
        HTTPTransport transport = new HTTPTransport();
        transport.put("http://www.17od.com/upload.php", fileToUpload);

        try {
            //Now attempt to upload the file again
            transport.put("http://www.17od.com/upload.php", fileToUpload);

            //Should have got an error here
            fail("Should have got an error when uploading an existing file");
        } catch (Exception e) {
            if (!(e.getMessage().indexOf("FILE_ALREADY_EXISTS") > 0)) {
                fail("Should have got a FILE_ALREADY_EXISTS error when uploading an existing file");
            }
        }
    }

    
    public void testGet() throws Exception {

        //Create a test file to upload
        File fileToUpload = File.createTempFile("tmp", ".txt");
        FileOutputStream fos = new FileOutputStream(fileToUpload);
        byte[] b1 = (new Date()).toString().getBytes();
        fos.write(b1);
        fos.close();
        
        //Upload the file
        HTTPTransport transport = new HTTPTransport();
        transport.put("http://www.17od.com/upload.php", fileToUpload);

        //Now get it back
        byte[] b2 = transport.get("http://www.17od.com/upload/" + fileToUpload.getName());

        //Check that the before and after byte arrays are the same
        if (!Arrays.equals(b1, b2)) {
            fail("The files are not the same");
        }

    }

}
