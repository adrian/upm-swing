package com._17od.upm.gui;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.IllegalBlockSizeException;

import com._17od.upm.crypto.InvalidPasswordException;
import com._17od.upm.database.AccountInformation;
import com._17od.upm.database.PasswordDatabase;
import com._17od.upm.database.ProblemReadingDatabaseFile;

import junit.framework.TestCase;

public class TestDatabaseActionsHelper extends TestCase {

    private char[] password = "password".toCharArray();
		

    /**
     * This testcase tests the database rename functionality in the 
     * DatabaseActionsHelper.replaceDatabase() method
     * @throws PasswordDatabaseException
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws GeneralSecurityException
     * @throws ProblemReadingDatabaseFile
     * @throws InvalidPasswordException
     */
    public void testReplaceDatabase() throws PasswordDatabaseException, IOException, IllegalBlockSizeException, GeneralSecurityException, ProblemReadingDatabaseFile, InvalidPasswordException {
		
        // Create 2 databases
        PasswordDatabase db1 = new PasswordDatabase(createTempFile(), password);
        db1.addAccount(new AccountInformation("Database 1", "user".getBytes(), "pass".getBytes(), "url".getBytes(), "notes".getBytes()));
        db1.save();
        String db1FileName = db1.getDatabaseFile().getAbsolutePath();
        
        PasswordDatabase db2 = new PasswordDatabase(createTempFile(), password);
        db2.addAccount(new AccountInformation("Database 2", "user".getBytes(), "pass".getBytes(), "url".getBytes(), "notes".getBytes()));
        db2.save();
        
        // Now replace the database
        DatabaseActionsHelper.replaceDatabase(db1, db2);
        
        // Check that the new database has the details from the database we replaced it with
        PasswordDatabase replacedDatabase = new PasswordDatabase(new File(db1FileName), password);
        AccountInformation ai = replacedDatabase.getAccount("Database 2");
        if (ai == null) {
            fail("The database wasn't replaced");
        }

    }

    
    private File createTempFile() throws IOException {
        File tempFile = File.createTempFile("upm", null);
        String tempFileName = tempFile.getAbsolutePath();
        tempFile.delete();
        return tempFile;
    }
    
}
