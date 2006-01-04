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
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import com._17od.upm.util.Preferences;


public class HTTPTransport implements Transport {

    public void put(String targetLocation, File file) throws TransportException {

        HttpClient client = new HttpClient();

        //Get the proxy settings
        String proxyHost = Preferences.get(Preferences.ApplicationOptions.PROXY_HOST);
        if (proxyHost != null) {
            int proxyPort = Integer.parseInt(Preferences.get(Preferences.ApplicationOptions.PROXY_PORT));
            client.getHostConfiguration().setProxy(proxyHost, proxyPort);
        }

        PostMethod post = new PostMethod(targetLocation);

        //This part is wrapped in a try/finally so that we can ensure
        //the connection to the HTTP server is always closed cleanly 
        try {
            
            Part[] parts = {
                    new FilePart("userfile", file),
                    new StringPart("MAX_FILE_SIZE", "999999999")
            };
            post.setRequestEntity(
                    new MultipartRequestEntity(parts, post.getParams())
            );

            int status = client.executeMethod(post);
            if (status != HttpStatus.SC_OK || !post.getResponseBodyAsString().equals("OK") ) {
                throw new TransportException("There's been some kind of problem uploading a file to the HTTP server. The return code returned was [" + post.getResponseBodyAsString() + "]");
            }
            
        } catch (Exception e) {
            throw new TransportException(e);
        } finally {
            post.releaseConnection();
        }
        
    }


    public byte[] get(String url) throws TransportException {

        byte[] retVal = null;
        
        HttpClient client = new HttpClient();

        //Get the proxy settings
        String proxyHost = Preferences.get(Preferences.ApplicationOptions.PROXY_HOST);
        if (proxyHost != null) {
            int proxyPort = Integer.parseInt(Preferences.get(Preferences.ApplicationOptions.PROXY_PORT));
            client.getHostConfiguration().setProxy(proxyHost, proxyPort);
        }

        GetMethod method = new GetMethod(url);
        
        //This part is wrapped in a try/finally so that we can ensure
        //the connection to the HTTP server is always closed cleanly 
        try {

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


    public void delete(String targetLocation, String name) throws TransportException {

        HttpClient client = new HttpClient();

        //Get the proxy settings
        String proxyHost = Preferences.get(Preferences.ApplicationOptions.PROXY_HOST);
        if (proxyHost != null) {
            int proxyPort = Integer.parseInt(Preferences.get(Preferences.ApplicationOptions.PROXY_PORT));
            client.getHostConfiguration().setProxy(proxyHost, proxyPort);
        }

        PostMethod post = new PostMethod(targetLocation);
        post.addParameter("fileToDelete", name);

        //This part is wrapped in a try/finally so that we can ensure
        //the connection to the HTTP server is always closed cleanly 
        try {
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
