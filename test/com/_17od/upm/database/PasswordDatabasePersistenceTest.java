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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import com._17od.upm.crypto.CryptoException;
import com._17od.upm.crypto.DESDecryptionService;
import com._17od.upm.crypto.EncryptionService;
import com._17od.upm.crypto.InvalidPasswordException;

public class PasswordDatabasePersistenceTest extends TestCase {

    private String password = "password";
    private PasswordDatabasePersistence passwordDatabasePersistence;

    public void setUp() {
        passwordDatabasePersistence = new PasswordDatabasePersistence();
    }

    public void testLoadVer2DB() throws ProblemReadingDatabaseFile, CryptoException, IOException, InvalidPasswordException {
        File f = createVer2Db();
        PasswordDatabase db = passwordDatabasePersistence.load(f, password.toCharArray());
        assertEquals(4, db.getRevision());
        assertEquals("rl", db.getDbOptions().getRemoteLocation());
        assertEquals("adbe", db.getDbOptions().getAuthDBEntry());
        assertEquals(1, db.getAccounts().size());
        AccountInformation account = (AccountInformation) db.getAccounts().get(0);
        assertEquals("test account", account.getAccountName());
        assertEquals("auserid", new String(account.getUserId()));
        assertEquals("apassword", new String(account.getPassword()));
        assertEquals("http://www.a.com", new String(account.getUrl()));
        assertEquals("some notes", new String(account.getNotes()));
    }

    public void testLoadVer110DB() throws ProblemReadingDatabaseFile, CryptoException, IOException, InvalidPasswordException {
        File f = createVer110Db();
        PasswordDatabase db = passwordDatabasePersistence.load(f, password.toCharArray());
        assertEquals(6, db.getRevision());
        assertEquals("qwe", db.getDbOptions().getRemoteLocation());
        assertEquals("fdef", db.getDbOptions().getAuthDBEntry());
        assertEquals(1, db.getAccounts().size());
        AccountInformation account = (AccountInformation) db.getAccounts().get(0);
        assertEquals("test account110", account.getAccountName());
        assertEquals("auserid110", new String(account.getUserId()));
        assertEquals("apassword110", new String(account.getPassword()));
        assertEquals("http://www.a.com/110", new String(account.getUrl()));
        assertEquals("some notes110", new String(account.getNotes()));
    }

    public void testLoadVer100DB() throws ProblemReadingDatabaseFile, CryptoException, IOException, InvalidPasswordException {
        File f = createVer100Db();
        PasswordDatabase db = passwordDatabasePersistence.load(f, password.toCharArray());
        assertEquals(1, db.getAccounts().size());
        AccountInformation account = (AccountInformation) db.getAccounts().get(0);
        assertEquals("test account100", account.getAccountName());
        assertEquals("auserid100", new String(account.getUserId()));
        assertEquals("apassword100", new String(account.getPassword()));
        assertEquals("http://www.a.com/100", new String(account.getUrl()));
        assertEquals("some notes100", new String(account.getNotes()));
    }

    private File createVer100Db() throws IOException, CryptoException {
        byte[] b = createVer100DbBytes();
        return saveToFile(b);
    }

    private File createVer110Db() throws IOException, CryptoException {
        byte[] b = createVer110DbBytes();
        return saveToFile(b);
    }

    private File createVer2Db() throws IOException, CryptoException {
        byte[] b = createVer2DbBytes();
        return saveToFile(b);
    }

