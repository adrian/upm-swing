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
import java.io.IOException;
import java.net.URL;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import com._17od.upm.util.Preferences;


public class HTTPTransport extends Transport {

    private HttpClient client;


    public HTTPTransport() {

        client = new HttpClient();

        //Get the proxy settings
        String proxyHost = Preferences.get(Preferences.ApplicationOptions.HTTP_PROXY_HOST);
        if (proxyHost != null && !proxyHost.trim().equals("")) {
            String proxyPortStr = Preferences.get(Preferences.ApplicationOptions.HTTP_PROXY_PORT);
            int proxyPort = 0;
            if (proxyPortStr != null && !proxyPortStr.trim().equals("")) {
                proxyPort = Integer.parseInt(proxyPortStr);
            }
            client.getHostConfiguration().setProxy(proxyHost, proxyPort);
        }

    }


    public void put(String targetLocation, File file, byte[] username, byte[] password) throws TransportException {

        PostMethod post = new PostMethod(targetLocation);

        //This part is wrapped in a try/finally so that we can ensure
        //the connection to the HTTP server is always closed cleanly 
        try {
            
            Part[] parts = {
                    new FilePart("userfile", file)
            };
            post.setRequestEntity(
                    new MultipartRequestEntity(parts, post.getParams())
            );

            //Set the authentication details
            if (username.length > 0) {
                Credentials creds = new UsernamePasswordCredentials(new String(username), new String(password));
                URL url = new URL(targetLocation);
                AuthScope authScope = new AuthScope(url.getHost(), url.getPort());
                client.getState().setCredentials(authScope, creds);
                client.getParams().setAuthenticationPreemptive(true);
            }

            int status = client.executeMethod(post);
            
            // I've noticed on Windows (at least) that PHP seems to fail when moving files on the first attempt
            // The second attempt works so lets just do that
            if (status == HttpStatus.SC_OK && post.getResponseBodyAsString().equals("FILE_WASNT_MOVED")) {
            	status = client.executeMethod(post);            	
            }

            if (status != HttpStatus.SC_OK || !post.getResponseBodyAsString().equals("OK") ) {
                throw new TransportException("There's been some kind of problem uploading a file to the HTTP server. The return code returned was [" + post.getResponseBodyAsString() + "]");
            }
            
        } catch (Exception e) {
            throw new TransportException(e);
        } finally {
            post.releaseConnection();
        }
        
    }


    public byte[] get(String url, byte[] username, byte[] password) throws TransportException {

        byte[] retVal = null;
        
        GetMethod method = new GetMethod(url);
        
        //This part is wrapped in a try/finally so that we can ensure
        //the connection to the HTTP server is always closed cleanly 
        try {

            //Set the authentication details
            if (username.length > 0) {
                Credentials creds = new UsernamePasswordCredentials(new String(username), new String(password));
                URL urlObj = new URL(url);
                AuthScope authScope = new AuthScope(urlObj.getHost(), urlObj.getPort());
                client.getState().setCredentials(authScope, creds);
                client.getParams().setAuthenticationPreemptive(true);
            }
            
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                throw new TransportException("There's been some kind of problem getting a file from the HTTP server. The status code is [" + statusCode + "]");
            }

            retVal = method.getResponseBody();

        } catch (Exception e) {
            throw new TransportException(e);
        } finally {
            method.releaseConnection();
        }

        return retVal;

    }

    
    public File getRemoteFile(String remoteLocation, String httpUsername, String httpPassword) throws TransportException, IOException {
    	byte[] remoteFile = get(remoteLocation, httpUsername.getBytes(), httpPassword.getBytes());
    	File downloadedFile = File.createTempFile("upm", null);
    	FileOutputStream fos = new FileOutputStream(downloadedFile);
    	fos.write(remoteFile);
    	fos.close();
    	
    	return downloadedFile;

    }


    public void delete(String targetLocation, String name, byte[] username, byte[] password) throws TransportException {

        PostMethod post = new PostMethod(targetLocation);
        post.addParameter("fileToDelete", name);

        //This part is wrapped in a try/finally so that we can ensure
        //the connection to the HTTP server is always closed cleanly 
        try {

            //Set the authentication details
            if (username.length > 0) {
                Credentials creds = new UsernamePasswordCredentials(new String(username), new String(password));
                URL url = new URL(targetLocation);
                AuthScope authScope = new AuthScope(url.getHost(), url.getPort());
                client.getState().setCredentials(authScope, creds);
                client.getParams().setAuthenticationPreemptive(true);
            }

            int status = client.executeMethod(post);
            if (status != HttpStatus.SC_OK || !post.getResponseBodyAsString().equals("OK") ) {
                throw new TransportException("There's been some kind of problem deleting a file to the HTTP server. The return code returned was [" + post.getResponseBodyAsString() + "]");
            }

        } catch (Exception e) {
            throw new TransportException(e);
        } finally {
            post.releaseConnection();
        }

    }

}
