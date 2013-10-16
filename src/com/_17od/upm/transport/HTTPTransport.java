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
package com._17od.upm.transport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import com._17od.upm.util.Preferences;


public class HTTPTransport extends Transport {

    private HttpClient client;


    public HTTPTransport() {

        client = new HttpClient();

        Boolean acceptSelfSignedCerts =
                new Boolean(Preferences.get(
                        Preferences.ApplicationOptions.HTTPS_ACCEPT_SELFSIGNED_CERTS));
        if (acceptSelfSignedCerts.booleanValue()) {
            // Create a Protcol handler which contains a HTTPS socket factory
            // capable of accepting self signed and otherwise invalid certificates.
            Protocol httpsProtocol = new Protocol("https",
                    (ProtocolSocketFactory) new EasySSLProtocolSocketFactory(),
                    443);
            Protocol.registerProtocol("https", httpsProtocol);
        }

        //Get the proxy settings
        Boolean proxyEnabled = new Boolean(Preferences.get(Preferences.ApplicationOptions.HTTP_PROXY_ENABLED));
        if (proxyEnabled.booleanValue()) {
            String proxyHost = Preferences.get(Preferences.ApplicationOptions.HTTP_PROXY_HOST);
            String proxyPortStr = Preferences.get(Preferences.ApplicationOptions.HTTP_PROXY_PORT);
            String proxyUserName = Preferences.get(Preferences.ApplicationOptions.HTTP_PROXY_USERNAME);
            String proxyPassword = Preferences.get(Preferences.ApplicationOptions.HTTP_PROXY_PASSWORD);
            String decodedPassword = new String(Base64.decodeBase64(proxyPassword.getBytes()));
            
            if (isNotEmpty(proxyHost)) {
                int proxyPort = 0;
                if (isNotEmpty(proxyPortStr)) {
                    proxyPort = Integer.parseInt(proxyPortStr);
                    client.getHostConfiguration().setProxy(proxyHost, proxyPort);
                    if (isNotEmpty(proxyUserName) && isNotEmpty(proxyPassword)) {
                        client.getState().setProxyCredentials(AuthScope.ANY, 
                                new UsernamePasswordCredentials(proxyUserName, decodedPassword));
                    }
                }
            }
        }

    }


    
    public void put(String url, File file, String username, String password) throws TransportException {

        PutMethod post = new PutMethod(url);

        //This part is wrapped in a try/finally so that we can ensure
        //the connection to the HTTP server is always closed cleanly 
        try {
            
            post.setRequestBody(
                    new FileInputStream(file)
            );

            //Set the HTTP authentication details
            if (username != null) {
                Credentials creds = new UsernamePasswordCredentials(new String(username), new String(password));
                URL _url = new URL(url);
                AuthScope authScope = new AuthScope(_url.getHost(), _url.getPort());
                client.getState().setCredentials(authScope, creds);
                client.getParams().setAuthenticationPreemptive(true);
            }

            // This line makes the HTTP call
            int status = client.executeMethod(post);
            
            if ((status != HttpStatus.SC_OK) && (status != HttpStatus.SC_NO_CONTENT)) {
                throw new TransportException("There's been some kind of problem uploading a file to the HTTP server.\n\nThe HTTP error message is " + status +" [" + HttpStatus.getStatusText(status) + "]");
            }
            
        } catch (FileNotFoundException e) {
            throw new TransportException(e);
        } catch (MalformedURLException e) {
            throw new TransportException(e);
        } catch (HttpException e) {
            throw new TransportException(e);
        } catch (IOException e) {
            throw new TransportException(e);
        } finally {
            post.releaseConnection();
        }
        
    }


    public byte[] get(String url, String username, String password) throws TransportException {

        byte[] retVal = null;

        GetMethod method = new GetMethod(url);
        
        //This part is wrapped in a try/finally so that we can ensure
        //the connection to the HTTP server is always closed cleanly 
        try {

            //Set the authentication details
            if (username != null) {
                Credentials creds = new UsernamePasswordCredentials(new String(username), new String(password));
                URL urlObj = new URL(url);
                AuthScope authScope = new AuthScope(urlObj.getHost(), urlObj.getPort());
                client.getState().setCredentials(authScope, creds);
                client.getParams().setAuthenticationPreemptive(true);
            }

            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                throw new TransportException("There's been some kind of problem getting the URL [" + url + "].\n\nThe HTTP error message is [" + HttpStatus.getStatusText(statusCode) + "]");
            }

            retVal = method.getResponseBody();


        } catch (MalformedURLException e) {
            throw new TransportException(e);
        } catch (HttpException e) {
            throw new TransportException(e);
        } catch (IOException e) {
            throw new TransportException(e);
        } finally {
            method.releaseConnection();
        }

        return retVal;

    }

    public void delete(String url, String username, String password) throws TransportException {

        DeleteMethod post = new DeleteMethod(url);

        //This part is wrapped in a try/finally so that we can ensure
        //the connection to the HTTP server is always closed cleanly 
        try {

            //Set the authentication details
            if (username != null) {
                Credentials creds = new UsernamePasswordCredentials(new String(username), new String(password));
                URL _url = new URL(url);
                AuthScope authScope = new AuthScope(_url.getHost(), _url.getPort());
                client.getState().setCredentials(authScope, creds);
                client.getParams().setAuthenticationPreemptive(true);
            }

            int status = client.executeMethod(post);
            if (status != HttpStatus.SC_OK) {
                throw new TransportException("There's been some kind of problem deleting a file on the HTTP server.\n\nThe HTTP error message is [" + HttpStatus.getStatusText(status) + "]");
            }
        } catch (MalformedURLException e) {
            throw new TransportException(e);
        } catch (HttpException e) {
            throw new TransportException(e);
        } catch (IOException e) {
            throw new TransportException(e);
        } finally {
            post.releaseConnection();
        }

    }

    
    private String addTrailingSlash(String url) {
        if (url.charAt(url.length() - 1) != '/') {
            url = url + '/';
        }
        return url;
    }

    
    private boolean isNotEmpty(String stringToCheck) {
        boolean retVal = false;
        if (stringToCheck != null && !stringToCheck.trim().equals("")) {
            retVal = true;
        }
        return retVal;
    }

}
