/*
 * $Id$
 * 
 * Universal Password Manager
 * Copyright (C) 2005-2011 Adrian Smith
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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;


public class DESDecryptionService {

    public static final String PBEWithMD5AndDES = "PBEWithMD5AndDES";

    
    /**
     * This method initialises a local decryption cipher, and decrypts the given string.
     * It's here as a convienence method for backwards compatibility with the old DES 
     * encryption algorithim pre 1.3
     * @param password
     * @param salt
     * @param ciphertext
     * @return The decrypted bytes
     * @throws CryptoException 
     */
    public static byte[] decrypt(char[] password, byte[] salt, byte[] cipherText) throws CryptoException {
        return process(password, Cipher.DECRYPT_MODE, salt, cipherText); 
    }
    
    public static byte[] encrypt(char[] password,  byte[] salt, byte[] plainText) throws CryptoException {
        return process(password, Cipher.ENCRYPT_MODE, salt, plainText); 
    }

    private static byte[] process(char[] password, int mode, byte[] salt, byte[] plainText) throws CryptoException {
        byte[] retVal = null;
        try {
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
            SecretKeyFactory keyFac = SecretKeyFactory.getInstance(PBEWithMD5AndDES);
            SecretKey secreyKey = keyFac.generateSecret(pbeKeySpec);
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 20);
            Cipher desDecryptionCipher = Cipher.getInstance(PBEWithMD5AndDES);
            desDecryptionCipher.init(mode, secreyKey, pbeParamSpec);
            retVal = desDecryptionCipher.doFinal(plainText);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        } catch (IllegalBlockSizeException e) {
            throw new CryptoException(e);
        } catch (BadPaddingException e) {
            throw new CryptoException(e);
        } catch (NoSuchPaddingException e) {
            throw new CryptoException(e);
        } catch (InvalidKeySpecException e) {
            throw new CryptoException(e);
        } catch (InvalidKeyException e) {
            throw new CryptoException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new CryptoException(e);
        }

        return retVal;
    }

    public static byte[] generateSalt() throws CryptoException {
        SecureRandom saltGen;
        try {
            saltGen = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
        byte pSalt[] = new byte[8];
        saltGen.nextBytes(pSalt);
        return pSalt;
    }

}
