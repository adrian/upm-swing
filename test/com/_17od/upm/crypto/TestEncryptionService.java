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
package com._17od.upm.crypto;

import java.util.Arrays;
import junit.framework.TestCase;


public class TestEncryptionService extends TestCase {

    public void testEncryptDecryptString() throws CryptoException {
        char[] password = "test password".toCharArray();
        EncryptionService encryptionService = new EncryptionService(password);
        byte[] cleartext = "samplestring".getBytes();
        byte[] cipherText = encryptionService.encrypt(cleartext);
        byte[] cleartext2 = encryptionService.decrypt(cipherText);
        assertEquals("Decrypted text is different to original cleartext", new String(cleartext), new String(cleartext2));
    }

    
    public void testChangePassword() throws CryptoException {

        char[] password = "test password".toCharArray();
        EncryptionService encryptionService = new EncryptionService(password);

        char[] password2 = "test password2".toCharArray();
        EncryptionService encryptionService2 = new EncryptionService(password2, encryptionService.getSalt());

        byte[] cleartext = "samplestring".getBytes();
        byte[] cipherText = encryptionService.encrypt(cleartext);

        //When we decrypt the cipherText we should get a different result
        try {
            encryptionService2.decrypt(cipherText);
            fail("Should have got an InvalidPasswordException because we changed the password");
        } catch (CryptoException e) {
            //should get here
        }
    }

    
    public void testSaltIsRandom() throws CryptoException, InterruptedException {

        char[] password = "test password".toCharArray();
        EncryptionService encryptionService = new EncryptionService(password);

        byte[] salt1 = encryptionService.getSalt();
        Thread.sleep(100); //Sleep to ensure the Random class will run in the future with a new seed
        encryptionService = new EncryptionService(password);
        byte[] salt2 = encryptionService.getSalt();
        Thread.sleep(100);
        encryptionService = new EncryptionService(password);
        byte[] salt3 = encryptionService.getSalt();
        Thread.sleep(100);
        encryptionService = new EncryptionService(password);
        byte[] salt4 = encryptionService.getSalt();
        
        assertNotEquals(salt1, salt2, "Salt1 and Salt2 are the same");
        assertNotEquals(salt1, salt3, "Salt1 and Salt3 are the same");
        assertNotEquals(salt1, salt4, "Salt1 and Salt4 are the same");
        assertNotEquals(salt2, salt3, "Salt2 and Salt3 are the same");
        assertNotEquals(salt2, salt4, "Salt2 and Salt4 are the same");
        assertNotEquals(salt3, salt4, "Salt3 and Salt4 are the same");
    }

    
    private void assertNotEquals(byte[] b1, byte[] b2, String message) {
        if (Arrays.equals(b1, b2)) {
            fail(message);
        }
    }
    
}
