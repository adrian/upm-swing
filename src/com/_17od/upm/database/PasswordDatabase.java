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
package com._17od.upm.database;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.EOFException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import com._17od.upm.crypto.EncryptionService;


/**
 * This class represents the main interface to a password database
 * All interaction with the database file is done using this class
 */
public class PasswordDatabase {

	private static final String MAJOR_VERSION = "1"; 
	private static final String MINOR_VERSION = "0"; 
	private static final String PATCH_VERSION = "0";

	private File databaseFile;
	private DatabaseHeader dh;
	private HashMap accounts;
	private EncryptionService encryptionService;

	
	public PasswordDatabase(String dbFile, char[] password) throws Exception {
		this(dbFile, password, false);
	}
	
	
	public PasswordDatabase(String dbFile, char[] password, boolean overwrite) throws Exception {
		databaseFile = new File(dbFile);
		//Either create a new file (if it exists and overwrite == true OR it doesn't exist) or open the existing file
		if ((databaseFile.exists() && overwrite == true) || !databaseFile.exists()) {
			databaseFile.delete();
			databaseFile.createNewFile();
			dh = new DatabaseHeader(MAJOR_VERSION, MINOR_VERSION, PATCH_VERSION);
			accounts = new HashMap();
			encryptionService = new EncryptionService(password);
		} else {
			load(password);
		}
	}


	private void load(char[] password) throws IOException, GeneralSecurityException, ProblemReadingDatabaseFile {
		
		//Get the salt
		byte[] salt = new byte[EncryptionService.SALT_LENGTH];
		FileInputStream fileIS = new FileInputStream(databaseFile);
		fileIS.read(salt);
		fileIS.close();

		encryptionService = new EncryptionService(password, salt);
		
		InputStream is = encryptionService.getCipherInputStream(databaseFile);
		is.skip(salt.length);

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
		OutputStream os = encryptionService.getCipherOutputStream(databaseFile);
		
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
		
		RandomAccessFile raf = new RandomAccessFile(databaseFile, "rw");
		raf.write(encryptionService.getSalt());
		raf.close();
		//OutputStream dbOutputStream = new FileOutputStream(databaseFile);
		//dbOutputStream.write(encryptionService.getSalt());
		//dbOutputStream.close();
	}

	
	public Collection getAccounts() {
		return accounts.values();
	}
	
}
