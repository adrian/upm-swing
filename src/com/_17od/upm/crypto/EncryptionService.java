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
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


public class EncryptionService {

    private static final String PBEWithSHA256And256BitAES = "PBEWithSHA256And256BitAES-CBC-BC";
    private static final String randomAlgorithm = "SHA1PRNG";
    public static final int SALT_LENGTH = 8;

    private Cipher encryptionCipher; 
    private Cipher decryptionCipher;
    private byte[] salt;


    public EncryptionService(char[] password) throws GeneralSecurityException {
        //Generate a random salt
        SecureRandom saltGen = SecureRandom.getInstance(randomAlgorithm);
        byte pSalt[] = new byte[SALT_LENGTH];
        saltGen.nextBytes(pSalt);

        init(password, pSalt);
    }


    public EncryptionService(char[] password, byte[] salt) throws GeneralSecurityException {
        init(password, salt);
    }


	private void init(char[] password, byte[] salt) throws GeneralSecurityException {

        // Load the BouncyCastle JCE provider (for the AES algorithim)
        Security.addProvider(new BouncyCastleProvider());

        PBEKeySpec pbeKeySpec;
        PBEParameterSpec pbeParamSpec;
        SecretKeyFactory keyFac;

        this.salt = salt;
        int count = 20;

        pbeParamSpec = new PBEParameterSpec(salt, count);

        pbeKeySpec = new PBEKeySpec(password);
        keyFac = SecretKeyFactory.getInstance(PBEWithSHA256And256BitAES);
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

        encryptionCipher = Cipher.getInstance(PBEWithSHA256And256BitAES);
        decryptionCipher = Cipher.getInstance(PBEWithSHA256And256BitAES);

        encryptionCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
        decryptionCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
    }


	public byte[] encrypt(byte[] cleartext) throws IllegalBlockSizeException, BadPaddingException {
        return encryptionCipher.doFinal(cleartext);
	}


    public byte[] decrypt(byte[] ciphertext) throws IllegalBlockSizeException, InvalidPasswordException {
        byte[] retVal;
        try {
            retVal = decryptionCipher.doFinal(ciphertext);
        } catch (BadPaddingException e) {
            throw new InvalidPasswordException(); 
        }
        return retVal;
    }


    public byte[] getSalt() {
        return salt;
    }
    
}
