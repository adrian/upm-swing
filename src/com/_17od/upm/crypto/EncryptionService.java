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
package com._17od.upm.crypto;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.security.GeneralSecurityException;
import java.util.Random;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import java.io.File;
import java.io.FileNotFoundException;


public class EncryptionService {

	private static EncryptionService singletonInstance;
	private Cipher encryptionCipher; 
	private Cipher decryptionCipher;
	//Would prefer if I could use AES or Twofish here. Sun doesn't 
	//distribute one with their JRE. Might want to look into
	//http://www.bouncycastle.org
	private static final String algorithm = "PBEWithMD5AndDES";
	private static final int SALT_LENGTH = 8;
	private byte[] salt;

	
	/*
	 * This is a singleton class so we need to remove the ability
	 * to create instances of it
	 */
	private EncryptionService() {
	}


	/**
	 * Get the singleton instance of this class
	 */
	public static EncryptionService getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new EncryptionService();
		}
		return singletonInstance;
	}
	
	
	public void init(char[] password) throws GeneralSecurityException {
		
		PBEKeySpec pbeKeySpec;
	    PBEParameterSpec pbeParamSpec;
	    SecretKeyFactory keyFac;
	    
	    //Generate a random salt
	    Random saltGen = new Random();
	    salt = new byte[SALT_LENGTH];
	    saltGen.nextBytes(salt);

	    int count = 20;

	    pbeParamSpec = new PBEParameterSpec(salt, count);
	    
	    pbeKeySpec = new PBEKeySpec(password);
	    keyFac = SecretKeyFactory.getInstance(algorithm);
	    SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

	    encryptionCipher = Cipher.getInstance(algorithm);
	    decryptionCipher = Cipher.getInstance(algorithm);

	    encryptionCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
	    decryptionCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
	    
	}
	
	
	public CipherOutputStream getCipherOutputStream(File file) throws FileNotFoundException {
	    return new CipherOutputStream(new FileOutputStream(file), encryptionCipher);
	}

	
	public CipherInputStream getCipherInputStream(File file) throws FileNotFoundException {
	    return new CipherInputStream(new FileInputStream(file), decryptionCipher);
	}
	
	
	public Cipher getEncryptionCipher() {
		return encryptionCipher;
	}
	
	
	public Cipher getDecryptionCipher() {
		return decryptionCipher;
	}
	
	
	public byte[] encrypt(byte[] cleartext) throws IllegalBlockSizeException, BadPaddingException {
		return encryptionCipher.doFinal(cleartext);
	}

	
	public byte[] decrypt(byte[] ciphertext) throws IllegalBlockSizeException, BadPaddingException {
		return decryptionCipher.doFinal(ciphertext);
	}

	public byte[] getSalt() {
		return salt;
	}
	
}
