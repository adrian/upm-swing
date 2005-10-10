/*
 * $Id: PasswordDatabase.java 33 2005-09-19 17:35:31Z Adrian Smith $
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.EOFException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.InputStream;
import java.security.GeneralSecurityException;



/**
 * This class represents the main interface to a password database
 * All interaction with the database file is done using this class
 */
public class PasswordDatabase {

	private static final String MAC = "UniversalPasswordManagerMAC";

	private static final String MAJOR_VERSION = "1"; 
	private static final String MINOR_VERSION = "0"; 
	private static final String PATCH_VERSION = "0";

	private File databaseFile;
	private DatabaseHeader dh;
	private HashMap accounts;

	
	private PasswordDatabase() {
		//Don't allow constructor to be called externally
	}

	
	public static PasswordDatabase createNewDatabase(String dbFile, char[] password) throws IOException, GeneralSecurityException  {
		PasswordDatabase pd = new PasswordDatabase();
		pd.initialiseNewDatabase(dbFile, password);
		return pd;
	}


	public static PasswordDatabase loadExistingDatabase(String dbFile, char[] password) throws FileNotFoundException {
		
		//First check to ensure the password is correct
		InputStream unencryptedInputStream = new FileInputStream(dbFile);
		byte[] macOnFile = new byte[MAC.getBytes().length];
		unencryptedInputStream.read(macOnFile);
		
		byte[] encryptedMac = EncryptionService.getInstance().getMAC(MAC.getBytes());
		
		unencryptedInputStream.close();
		
		if (!Arrays.equals(macOnFile, encryptedMac)) {
			throw new InvalidPasswordException();
		}
		
		//If the password is correct then continue to load the database
		PasswordDatabase pd = new PasswordDatabase();
		pd.loadDatabase(password);
		return pd;
	}
	

	private void loadDatabase(char[] password) throws IOException, ProblemReadingDatabaseFile, GeneralSecurityException, InvalidPasswordException {
		
		InputStream is = EncryptionService.getInstance().getCipherInputStream(databaseFile);

		//Load the header
		dh = new DatabaseHeader(is);
		
		//Load the accounts
		accounts = new HashMap();
		try {
			while (true) { //keep loading accounts until an EOFException is thrown
				AccountInformation ai = new AccountInformation(is);
				addAccount(ai);
			}
		} catch (EOFException e) {
			//just means we hit eof
		}
		
		is.close();
	}
	
	
	private void initialiseNewDatabase(String dbFile, char[] password) throws IOException, GeneralSecurityException {
		EncryptionService.getInstance().init(password);
		databaseFile = new File(dbFile);
		databaseFile.createNewFile();
		dh = new DatabaseHeader(MAJOR_VERSION, MINOR_VERSION, PATCH_VERSION);
		accounts = new HashMap();
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
	
	public void save() throws IOException {

		//Write the MAC
		byte[] encryptedMac = EncryptionService.getInstance().getMAC(MAC.getBytes());
		OutputStream dbOutputStream = new FileOutputStream(databaseFile);
		dbOutputStream.write(encryptedMac);
		dbOutputStream.close();
		
		//Now write out the encrypted contents
		OutputStream os = EncryptionService.getInstance().getCipherOutputStream(databaseFile);

		//Write the header
		dh.flatPack(os);
		
		//Write all the accounts
		Iterator it = accounts.values().iterator();
		while (it.hasNext()) {
			AccountInformation ai = (AccountInformation) it.next();
			ai.flatPack(os);
		}
	
		os.flush();
		os.close();
	}

	public Collection getAccounts() {
		return accounts.values();
	}
	
}
