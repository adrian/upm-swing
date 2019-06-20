package com._17od.upm.transport;

import it.sauronsoftware.ftp4j.FTPClient;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Implements the Ftp transport.
 *
 * This class uses the following classes:
 * - FTPclient (http://www.sauronsoftware.it/projects/ftp4j/)
 * - URL (Java built-in)
 * @file FTPtransport.java
 * @author andreas
 *
 */
public class FTPtransport extends Transport {

    /**
     * This is the account name for logging in on the Ftp server if none is
     * supplied to the method. This should be an e-mail address.
     */
    protected static final String ANONYMOUS = "anonymous@world.test";

    // / This is the password sent for an anonymous login.
    protected static final String PASSWORD = "secret";

    // / This field holds an unique file resource for temporary storing the
    // database file.
    private File tmpDir;

    /**
     * Instantiates the object with the given remote location.
     * 
     * @note Currently the constructor does no special job...
     * @param remoteLocation
     *            URL of the remote file
     */
    public FTPtransport(String remoteLocation) {
    }

    /**
     * Instantiates the object and supplies a resource on the local host.
     * 
     * @param tmpDir
     *            The resource where the retrieved database file should be
     *            stored.
     */
    public FTPtransport(File tmpDir) {
        this.tmpDir = tmpDir;
    }

    // ----- put
    // -------------------------------------------------------------------------

    /**
     * puts the source file to the remote location
     * @param destination full destination url - with or without filename
     * @param source file
     * @param username the username accessing the server
     * @param username the password accessing the server
     * @throws TransportException thrown if there was a problem communicating with the server 
     */
    public void put(String destination, File source, String username,
            String password) throws TransportException {
        try {
            URL dst = new URL(destination);

            if (destination.endsWith("/")) {
                throw new TransportException("filename is mandatory");
            }

            FTPClient ftp = new FTPClient();
            ftp.connect(dst.getHost());
            ftp.login(username, password);

            File dstfile = new File(dst.getPath());
            // put the file into the correct place
            ftp.changeDirectory(dstfile.getParent().substring(1));
            ftp.upload(source);
            // rename the remote file if necessary
            if (!source.getName().equals(dstfile.getName())) {
                ftp.rename(source.getName(), dstfile.getName());
            }

            ftp.disconnect(false);
        } catch (Exception e) {
            throw new TransportException(e);
        }
    }

    public void put(String targetLocation, File file) throws TransportException {
        put(targetLocation, file, ANONYMOUS, PASSWORD);
    }

    // ----- get
    // -------------------------------------------------------------------------

    public byte[] get(String url) throws TransportException {
        return get(url, ANONYMOUS, PASSWORD);
    }

    public byte[] get(String fullRemoteLocation, String username,
            String password) throws TransportException {
        byte[] retVal = null;

        try {
            URL url = new URL(fullRemoteLocation);
            url = new URL(url.getProtocol() + "://" + username + ":" + password
                    + "@" + url.getHost() + url.getPath());
            BufferedInputStream src = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int ch;
            while ((ch = src.read()) != -1) {
                buf.write(ch);
            }
            retVal = buf.toByteArray();
            src.close();
            buf.close();
        } catch (MalformedURLException e) {
            throw new TransportException(e);
        } catch (IOException e) {
            throw new TransportException(e);
        }
        return retVal;
    }

    public byte[] get(String url, String fileName) throws TransportException {
        return get(url, fileName, ANONYMOUS, PASSWORD);
    }

    public byte[] get(String url, String fileName, String username,
            String password) throws TransportException {
        return get(url, fileName, ANONYMOUS, PASSWORD);
    }

    // ----- getRemoteFile
    // ---------------------------------------------------------------

    public File getRemoteFile(String remoteLocation) throws TransportException {
        return getRemoteFile(remoteLocation, ANONYMOUS, PASSWORD);
    }

    public File getRemoteFile(String fullRemoteLocation, String fileName,
            String username, String password) throws TransportException {
        return getRemoteFile(fullRemoteLocation, username, password);
    }

    public File getRemoteFile(String remoteLocation, String username,
            String password) throws TransportException {
        try {
            URL source = new URL(remoteLocation);
            File destination = File.createTempFile("upm", null, tmpDir);
            FTPClient ftp = new FTPClient();
            ftp.connect(source.getHost());
            ftp.login(username, password);
            ftp.download(source.getPath(), destination);
            ftp.disconnect(false);

            return destination;
        } catch (Exception e) {
            throw new TransportException(e);
        }
    }

    public File getRemoteFile(String remoteLocation, String fileName)
            throws TransportException {
        return getRemoteFile(remoteLocation, fileName, ANONYMOUS, PASSWORD);
    }

    // ----- delete
    // ----------------------------------------------------------------------

    public void delete(String targetLocation, String name, String username,
            String password) throws TransportException {
        // do nothing
    }

    public void delete(String targetLocation, String name)
            throws TransportException {
        // do nothing
    }

}
