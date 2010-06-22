/*
 * $Id$
 * 
 * Universal Password Manager
 * Copyright (C) 2005-2010 Adrian Smith
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
package com._17od.upm.database;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;


public class AccountInformation extends FlatPackObject {

    private String accountName;
    private byte[] userId;
    private byte[] password;
    private byte[] url;
    private byte[] notes;


    public AccountInformation() {
        accountName = "";
        userId = "".getBytes();
        password = "".getBytes();
        url = "".getBytes();
        notes = "".getBytes();
    }
    
    
    public AccountInformation(String accountName, byte[] userId, byte[] password,
            byte[] url, byte[] notes) {
        this.accountName = accountName;
        this.userId = userId;
        this.password = password;
        this.url = url;
        this.notes = notes;
    }
    
    
    public AccountInformation(InputStream is) throws IOException, ProblemReadingDatabaseFile {
        assemble(is);
    }
    
    
    public void flatPack(OutputStream os) throws IOException {
        os.write(flatPack(accountName));
        os.write(flatPack(userId));
        os.write(flatPack(password));
        os.write(flatPack(url));
        os.write(flatPack(notes));
    }

    private void assemble(InputStream is) throws IOException, ProblemReadingDatabaseFile {
        accountName = getString(is);
        userId = getBytes(is);
        password = getBytes(is);
        url = getBytes(is);
        notes = getBytes(is);
    }
    
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public byte[] getNotes() {
        return notes;
    }

    public void setNotes(byte[] notes) {
        this.notes = notes;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public byte[] getUrl() {
        return url;
    }

    public void setUrl(byte[] url) {
        this.url = url;
    }

    public byte[] getUserId() {
        return userId;
    }

    public void setUserId(byte[] userId) {
        this.userId = userId;
    }

}
