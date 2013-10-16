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
import java.net.URL;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;


/**
 * This interface allows the PasswordDatabase to communicate with a remote location
 * without having to know what the underlying transport or remote filesystem is
 */
public abstract class Transport {

    public abstract void put(String url, File file, String username, String password) throws TransportException;

    public abstract void delete(String url, String username, String password) throws TransportException;

    public abstract byte[] get(String url, String username, String password) throws TransportException;
    
	public File download(String url, String username, String password) throws TransportException {
        try {
            byte[] remoteFile = get(url, username, password);
            File downloadedFile = File.createTempFile("upm", null);
            FileOutputStream fos = new FileOutputStream(downloadedFile);
            fos.write(remoteFile);
            fos.close();
            return downloadedFile;
        } catch (IOException e) {
            throw new TransportException(e);
        }
	}

    public static Transport getTransportForURL(URL url) {
        Transport retVal = null;
        if (url.getProtocol().equals("http")) {
            retVal = new HTTPTransport();
        } else if (url.getProtocol().equals("https")) {
            retVal = new HTTPTransport();
        }
        return retVal;
    }
    
    public static boolean isASupportedProtocol(String protocol) {
        boolean supported = false;
        if (protocol.equals("http")) {
            supported = true;
        } else if (protocol.equals("https")) {
            supported = true;
        } else if (protocol.equals("file")) {
            supported = true;
        }
        return supported;
    }
}
