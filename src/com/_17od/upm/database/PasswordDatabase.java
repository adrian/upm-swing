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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.security.GeneralSecurityException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import com._17od.upm.crypto.EncryptionService;
import com._17od.upm.crypto.InvalidPasswordException;


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

	
	public PasswordDatabase(File dbFile, char[] password) throws IllegalBlockSizeException, IOException, GeneralSecurityException, ProblemReadingDatabaseFile, InvalidPasswordException {
		this(dbFile, password, false);
	}
	
	
	public PasswordDatabase(File dbFile, char[] password, boolean overwrite) throws IllegalBlockSizeException, IOException, GeneralSecurityException, ProblemReadingDatabaseFile, InvalidPasswordException {
		databaseFile = dbFile;
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


	public void changePassword(char[] password) throws GeneralSecurityException {
		encryptionService = new EncryptionService(password);
	}
	
	private void load(char[] password) throws IOException, GeneralSecurityException, ProblemReadingDatabaseFile, IllegalBlockSizeException, InvalidPasswordException {
		
		//Read the encrypted bytes into an in memory object (the ByteArrayOutputStream)
		FileInputStream fis = new FileInputStream(databaseFile);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = 0;
		while ((i = fis.read()) != -1) {
			baos.write(i);
		}
		byte[] saltAndKeyBytes = baos.toByteArray();
		baos.close();
		fis.close();

        //Make sure this is a real password database file (well at least the right size)
        if (saltAndKeyBytes.length < EncryptionService.SALT_LENGTH) {
            throw new ProblemReadingDatabaseFile("Either this isn't a UPM database file or it's corrupt");
        }
        
		//Split up the salt and encrypted bytes
		byte[] salt = new byte[EncryptionService.SALT_LENGTH];
		System.arraycopy(saltAndKeyBytes, 0, salt, 0, EncryptionService.SALT_LENGTH);
		int encryptedBytesLength = saltAndKeyBytes.length - EncryptionService.SALT_LENGTH;
		byte[] encryptedBytes = new byte[encryptedBytesLength]; 
		System.arraycopy(saltAndKeyBytes, EncryptionService.SALT_LENGTH, encryptedBytes, 0, encryptedBytesLength);

		//Attempt to dencrypt the database information
		encryptionService = new EncryptionService(password, salt);
		byte[] decryptedBytes = encryptionService.decrypt(encryptedBytes);
		
		//We'll get to here if the password was correct so load up the decryped byte
		ByteArrayInputStream is = new ByteArrayInputStream(decryptedBytes);
		dh = new DatabaseHeader(is);
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
	
	
	public void save() throws IOException, IllegalBlockSizeException, BadPaddingException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		//Build up a byte array of all the data to be encrypted
		dh.flatPack(os);
		Iterator it = accounts.values().iterator();
		while (it.hasNext()) {
			AccountInformation ai = (AccountInformation) it.next();
			ai.flatPack(os);
		}
		os.close();
		byte[] dataToEncrypt = os.toByteArray();

		//Now encrypt the database data
		byte[] encryptedData = encryptionService.encrypt(dataToEncrypt);
		
		//Write the salt and the encrypted data out to the database file
		FileOutputStream fos = new FileOutputStream(databaseFile);
		fos.write(encryptionService.getSalt());
		fos.write(encryptedData);
		fos.close();

	}

	
	public ArrayList getAccounts() {
	    return new ArrayList(accounts.values());
	}
	
	
	public File getDatabaseFile() {
		return databaseFile;
	}
	
}
