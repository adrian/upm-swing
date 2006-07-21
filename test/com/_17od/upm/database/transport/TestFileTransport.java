package com._17od.upm.database.transport;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import junit.framework.TestCase;

public class TestFileTransport extends TestCase {

    private File fileToUpload;
    private FileTransport transport;
    private byte[] fileContents;


    public void setUp() throws Exception {
        transport = new FileTransport();
        
        //Create a test file to upload
        fileToUpload = File.createTempFile("tmp", ".txt");
        FileOutputStream fos = new FileOutputStream(fileToUpload);
        fileContents = (new Date()).toString().getBytes();
        fos.write(fileContents);
        fos.close();
        
    }


    /*
     * Test method for 'com._17od.upm.database.transport.FileTransport.put(String, File)'
     */
    public void testPut() throws TransportException {
        transport.put("file://c:/temp", fileToUpload);
    }

    
    /*
     * Test method for 'com._17od.upm.database.transport.FileTransport.get(String)'
     */
    public void testGetString() {

    }

    
    /*
     * Test method for 'com._17od.upm.database.transport.FileTransport.delete(String, String, byte[], byte[])'
     */
    public void testDelete() {

    }

    
    /*
     * Test method for 'com._17od.upm.database.transport.FileTransport.getRemoteFile(String, String, String)'
     */
    public void testGetRemoteFile() {

    }

}
