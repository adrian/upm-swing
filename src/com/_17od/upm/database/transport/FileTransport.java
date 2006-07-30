package com._17od.upm.database.transport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;


public class FileTransport extends Transport {

    public void put(String targetLocation, File file, byte[] username, byte[] password) throws TransportException {
        
        // Create a new file at the target location
        targetLocation = addTrailingSlash(targetLocation);
        File targetFile = new File(targetLocation + file.getName());
        
        // Copy the source file to the target file
        try {
            copy(file, targetFile);
        } catch (IOException e) {
            throw new TransportException(e);
        }
        
    }

    
    public void put(String targetLocation, File file) throws TransportException {
        put(targetLocation, file, null, null);
    }

    
    public byte[] get(String fileToGet, byte[] username, byte[] password) throws TransportException {
        
        byte[] retVal;
        
        try {
            InputStream in = new FileInputStream(fileToGet);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            // Read from the URL and write the bytes read to a ByteArrayOutputStream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }
            in.close();
            baos.close();
            
            // Set the return value = to the bytes in the byte array
            retVal = baos.toByteArray();
            
        } catch (IOException e) {
            throw new TransportException(e);
        }
        
        return retVal;
        
    }

    
    public byte[] get(String url) throws TransportException {
        return get(url, null, null);
    }

    
    public void delete(String uploadArea, String name, byte[] username, byte[] password) throws TransportException {
        // Create a new file at the target location
        uploadArea = addTrailingSlash(uploadArea);
        File targetFile = new File(uploadArea + name);
        
        // Delete the file
        targetFile.delete();
    }


    public void delete(String uploadArea, String name) throws TransportException {
        delete(uploadArea, name, null, null);
    }
    
    
    public File getRemoteFile(String remoteFile, byte[] username, byte[] password) throws TransportException {
        try {
            byte[] remoteFileBytes = get(remoteFile, username, password);
            File downloadedFile = File.createTempFile("upm", null);
            FileOutputStream fos = new FileOutputStream(downloadedFile);
            fos.write(remoteFileBytes);
            fos.close();
            return downloadedFile;
        } catch (IOException e) {
            throw new TransportException(e);
        }
    }


    public File getRemoteFile(String remoteFile) throws TransportException {
        return getRemoteFile(remoteFile, null, null);
    }
    
    
    /**
     * Copy a src file to a destination file
     * @param src
     * @param dst
     * @throws IOException
     */
    private void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
    
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
