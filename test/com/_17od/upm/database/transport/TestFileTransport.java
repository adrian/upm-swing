package com._17od.upm.database.transport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

import junit.framework.TestCase;

public class TestFileTransport extends TestCase {

    private File fileToUpload;
    private FileTransport transport;
    private byte[] fileContents;
    private File uploadArea;


    public void setUp() throws Exception {
        transport = new FileTransport();
        
        //Create a test file to upload
        fileToUpload = File.createTempFile("tmp", ".txt");
        FileOutputStream fos = new FileOutputStream(fileToUpload);
        fileContents = (new Date()).toString().getBytes();
        fos.write(fileContents);
        fos.close();
        
        // Find the temporary directory and create a sub directory
        // for use as an upload area
        File tmpDirectory = fileToUpload.getParentFile();
        uploadArea = new File(tmpDirectory, "uploadArea");
        uploadArea.mkdir();
    }


    /*
     * Test method for 'com._17od.upm.database.transport.FileTransport.put(String, File)'
     */
    public void testPut() throws TransportException, MalformedURLException {
        transport.put(uploadArea.toURI().toString(), fileToUpload);
        File uploadedFile = new File(uploadArea, fileToUpload.getName());
        if (!uploadedFile.exists()) {
            fail("Failed to upload file");
        }
    }

    
    /*
     * Test method for 'com._17od.upm.database.transport.FileTransport.delete(String, String, byte[], byte[])'
     */
    public void testDelete() throws TransportException {
        // Upload a file, delete it and then check that it is actually gone
        transport.put(uploadArea.toString(), fileToUpload);
        transport.delete(uploadArea.toString(), fileToUpload.getName());
        File uploadedFile = new File(uploadArea, fileToUpload.getName());
        if (uploadedFile.exists()) {
            fail("Failed to delete file");
        }
    }


    /*
     * Test method for 'com._17od.upm.database.transport.FileTransport.getRemoteFile(String, String, String)'
     */
    public void testGetRemoteFile() throws TransportException, IOException {
        // Upload a file, and then try to retrieve it
        transport.put(uploadArea.toString(), fileToUpload);
        File downloadedFile = transport.getRemoteFile(uploadArea.toString() + File.separatorChar + fileToUpload.getName());
    }

}
