package com._17od.upm.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import com._17od.upm.crypto.CryptoException;
import com._17od.upm.crypto.DESDecryptionService;
import com._17od.upm.crypto.EncryptionService;
import com._17od.upm.crypto.InvalidPasswordException;

/**
 * This factory is used to load or create a PasswordDatabase. Different versions
 * of the database need to be loaded slightly differently so this class takes
 * care of those differences.
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
 *   This allows for cryptographic changes in the database structure. Before this
 *   we had to know how to unencrypt the database before we could find out the version number.
 */
public class PasswordDatabasePersistence {

    private static final String FILE_HEADER = "UPM";
    private static final int DB_VERSION = 2;

    private EncryptionService encryptionService;

    /**
     * Used when we have a password and we want to get an instance of the class
     * so that we can call load(File, char[])  
     */
    public PasswordDatabasePersistence() {
    }

    /**
     * Used when we want to create a new database with the given password
     * @param password
     * @throws CryptoException
     */
    public PasswordDatabasePersistence(char[] password) throws CryptoException {
        encryptionService = new EncryptionService(password);
    }

    public PasswordDatabase load(File databaseFile) throws InvalidPasswordException, ProblemReadingDatabaseFile, IOException {

        byte[] fullDatabase = readFile(databaseFile);

        // Check the database is a minimum length
        if (fullDatabase.length < EncryptionService.SALT_LENGTH) {
            throw new ProblemReadingDatabaseFile("This file doesn't appear to be a UPM password database");
        }

        PasswordDatabase passwordDatabase = null;
        ByteArrayInputStream is = null;
        Revision revision = null;
        DatabaseOptions dbOptions = null;
        HashMap accounts = null;

        // Ensure this is a real UPM database by checking for the existence of 
        // the string "UPM" at the start of the file
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
                byte[] decryptedBytes;
                try {
                    decryptedBytes = encryptionService.decrypt(encryptedBytes);
                } catch (CryptoException e1) {
                    throw new InvalidPasswordException();
                }

                //If we've got here then the database was successfully decrypted 
                is = new ByteArrayInputStream(decryptedBytes);
                try {
                    revision = new Revision(is);
                    dbOptions = new DatabaseOptions(is);
    
                    // Read the remainder of the database in now
                    accounts = new HashMap();
                    try {
                        while (true) { //keep loading accounts until an EOFException is thrown
                            AccountInformation ai = new AccountInformation(is);
                            accounts.put(ai.getAccountName(), ai);
                        }
                    } catch (EOFException e) {
                        //just means we hit eof
                    }
                    is.close();
                } catch (IOException e) {
                    throw new ProblemReadingDatabaseFile(e.getMessage(), e);
                }

                passwordDatabase = new PasswordDatabase(revision, dbOptions, accounts, databaseFile);

            } else {
                throw new ProblemReadingDatabaseFile("Don't know how to handle database version [" + dbVersion + "]");
            }

        } else {
            
            // This might be an old database (pre version 2).
            // By throwing InvalidPasswordException the calling method can ask
            // the user for the password so that the load(File, char[]) method
            // can be called. That method knows how to load old versions of the
            // db
            throw new InvalidPasswordException();
        }
                
        return passwordDatabase;

    }

    public PasswordDatabase load(File databaseFile, char[] password) throws IOException, ProblemReadingDatabaseFile, InvalidPasswordException, CryptoException {

        byte[] fullDatabase;
        fullDatabase = readFile(databaseFile);

        // Check the database is a minimum length
        if (fullDatabase.length < EncryptionService.SALT_LENGTH) {
            throw new ProblemReadingDatabaseFile("This file doesn't appear to be a UPM password database");
        }

        ByteArrayInputStream is = null;
        Revision revision = null;
        DatabaseOptions dbOptions = null;

        // Ensure this is a real UPM database by checking for the existence of 
        // the string "UPM" at the start of the file
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
                byte[] decryptedBytes;
                try {
                    decryptedBytes = encryptionService.decrypt(encryptedBytes);
                } catch (CryptoException e) {
                    throw new InvalidPasswordException();
                }

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
            //Attempt to decrypt the database information
            try {
                decryptedBytes = DESDecryptionService.decrypt(password, salt, encryptedBytes);
            } catch (CryptoException e) {
                throw new InvalidPasswordException();
            }

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

            // Initialise the EncryptionService so that it's ready for the "save" operation
            encryptionService = new EncryptionService(password);

        }
        
        // Read the remainder of the database in now
        HashMap accounts = new HashMap();
        try {
            while (true) { //keep loading accounts until an EOFException is thrown
                AccountInformation ai = new AccountInformation(is);
                accounts.put(ai.getAccountName(), ai);
            }
        } catch (EOFException e) {
            //just means we hit eof
        }
        is.close();

        PasswordDatabase passwordDatabase = new PasswordDatabase(revision, dbOptions, accounts, databaseFile);
        
        return passwordDatabase;

    }

    public void save(PasswordDatabase database) throws IOException, CryptoException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        
        // Flatpack the database revision and options
        database.getRevisionObj().increment();
        database.getRevisionObj().flatPack(os);
        database.getDbOptions().flatPack(os);

        // Flatpack the accounts
        Iterator it = database.getAccountsHash().values().iterator();
        while (it.hasNext()) {
            AccountInformation ai = (AccountInformation) it.next();
            ai.flatPack(os);
        }
        os.close();
        byte[] dataToEncrypt = os.toByteArray();

        //Now encrypt the database data
        byte[] encryptedData = encryptionService.encrypt(dataToEncrypt);
        
        //Write the salt and the encrypted data out to the database file
        FileOutputStream fos = new FileOutputStream(database.getDatabaseFile());
        fos.write(FILE_HEADER.getBytes());
        fos.write(DB_VERSION);
        fos.write(encryptionService.getSalt());
        fos.write(encryptedData);
        fos.close();
    }

    public EncryptionService getEncryptionService() {
        return encryptionService;
    }

    private byte[] readFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) file.length()];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
    
        is.close();

        return bytes;
    }

}
