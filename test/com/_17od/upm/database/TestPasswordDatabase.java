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
import java.io.IOException;

import junit.framework.TestCase;

import com._17od.upm.crypto.CryptoException;
import com._17od.upm.crypto.InvalidPasswordException;


public class TestPasswordDatabase extends TestCase {

    private String databaseFileName = System.getProperty("user.dir") + "/pd";
    private char[] password = "very secret password".toCharArray();

    
    public TestPasswordDatabase(String name) {
        super(name);
    }
    
    
    public void testOpenNonExistantFile() throws CryptoException, IOException {

        //Make sure the file doesn't exist
        deleteFile(databaseFileName);

        PasswordDatabase db = new PasswordDatabase(new File(databaseFileName));
        PasswordDatabasePersistence pers = new PasswordDatabasePersistence(password);
        pers.save(db);

        File f = new File(databaseFileName);
        if (f.exists() == false) {
            fail("Database file should exist now");
        }

    }


    public void testOpenExistingDB() throws CryptoException, IOException, ProblemReadingDatabaseFile, InvalidPasswordException {
        //Make sure the file doesn't exist
        deleteFile(databaseFileName);
        
        //Create the db on this line
        PasswordDatabase db = new PasswordDatabase(new File(databaseFileName));
        PasswordDatabasePersistence pers = new PasswordDatabasePersistence(password);
        pers.save(db);
        
        //Now try to open the db again
        PasswordDatabasePersistence dbPers = new PasswordDatabasePersistence();
        dbPers.load(new File(databaseFileName), password);
    }
    
    
    public void testAddAccount() throws CryptoException, IOException, ProblemReadingDatabaseFile, InvalidPasswordException {
        //Make sure the file doesn't exist
        deleteFile(databaseFileName);
        
        //Create the db
        PasswordDatabase db = new PasswordDatabase(new File(databaseFileName));
        
        //Add an account
        AccountInformation ai = new AccountInformation("Hotmail",
                "this is the userid",
                "this is the password",
                "this is the url",
                "this is the notes");
        db.addAccount(ai);
        PasswordDatabasePersistence pers = new PasswordDatabasePersistence(password);
        pers.save(db);

        //Load the db
        PasswordDatabasePersistence dbPers = new PasswordDatabasePersistence();
        db = dbPers.load(new File(databaseFileName), password);

        //Check to ensure the account was loaded back in
        AccountInformation ai2 = db.getAccount("Hotmail");

        assertEquals("this is the userid", new String(ai2.getUserId()));
        assertEquals("this is the password", new String(ai2.getPassword()));
        assertEquals("this is the url", new String(ai2.getUrl()));
        assertEquals("this is the notes", new String(ai2.getNotes()));
    }
    

    public void testRemoveAccount() throws CryptoException, IOException, ProblemReadingDatabaseFile, InvalidPasswordException {
        //Make sure the file doesn't exist
        deleteFile(databaseFileName);
        
        //Create the db
        PasswordDatabase db = new PasswordDatabase(new File(databaseFileName));
        
        //Add an account
        AccountInformation ai = new AccountInformation("Hotmail",
                "this is the userid",
                "this is the password",
                "this is the url",
                "this is the notes");
        AccountInformation ai2 = new AccountInformation("Yahoo Mail",
                "this is the userid2",
                "this is the password2",
                "this is the url2",
                "this is the notes2");
        db.addAccount(ai);
        db.addAccount(ai2);
        PasswordDatabasePersistence dbPers = new PasswordDatabasePersistence(password);
        dbPers.save(db);
        
        //Load the db 
        db = dbPers.load(new File(databaseFileName));
        
        //Delete an account
        db.deleteAccount("Yahoo Mail");
        dbPers.save(db);

        //Load the db again 
        db = dbPers.load(new File(databaseFileName));

        //Check to ensure the Hotmail account still exists
        AccountInformation hotmailAccount = db.getAccount("Hotmail");
        assertEquals("this is the userid", new String(hotmailAccount.getUserId()));
        assertEquals("this is the password", new String(hotmailAccount.getPassword()));
        assertEquals("this is the url", new String(hotmailAccount.getUrl()));
        assertEquals("this is the notes", new String(hotmailAccount.getNotes()));

        //Check to ensure the Yahoo Mail account is gone
        AccountInformation yahooAccount = db.getAccount("Yahoo Mail");
        if (yahooAccount != null) {
            fail("Account was not removed successfully");
        }
        
    }

    
    private void deleteFile(String fileName) {
        File f = new File(fileName);
        if (f.exists()) {
            boolean b = f.delete();
            if (b == false) {
                fail("Couldn't delete db file");
            }
        }
    }
    
}

