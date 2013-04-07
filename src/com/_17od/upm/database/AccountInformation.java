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
package com._17od.upm.database;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;


public class AccountInformation extends FlatPackObject {

    private String accountName;
    private String userId;
    private String password;
    private String url;
    private String notes;


    public AccountInformation() {
        accountName = "";
        userId = "";
        password = "";
        url = "";
        notes = "";
    }
    
    
    public AccountInformation(String accountName, String userId,
            String password, String url, String notes) {
        this.accountName = accountName;
        this.userId = userId;
        this.password = password;
        this.url = url;
        this.notes = notes;
    }


    public AccountInformation(InputStream is) throws IOException, ProblemReadingDatabaseFile {
        assemble(is, Charset.forName("UTF-8"));
    }


    public AccountInformation(InputStream is, Charset charset) throws IOException, ProblemReadingDatabaseFile {
        assemble(is, charset);
    }
    
    
    public void flatPack(OutputStream os) throws IOException {
        os.write(flatPack(accountName));
        os.write(flatPack(userId));
        os.write(flatPack(password));
        os.write(flatPack(url));
        os.write(flatPack(notes));
    }

    private void assemble(InputStream is, Charset charset) throws IOException, ProblemReadingDatabaseFile {
        accountName = getString(is, charset);
        userId = getString(is, charset);
        password = getString(is, charset);
        url = getString(is, charset);
        notes = getString(is, charset);
    }
    
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
