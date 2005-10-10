/*
 * $Id: TestPasswordDatabase.java 30 2005-09-04 11:07:54Z Adrian Smith $
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
package com._17od.upm;

import junit.framework.TestCase;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.security.GeneralSecurityException;


public class TestPasswordDatabase extends TestCase {

	private String databaseFileName = "c:\\temp\\pd5";
	private char[] password = {'v', 'e', 'r', 'y', 's', 'e', 'c',
			'r', 'e', 't', 'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};

	
	public TestPasswordDatabase(String name) {
		super(name);
	}
	
	
	public void testOpenNonExistantFile() throws IOException, ProblemReadingDatabaseFile, GeneralSecurityException, InvalidPasswordException {
		
		//Make sure the file doesn't exist
		deleteFile(databaseFileName);
		
		PasswordDatabase.createNewDatabase(databaseFileName, password);
		
		File f = new File(databaseFileName);
		if (f.exists() == false) {
			fail("Database file should exist now");
		}
	}


	public void testOpenExistingDB() throws IOException, ProblemReadingDatabaseFile, GeneralSecurityException {
		//Make sure the file doesn't exist
		deleteFile(databaseFileName);
		
		//Create the db on this line
		PasswordDatabase db = new PasswordDatabase(new File(databaseFileName), password);
		db.save();
		
		//Now try to open the db again
		new PasswordDatabase(new File(databaseFileName), password);
	}
	
	
	public void testAddAccount() throws IOException, ProblemReadingDatabaseFile, GeneralSecurityException {
		//Make sure the file doesn't exist
		deleteFile(databaseFileName);
		
		//Create the db
		PasswordDatabase db = new PasswordDatabase(new File(databaseFileName), password);
		
		//Add an account
		AccountInformation ai = new AccountInformation("Hotmail",
				"this is the userid".getBytes(),
				"this is the password".getBytes(),
				"this is the url".getBytes(),
				"this is the notes".getBytes());
		db.addAccount(ai);
		db.save();
		
		//Load the db 
		db = new PasswordDatabase(new File(databaseFileName), password);
		
		//Check to ensure the account was loaded back in
		AccountInformation ai2 = db.getAccount("Hotmail");

		assertEquals("this is the userid", new String(ai2.getUserId()));
		assertEquals("this is the password", new String(ai2.getPassword()));
		assertEquals("this is the url", new String(ai2.getUrl()));
		assertEquals("this is the notes", new String(ai2.getNotes()));
	}
	

	public void testRemoveAccount() throws IOException, ProblemReadingDatabaseFile, GeneralSecurityException {
		//Make sure the file doesn't exist
		deleteFile(databaseFileName);
		
		//Create the db
		PasswordDatabase db = new PasswordDatabase(new File(databaseFileName), password);
		
		//Add an account
		AccountInformation ai = new AccountInformation("Hotmail",
				"this is the userid".getBytes(),
				"this is the password".getBytes(),
				"this is the url".getBytes(),
				"this is the notes".getBytes());
		AccountInformation ai2 = new AccountInformation("Yahoo Mail",
				"this is the userid2".getBytes(),
				"this is the password2".getBytes(),
				"this is the url2".getBytes(),
				"this is the notes2".getBytes());
		db.addAccount(ai);
		db.addAccount(ai2);
		db.save();
		
		//Load the db 
		db = new PasswordDatabase(new File(databaseFileName), password);
		
		//Delete an account
		db.deleteAccount("Yahoo Mail");
		db.save();

		//Load the db again 
		db = new PasswordDatabase(new File(databaseFileName), password);
		
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

