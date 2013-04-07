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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * This class represents the main interface to a password database.
 * All interaction with the database file is done using this class.
 */
public class PasswordDatabase {

    private File databaseFile;
    private Revision revision;
    private DatabaseOptions dbOptions;
    private HashMap accounts;

    
    public PasswordDatabase(Revision revision, DatabaseOptions dbOptions, HashMap accounts, File databaseFile) {
        this.revision = revision;
        this.dbOptions = dbOptions;
        this.accounts = accounts;
        this.databaseFile = databaseFile;
    }


    public PasswordDatabase(File dbFile) {
        this.revision = new Revision();
        this.dbOptions = new DatabaseOptions();
        this.accounts = new HashMap();
        this.databaseFile = dbFile;
    }
    

    public void addAccount(AccountInformation ai) {
        accounts.put(ai.getAccountName(), ai);
    }
    

    public void deleteAccount(String accountName) {
        accounts.remove(accountName);
    }

    
    public AccountInformation getAccount(String name) {
        return (AccountInformation) accounts.get(name);
    }


    public ArrayList getAccounts() {
        return new ArrayList(accounts.values());
    }


    public HashMap getAccountsHash() {
        return accounts;
    }
    
    
    public File getDatabaseFile() {
        return databaseFile;
    }


    public DatabaseOptions getDbOptions() {
        return dbOptions;
    }


    public Revision getRevisionObj() {
        return revision;
    }

       
    public int getRevision() {
        return revision.getRevision();
    }

}
