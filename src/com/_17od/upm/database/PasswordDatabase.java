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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.security.GeneralSecurityException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.codec.binary.Base64;

import com._17od.upm.crypto.DESDecryptionService;
import com._17od.upm.crypto.EncryptionService;
import com._17od.upm.crypto.InvalidPasswordException;


/**
 * This class represents the main interface to a password database.
 * All interaction with the database file is done using this class.
 * 
 * Database versions and formats. The items between [] brackets are encrypted.
 *   2     >> MAGIC_NUMBER DB_VERSION SALT [DB_REVISION DB_OPTIONS ACCOUNTS]
 *   1.1.0 >> SALT [DB_HEADER DB_REVISION DB_OPTIONS ACCOUNTS]
 *   1.0.0 >> SALT [DB_HEADER ACCOUNTS]
 * 
 *   DB_VERSION = The structural version of the database
 *   SALT = The salt used to mix with the user password to create the key
 *   DB_HEADER = Was used to store the structural version of the database (pre version 2)
 *   DB_OPTIONS = Options relating to the database
 *   ACCOUNTS = The account information
 *   
 *   From version 2 the db version is stored unencrypted at the start of the file.
 *   This allows for cryptographic changes in the database structure because beforehand
 *   we had to know how to unencrypt the database before we could find out the version number.
 */
public class PasswordDatabase {

	private static final int DB_VERSION = 2;
    private static final String FILE_HEADER = "UPM";

	private File databaseFile;
	private Revision revision;
	private DatabaseOptions dbOptions;
	private HashMap accounts;
	private EncryptionService encryptionService;
	private byte[] encodedPassword;

	
	public PasswordDatabase(File dbFile, char[] password) throws IllegalBlockSizeException, IOException, GeneralSecurityException, ProblemReadingDatabaseFile, InvalidPasswordException {
		this(dbFile, password, false);
	}
	
	
	public PasswordDatabase(File dbFile, char[] password, boolean overwrite) throws IllegalBlockSizeException, IOException, GeneralSecurityException, ProblemReadingDatabaseFile, InvalidPasswordException {
		databaseFile = dbFile;
		//Either create a new file (if it exists and overwrite == true OR it doesn't exist) or open the existing file
		if ((databaseFile.exists() && overwrite == true) || !databaseFile.exists()) {
			databaseFile.delete();
			databaseFile.createNewFile();
			revision = new Revision();
			dbOptions = new DatabaseOptions();
			accounts = new HashMap();
			encryptionService = new EncryptionService(password);
			setPassword(password);
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
		byte[] fullDatabase = baos.toByteArray();
		baos.close();
		fis.close();

        // Check the database is a minimum length
        if (fullDatabase.length < EncryptionService.SALT_LENGTH) {
            throw new ProblemReadingDatabaseFile("This file doesn't appear to be a UPM password database");
        }

        ByteArrayInputStream is = null;
        
        // Ensure this is a real UPM database by checking for the existance of the string "UPM" at the start of the file
        byte[] header = new byte[FILE_HEADER.getBytes().length];
        System.arraycopy(fullDatabase, 0, header, 0, header.length);
        if (Arrays.equals(header, FILE_HEADER.getBytes())) {

            // Calculate the positions of each item in the file
            int dbVersionPos      = header.length;
            int saltPos           = dbVersionPos + 1;
            int encryptedBytesPos = saltPos + EncryptionService.SALT_LENGTH;

            // Get the database version 
            byte dbVersion = fullDatabase[dbVersionPos];

            if (dbVersion == 2) {
                byte[] salt = new byte[EncryptionService.SALT_LENGTH];
                System.arraycopy(fullDatabase, saltPos, salt, 0, EncryptionService.SALT_LENGTH);
                int encryptedBytesLength = fullDatabase.length - encryptedBytesPos;
                byte[] encryptedBytes = new byte[encryptedBytesLength]; 
                System.arraycopy(fullDatabase, encryptedBytesPos, encryptedBytes, 0, encryptedBytesLength);
    
                //Attempt to decrypt the database information
                encryptionService = new EncryptionService(password, salt);
                byte[] decryptedBytes = encryptionService.decrypt(encryptedBytes);

                //If we've got here then the database was successfully decrypted 
                is = new ByteArrayInputStream(decryptedBytes);
                revision = new Revision(is);
                dbOptions = new DatabaseOptions(is);
            } else {
                throw new ProblemReadingDatabaseFile("Don't know how to handle database version [" + dbVersion + "]");
            }

        } else {
            
            // This might be an old database (pre version 2) so try loading it using the old database format
            
            // Check the database is a minimum length
            if (fullDatabase.length < EncryptionService.SALT_LENGTH) {
                throw new ProblemReadingDatabaseFile("This file doesn't appear to be a UPM password database");
            }
            
            //Split up the salt and encrypted bytes
            byte[] salt = new byte[EncryptionService.SALT_LENGTH];
            System.arraycopy(fullDatabase, 0, salt, 0, EncryptionService.SALT_LENGTH);
            int encryptedBytesLength = fullDatabase.length - EncryptionService.SALT_LENGTH;
            byte[] encryptedBytes = new byte[encryptedBytesLength]; 
            System.arraycopy(fullDatabase, EncryptionService.SALT_LENGTH, encryptedBytes, 0, encryptedBytesLength);

            byte[] decryptedBytes = null;
            try {
                //Attempt to decrypt the database information
                decryptedBytes = DESDecryptionService.decrypt(password, salt, encryptedBytes);
            } catch (IllegalBlockSizeException e) {
                throw new ProblemReadingDatabaseFile("Either your password is incorrect or this file isn't a UPM password database");
            }

            // Create the encryption for use later in the save() method
            encryptionService = new EncryptionService(password, salt);
            
            //We'll get to here if the password was correct so load up the decryped byte
            is = new ByteArrayInputStream(decryptedBytes);
            DatabaseHeader dh = new DatabaseHeader(is);

            // At this point we'll check to see what version the database is and load it accordingly
            if (dh.getVersion().equals("1.1.0")) {
                // Version 1.1.0 introduced a revision number & database options so read that in now
                revision = new Revision(is);
                dbOptions = new DatabaseOptions(is);
            } else if (dh.getVersion().equals("1.0.0")) {
                revision = new Revision();
                dbOptions = new DatabaseOptions();
            } else {
                throw new ProblemReadingDatabaseFile("Don't know how to handle database version [" + dh.getVersion() + "]");
            }

        }
        
		// Read the remainder of the database in now
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
        
        
        // Store the master password for use later
        setPassword(password);
        
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
		
        // Flatpack the database revision and options
        revision.increment();
        revision.flatPack(os);
        dbOptions.flatPack(os);

		// Flatpack the accounts
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
        fos.write(FILE_HEADER.getBytes());
        fos.write(DB_VERSION);
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


	public DatabaseOptions getDbOptions() {
		return dbOptions;
	}


	public int getRevision() {
		return revision.getRevision();
	}


	public char[] getPassword() {
        String passwordAsString = new String(Base64.decodeBase64(this.encodedPassword));
		return passwordAsString.toCharArray();
	}


	public void setPassword(char[] password) {
        String passwordAsString = new String(password);
		this.encodedPassword = Base64.encodeBase64(passwordAsString.getBytes());
	}

}
