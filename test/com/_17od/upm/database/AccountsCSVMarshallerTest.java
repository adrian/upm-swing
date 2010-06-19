package com._17od.upm.database;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import com.csvreader.CsvReader;

public class AccountsCSVMarshallerTest extends TestCase {

    public void testWithExistingFile() throws IOException {
        File exportFile = File.createTempFile("testdb_", ".upm");

        AccountsCSVMarshaller marshaller = new AccountsCSVMarshaller();
        try {
            marshaller.marshal(null, exportFile);
            fail("Expected to get a FileAlreadyExistsException");
        } catch (ExportException e) {
            // expected to get here
        }
    }

    public void testWithOneSimpleAccount() throws ExportException, IOException {
        AccountInformation account = new AccountInformation(
                "Test Account1", 
                "testuser1".getBytes(),
                "test password1".getBytes(), 
                "http://www.test.com1".getBytes(),
                "this is a test note1".getBytes());

        ArrayList accounts = new ArrayList();
        accounts.add(account);

        // Marshall the database out to a CSV file
        File csvFile = File.createTempFile("testWithOneSimpleAccount_", ".csv");
        csvFile.delete();
        AccountsCSVMarshaller marshaller = new AccountsCSVMarshaller();
        marshaller.marshal(accounts, csvFile);

        // check that the file contents are what we expect
        CsvReader reader = new CsvReader(new FileReader(csvFile));
        reader.readRecord();
        assertEquals(account, reader.getValues());
    }

    public void testWithMultipleSimpleAccounts() throws IOException, ExportException {
        AccountInformation account1 = new AccountInformation(
                "Test Account1", 
                "testuser1".getBytes(),
                "test password1".getBytes(), 
                "http://www.test.com1".getBytes(),
                "this is a test note1".getBytes());
        AccountInformation account2 = new AccountInformation(
                "Test Account2", 
                "testuser2".getBytes(),
                "test password2".getBytes(), 
                "http://www.test.com2".getBytes(),
                "this is a test note2".getBytes());
        AccountInformation account3 = new AccountInformation(
                "Test Account3", 
                "testuser3".getBytes(),
                "test password3".getBytes(), 
                "http://www.test.com3".getBytes(),
                "this is a test note3".getBytes());

        ArrayList accounts = new ArrayList();
        accounts.add(account1);
        accounts.add(account2);
        accounts.add(account3);

        // Marshall the database out to a CSV file
        File csvFile = File.createTempFile("testWithMultipleSimpleAccounts_", ".csv");
        csvFile.delete();
        AccountsCSVMarshaller marshaller = new AccountsCSVMarshaller();
        marshaller.marshal(accounts, csvFile);

        // check that the file contents are what we expect
        CsvReader reader = new CsvReader(new FileReader(csvFile));
        reader.readRecord();
        assertEquals(account1, reader.getValues());
        reader.readRecord();
        assertEquals(account2, reader.getValues());
        reader.readRecord();
        assertEquals(account3, reader.getValues());
    }

    public void testWhenNotesHaveCRLF() throws IOException, ExportException {
        AccountInformation account = new AccountInformation(
                "Test Account1", 
                "testuser1".getBytes(),
                "test password1".getBytes(), 
                "http://www.test.com1".getBytes(),
                "this is a \ntest note1".getBytes());

        ArrayList accounts = new ArrayList();
        accounts.add(account);

        // Marshall the database out to a CSV file
        File csvFile = File.createTempFile("testWhenNotesHaveCRLF_", ".csv");
        csvFile.delete();
        AccountsCSVMarshaller marshaller = new AccountsCSVMarshaller();
        marshaller.marshal(accounts, csvFile);

        // check that the file contents are what we expect
        CsvReader reader = new CsvReader(new FileReader(csvFile));
        reader.readRecord();
        assertEquals(account, reader.getValues());
    }

    public void testWhenNotesHasComma() throws IOException, ExportException {
        AccountInformation account = new AccountInformation(
                "Test Account1", 
                "testuser1".getBytes(),
                "test password1".getBytes(), 
                "http://www.test.com1".getBytes(),
                "this is a ,test note1".getBytes());

        ArrayList accounts = new ArrayList();
        accounts.add(account);

        // Marshall the database out to a CSV file
        File csvFile = File.createTempFile("testWhenNotesHasComma", ".csv");
        csvFile.delete();
        AccountsCSVMarshaller marshaller = new AccountsCSVMarshaller();
        marshaller.marshal(accounts, csvFile);

        // check that the file contents are what we expect
        CsvReader reader = new CsvReader(new FileReader(csvFile));
        reader.readRecord();
        assertEquals(account, reader.getValues());
    }

    public void testImport() throws IOException, ExportException, ImportException {
        AccountInformation account = new AccountInformation(
                "Test Account1", 
                "testuser1".getBytes(),
                "test password1".getBytes(), 
                "http://www.test.com1".getBytes(),
                "this is a test note1".getBytes());

        ArrayList accounts = new ArrayList();
        accounts.add(account);

        // Marshall the database out to a CSV file
        File csvFile = File.createTempFile("testImport", ".csv");
        csvFile.delete();
        AccountsCSVMarshaller marshaller = new AccountsCSVMarshaller();
        marshaller.marshal(accounts, csvFile);

        // do the import and ensure the AccountInformation object we get back 
        // is correct
        ArrayList importedAccounts = marshaller.unmarshal(csvFile);
        assertNotNull(importedAccounts);
        assertEquals(1, importedAccounts.size());
        assertEquals(account, (AccountInformation) importedAccounts.get(0));
    }

    private void assertEquals(AccountInformation expected, AccountInformation actual) {
        assertEquals(expected.getAccountName(), actual.getAccountName());
        assertEquals(new String(expected.getUserId()), new String(actual.getUserId()));
        assertEquals(new String(expected.getPassword()), new String(actual.getPassword()));
        assertEquals(new String(expected.getUrl()), new String(actual.getUrl()));
        assertEquals(new String(expected.getNotes()), new String(actual.getNotes()));
    }

    private void assertEquals(AccountInformation expected, String[] actual) {
        assertEquals(expected.getAccountName(), actual[0]);
        assertEquals(new String(expected.getUserId()), actual[1]);
        assertEquals(new String(expected.getPassword()), actual[2]);
        assertEquals(new String(expected.getUrl()), actual[3]);
        assertEquals(new String(expected.getNotes()), actual[4]);
    }

}