    private byte[] createVer100DbBytes() throws IOException, CryptoException {
        // SALT [DB_HEADER ACCOUNTS]
        ByteArrayOutputStream partToEncryptOS = new ByteArrayOutputStream();
        partToEncryptOS.write("00011".getBytes());      // db version: major
        partToEncryptOS.write("00010".getBytes());      // db version: minor
        partToEncryptOS.write("00010".getBytes());      // db version: patch
        partToEncryptOS.write("0015test account100".getBytes());      // account name
        partToEncryptOS.write("0010auserid100".getBytes());           // userid
        partToEncryptOS.write("0012apassword100".getBytes());         // password
        partToEncryptOS.write("0020http://www.a.com/100".getBytes()); // url
        partToEncryptOS.write("0013some notes100".getBytes());        // notes
        partToEncryptOS.close();
        byte[] salt = DESDecryptionService.generateSalt();
        byte[] encryptedPart = DESDecryptionService.encrypt(password.toCharArray(), salt, partToEncryptOS.toByteArray());

        ByteArrayOutputStream dbOS = new ByteArrayOutputStream();
        dbOS.write(salt);             // salt
        dbOS.write(encryptedPart);    // encrypted body
        dbOS.close();
        
        return dbOS.toByteArray();
    }

    private byte[] createVer110DbBytes() throws IOException, CryptoException {
        // SALT [DB_HEADER DB_REVISION DB_OPTIONS ACCOUNTS]
        ByteArrayOutputStream partToEncryptOS = new ByteArrayOutputStream();
        partToEncryptOS.write("00011".getBytes());      // db version: major
        partToEncryptOS.write("00011".getBytes());      // db version: minor
        partToEncryptOS.write("00010".getBytes());      // db version: patch
        partToEncryptOS.write("00016".getBytes());      // revision
        partToEncryptOS.write("0003qwe".getBytes());    // remoteLocation
        partToEncryptOS.write("0004fdef".getBytes());   // authDBEntry
        partToEncryptOS.write("0015test account110".getBytes());      // account name
        partToEncryptOS.write("0010auserid110".getBytes());           // userid
        partToEncryptOS.write("0012apassword110".getBytes());         // password
        partToEncryptOS.write("0020http://www.a.com/110".getBytes()); // url
        partToEncryptOS.write("0013some notes110".getBytes());        // notes
        partToEncryptOS.close();
        byte[] salt = DESDecryptionService.generateSalt();
        byte[] encryptedPart = DESDecryptionService.encrypt(password.toCharArray(), salt, partToEncryptOS.toByteArray());
        
        ByteArrayOutputStream dbOS = new ByteArrayOutputStream();
        dbOS.write(salt);              // salt
        dbOS.write(encryptedPart);    // encrypted body
        dbOS.close();
        
        return dbOS.toByteArray();
    }
    
    private byte[] createVer2DbBytes() throws CryptoException, IOException {
        EncryptionService ec = new EncryptionService(password.toCharArray());

        // MAGIC_NUMBER DB_VERSION SALT [DB_REVISION DB_OPTIONS ACCOUNTS]
        ByteArrayOutputStream partToEncryptOS = new ByteArrayOutputStream();
        partToEncryptOS.write("00014".getBytes());      // revision
        partToEncryptOS.write("0002rl".getBytes());     // remoteLocation
        partToEncryptOS.write("0004adbe".getBytes());   // authDBEntry
        partToEncryptOS.write("0012test account".getBytes());     // account name
        partToEncryptOS.write("0007auserid".getBytes());          // userid
        partToEncryptOS.write("0009apassword".getBytes());        // password
        partToEncryptOS.write("0016http://www.a.com".getBytes()); // url
        partToEncryptOS.write("0010some notes".getBytes());       // notes
        partToEncryptOS.close();
        byte[] encryptedPart = ec.encrypt(partToEncryptOS.toByteArray());

        ByteArrayOutputStream dbOS = new ByteArrayOutputStream();
        dbOS.write("UPM".getBytes());   // magic number
        dbOS.write(0x2);                // db version (2)
        dbOS.write(ec.getSalt());       // salt
        dbOS.write(encryptedPart);      // encrypted body
        dbOS.close();
        
        return dbOS.toByteArray();
    }

    private File saveToFile(byte[] b) throws IOException {
        File f = File.createTempFile("upm", "upm");
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(b);
        fos.close();
        return f;
    }

}
