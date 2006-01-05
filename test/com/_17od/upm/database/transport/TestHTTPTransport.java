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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import com._17od.upm.util.Preferences;
import junit.framework.TestCase;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;


public class TestHTTPTransport extends TestCase {
    
    private File fileToUpload;
    private byte[] fileContents;
    private HTTPTransport transport;
    private byte[] httpUsername;
    private byte[] httpPassword;


    public void setUp() throws Exception {
        Preferences.load();

        transport = new HTTPTransport();

        //Create a test file to upload
        fileToUpload = File.createTempFile("tmp", ".txt");
        FileOutputStream fos = new FileOutputStream(fileToUpload);
        fileContents = (new Date()).toString().getBytes();
        fos.write(fileContents);
        fos.close();

        //Load the properties file
        String propertiesFile = System.getProperty("junit.properties");
        if (propertiesFile != null) {
            Properties properties = new Properties();
            properties.load(new FileInputStream(propertiesFile));
            httpUsername = properties.getProperty("junit.http.username").getBytes();
            httpPassword = properties.getProperty("junit.http.password").getBytes();
        }
    }


    public void tearDown() throws Exception {
        try {
            transport.delete("http://www.17od.com/upload/deletefile.php", fileToUpload.getName(), httpUsername, httpPassword);
        } catch (Exception e) {
            //Don't worry about errors here
        }
    }


    public void testPut() throws Exception {
        //Upload the file
        transport.put("http://www.17od.com/upload/upload.php", fileToUpload, httpUsername, httpPassword);
    }


    public void testPutExistingFile() throws Exception {
        //Upload the file
        transport.put("http://www.17od.com/upload/upload.php", fileToUpload, httpUsername, httpPassword);

        try {
            //Now attempt to upload the file again
            transport.put("http://www.17od.com/upload/upload.php", fileToUpload, httpUsername, httpPassword);

            //Should have got an error here
            fail("Should have got an error when uploading an existing file");
        } catch (Exception e) {
            if (!(e.getMessage().indexOf("FILE_ALREADY_EXISTS") > 0)) {
                fail("Should have got a FILE_ALREADY_EXISTS error when uploading an existing file");
            }
        }
    }


    public void testGet() throws Exception {
        //Upload the file
        transport.put("http://www.17od.com/upload/upload.php", fileToUpload, httpUsername, httpPassword);

        //Get the file back
        byte[] retrievedFileContents = transport.get("http://www.17od.com/upload/" + fileToUpload.getName(), httpUsername, httpPassword);

        //Compare before and after file
        if (!Arrays.equals(fileContents, retrievedFileContents)) {
            fail("Before and after files are not the same");
        }
    }
    
    
    public void testDelete() throws Exception {
        
        //Upload the file
        transport.put("http://www.17od.com/upload/upload.php", fileToUpload, httpUsername, httpPassword);

        //Delete the file
        transport.delete("http://www.17od.com/upload/deletefile.php", fileToUpload.getName(), httpUsername, httpPassword);

        try {
            //Now try to get the file back
            transport.get("http://www.17od.com/upload/" + fileToUpload.getName(), httpUsername, httpPassword);

            //Should have got an error here
            fail("Should have got an error when uploading an existing file");
        } catch (Exception e) {
            if (!(e.getMessage().indexOf("404") > 0)) {
                fail("Should have got a HTTP 404 error when uploading an existing file");
            }
        }

    }
    
}
