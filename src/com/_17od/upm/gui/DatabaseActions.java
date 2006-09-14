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
package com._17od.upm.gui;

import java.awt.Color;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import com._17od.upm.crypto.InvalidPasswordException;
import com._17od.upm.database.AccountInformation;
import com._17od.upm.database.PasswordDatabase;
import com._17od.upm.database.ProblemReadingDatabaseFile;
import com._17od.upm.transport.Transport;
import com._17od.upm.transport.TransportException;
import com._17od.upm.util.Util;


public class DatabaseActions {

    private MainWindow mainWindow;
    private PasswordDatabase database;
    private ArrayList accountNames;
    private boolean localDatabaseDirty = true;


    public DatabaseActions(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

	
    /**
     * This method asks the user for the name of a new database and then creates
     * it. If the file already exists then the user is asked if they'd like to
     * overwrite it.
     * 
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws ProblemReadingDatabaseFile
     * @throws InvalidPasswordException
     * @throws IllegalBlockSizeException
     */
    public void newDatabase() throws IllegalBlockSizeException, IOException, GeneralSecurityException, ProblemReadingDatabaseFile, InvalidPasswordException {
    
        File newDatabaseFile = getSaveAsFile("New Password Database...");
        if (newDatabaseFile == null) {
            return;
        }
        
        JPasswordField masterPassword;
        boolean passwordsMatch = false;
        do {
        
            //Get a new master password for this database from the user
            
            masterPassword = new JPasswordField("");
            JPasswordField confirmedMasterPassword = new JPasswordField("");
            JOptionPane pane = new JOptionPane(new Object[] {"Please enter a master password for your new database...", masterPassword, "Confirmation...", confirmedMasterPassword},	JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            JDialog dialog = pane.createDialog(mainWindow, "Master Password...");
            dialog.show();
            
            if (pane.getValue().equals(new Integer(JOptionPane.OK_OPTION))) {
                if (!Arrays.equals(masterPassword.getPassword(), confirmedMasterPassword.getPassword())) {
                    JOptionPane.showMessageDialog(mainWindow, "The two passwords you entered don't match");
                } else {
                    passwordsMatch = true;
                }
            } else {
                return;
            }
        
        } while (passwordsMatch == false);
        
        if (newDatabaseFile.exists()) {
            newDatabaseFile.delete();
        }
        
        database = new PasswordDatabase(newDatabaseFile, masterPassword.getPassword());        
        saveDatabase();
        accountNames = new ArrayList();
        doOpenDatabaseActions();

    }


    public void changeMasterPassword() throws IllegalBlockSizeException, IOException, GeneralSecurityException, ProblemReadingDatabaseFile, HeadlessException, InvalidPasswordException, TransportException, PasswordDatabaseException {

        if (getLatestVersionOfDatabase()) {
        	//The first task is to get the current master password
            boolean passwordCorrect = false;
            boolean okClicked = true;
            do {
                char[] password = askUserForPassword("Please enter the master password for this database");
                if (password == null) {
                    okClicked = false;
                } else {
                    try {
                        new PasswordDatabase(database.getDatabaseFile(), password);
                        passwordCorrect = true;
                    } catch (InvalidPasswordException e) {
                        JOptionPane.showMessageDialog(mainWindow, "Incorrect password");
                    }
                }
            } while (!passwordCorrect && okClicked);
            
            //If the master password was entered correctly then the next step is to get the new master password
            if (passwordCorrect == true) {
    
        	        JPasswordField masterPassword;
        	        boolean passwordsMatch = false;
        	        Object buttonClicked;
        	        
        	        //Ask the user for the new master password
        	        //This loop will continue until the two passwords entered match or until the user hits the cancel button
        	        do {
        	
        	            masterPassword = new JPasswordField("");
        	            JPasswordField confirmedMasterPassword = new JPasswordField("");
        	            JOptionPane pane = new JOptionPane(new Object[] {"Please enter a new master password for your database...", masterPassword, "Confirmation...", confirmedMasterPassword},	JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        	            JDialog dialog = pane.createDialog(mainWindow, "Change Master Password...");
        	            dialog.show();
        	            
        	            buttonClicked = pane.getValue();
        	            if (buttonClicked.equals(new Integer(JOptionPane.OK_OPTION))) {
        	                if (!Arrays.equals(masterPassword.getPassword(), confirmedMasterPassword.getPassword())) {
        	                    JOptionPane.showMessageDialog(mainWindow, "The two passwords you entered don't match");
        	                } else {
        	                    passwordsMatch = true;
        	                }
        	            }
        	        
        	        } while (buttonClicked.equals(new Integer(JOptionPane.OK_OPTION)) && !passwordsMatch);
        	
        	        //If the user clicked OK and the passwords match then change the database password
        	        if (buttonClicked.equals(new Integer(JOptionPane.OK_OPTION)) && passwordsMatch) {
        		        database.changePassword(masterPassword.getPassword());
        		        saveDatabase();
        	        }
    
            }
        }
        
    }


    public void errorHandler(Exception e) {
        e.printStackTrace();
        String errorMessage = e.getMessage();
        if (errorMessage == null) {
            errorMessage = e.getClass().getName();
        }
        JOptionPane.showMessageDialog(mainWindow, errorMessage, "Error...", JOptionPane.ERROR_MESSAGE);
    }
    
    
    private void doOpenDatabaseActions() {
        mainWindow.getAddAccountButton().setEnabled(true);
        mainWindow.getAddAccountMenuItem().setEnabled(true);
        mainWindow.getSearchField().setEnabled(true);
        mainWindow.getSearchField().setText("");
        mainWindow.getSearchIcon().setEnabled(true);
        mainWindow.getResetSearchButton().setEnabled(true);
        mainWindow.getChangeMasterPasswordMenuItem().setEnabled(true);
        mainWindow.getDatabasePropertiesMenuItem().setEnabled(true);

        mainWindow.setTitle(database.getDatabaseFile() + " - " + MainWindow.getApplicationName());
        
        setLocalDatabaseDirty(true);

        accountNames = getAccountNames();
        populateListview(accountNames);
    }
    
    
    public ArrayList getAccountNames() {
        ArrayList dbAccounts = database.getAccounts();
        ArrayList accountNames = new ArrayList();
        for (int i=0; i<dbAccounts.size(); i++) {
            AccountInformation ai = (AccountInformation) dbAccounts.get(i);
            String accountName = (String) ai.getAccountName();
            accountNames.add(accountName);
        }
        return accountNames;
    }
    
    
    
    /**
     * Prompt the user to enter a password
     * @return The password entered by the user or null of this hit escape/cancel
     */
    private char[] askUserForPassword(String message) {
        char[] password = null; 
            
        JPasswordField masterPassword = new JPasswordField("");
        JOptionPane pane = new JOptionPane(new Object[] {message, masterPassword }, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = pane.createDialog(mainWindow, "Master Password...");
        dialog.show();
        
        if (pane.getValue() != null && pane.getValue().equals(new Integer(JOptionPane.OK_OPTION))) {
            password = masterPassword.getPassword();
        }
        
        return password;
    }
    

    public void openDatabase(String databaseFilename) throws IllegalBlockSizeException, IOException, GeneralSecurityException, ProblemReadingDatabaseFile {
        openDatabase(databaseFilename, null);
    }
    
    
    public void openDatabase(String databaseFilename, char[] password) throws IllegalBlockSizeException, IOException, GeneralSecurityException, ProblemReadingDatabaseFile {

        boolean passwordCorrect = false;
        boolean okClicked = true;
        while (!passwordCorrect && okClicked) {
            // If we weren't given a password then ask the user to enter one
            if (password == null) {
                password = askUserForPassword("Please enter the master password for this database");
                if (password == null) {
                    okClicked = false;
                }
            } else {
                okClicked = true;
            }
            
            if (okClicked) {
                try {
                    database = new PasswordDatabase(new File(databaseFilename), password);
                    passwordCorrect = true;
                } catch (InvalidPasswordException e) {
                    JOptionPane.showMessageDialog(mainWindow, "Incorrect password");
                    password = null;
                }
            }
        }
        
        if (passwordCorrect) {
            doOpenDatabaseActions();
        }

    }
    
    
    public void openDatabase() throws IllegalBlockSizeException, IOException, GeneralSecurityException, ProblemReadingDatabaseFile {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Open Password Database...");
        int returnVal = fc.showOpenDialog(mainWindow);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File databaseFile = fc.getSelectedFile();
            if (databaseFile.exists()) {
                openDatabase(databaseFile.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(mainWindow, "The file [" + databaseFile.getAbsolutePath() + "] doesn't exist", "File doesn't exist", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    

    public void deleteAccount() throws IOException, HeadlessException, GeneralSecurityException, ProblemReadingDatabaseFile, InvalidPasswordException, TransportException, PasswordDatabaseException {
        
        if (getLatestVersionOfDatabase()) {
            SortedListModel listview = (SortedListModel) mainWindow.getAccountsListview().getModel();
            String selectedAccName = (String) mainWindow.getAccountsListview().getSelectedValue();
    
            int buttonSelected = JOptionPane.showConfirmDialog(mainWindow, "Are you sure you want to delete the account [" + selectedAccName + "]", "Confirm delete account", JOptionPane.YES_NO_OPTION);
            if (buttonSelected == JOptionPane.OK_OPTION) {
                //Remove the account from the listview, accountNames arraylist & the database
                listview.removeElement(selectedAccName);
                int i = accountNames.indexOf(selectedAccName);
                accountNames.remove(i);
                database.deleteAccount(selectedAccName);
                saveDatabase();
                //[1375385] Call the filter method so that the listview is 
                //reinitialised with the remaining matching items
                filter();
            }
        }
            
    }
    
    
    public void addAccount() throws IOException, GeneralSecurityException, ProblemReadingDatabaseFile, InvalidPasswordException, TransportException, PasswordDatabaseException {
		
        if (getLatestVersionOfDatabase()) {

            //Initialise the AccountDialog
            AccountInformation accInfo = new AccountInformation();
            AccountDialog accDialog = new AccountDialog(accInfo, mainWindow, "Add Account", true, accountNames);
            accDialog.pack();
            accDialog.setLocationRelativeTo(mainWindow);
            accDialog.show();
    
            //If the user press OK then save the new account to the database
            if (accDialog.okClicked()) {
                database.deleteAccount(accInfo.getAccountName());
                database.addAccount(accInfo);
                saveDatabase();
                accountNames.add(accInfo.getAccountName());
                //[1375390] Ensure that the listview is properly filtered after an add
                filter();
            }
            
        }

    }

    
    public AccountInformation getSelectedAccount() {
        String selectedAccName = (String) mainWindow.getAccountsListview().getSelectedValue();
        return database.getAccount(selectedAccName);
    }


    private boolean getLatestVersionOfDatabase() throws IllegalBlockSizeException, IOException, GeneralSecurityException, ProblemReadingDatabaseFile, InvalidPasswordException, TransportException, PasswordDatabaseException {
        boolean latestVersionDownloaded = false;

        // Ensure we're working with the latest version of the database
        if (databaseHasRemoteInstance() && localDatabaseDirty) {
            int answer = JOptionPane.showConfirmDialog(mainWindow, "Sync with remote database?", "Sync Database?",JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                latestVersionDownloaded = syncWithRemoteDatabase();
            }
        } else {
            latestVersionDownloaded = true;
        }

        return latestVersionDownloaded;
    }
    
    
    private boolean databaseHasRemoteInstance() {
        if (database.getDbOptions().getRemoteLocation().equals("")) {
            return false;
        } else {
            return true;
        }
    }


    public void editAccount() throws IOException, GeneralSecurityException, ProblemReadingDatabaseFile, InvalidPasswordException, TransportException, PasswordDatabaseException {

        if (getLatestVersionOfDatabase()) {
            AccountInformation accInfo = getSelectedAccount();
            String selectedAccName = (String) accInfo.getAccountName();
            AccountDialog accDialog = new AccountDialog(accInfo, mainWindow, "Edit Account", false, accountNames);
            accDialog.pack();
            accDialog.setLocationRelativeTo(mainWindow);
            accDialog.show();

            //If the ok button was clicked then save the account to the database and update the 
            //listview with the new account name (if it's changed) 
            if (accDialog.okClicked() && accDialog.getAccountChanged()) {
                accInfo = accDialog.getAccount();
                database.deleteAccount(selectedAccName);
                database.addAccount(accInfo);
                saveDatabase();
                //If the new account name is different to the old account name then update the
                //accountNames array and refilter the listview  
                if (!accInfo.getAccountName().equals(selectedAccName)) {
                    int i = accountNames.indexOf(selectedAccName);
                    accountNames.remove(i);
                    accountNames.add(accInfo.getAccountName());
                    //[1375390] Ensure that the listview is properly filtered after an edit
                    filter();
                }
            }
        }

    }

    
    public void filter() {
        String filterStr = mainWindow.getSearchField().getText().toLowerCase();

        ArrayList filteredAccountsList = new ArrayList();
        for (int i=0; i<accountNames.size(); i++) {
            String accountName = (String) accountNames.get(i);
            if (filterStr.equals("") || accountName.toLowerCase().indexOf(filterStr) != -1) {
                filteredAccountsList.add(accountName);
            }
        }
        
        populateListview(filteredAccountsList);
        
        //If there's only one item in the listview then select it 
	    if (mainWindow.getAccountsListview().getModel().getSize() == 1) {
	    	mainWindow.getAccountsListview().setSelectedIndex(0);
	    }
    }


    public void populateListview(ArrayList accountNames) {
        SortedListModel listview = (SortedListModel) mainWindow.getAccountsListview().getModel();
        
        listview.clear();
        mainWindow.getAccountsListview().clearSelection();

        for (int i=0; i<accountNames.size(); i++) {
            listview.addElement(accountNames.get(i));
        }

        setButtonState();
    }

    
    public void setButtonState() {
    	if (mainWindow.getAccountsListview().getSelectedValue() == null) {
            mainWindow.getEditAccountButton().setEnabled(false);
            mainWindow.getCopyUsernameButton().setEnabled(false);
            mainWindow.getCopyPasswordButton().setEnabled(false);
            mainWindow.getDeleteAccountButton().setEnabled(false);
            mainWindow.getEditAccountMenuItem().setEnabled(false);
            mainWindow.getCopyUsernameMenuItem().setEnabled(false);
            mainWindow.getCopyPasswordMenuItem().setEnabled(false);
            mainWindow.getDeleteAccountMenuItem().setEnabled(false);
        } else {
            mainWindow.getEditAccountButton().setEnabled(true);
            mainWindow.getCopyUsernameButton().setEnabled(true);
            mainWindow.getCopyPasswordButton().setEnabled(true);
            mainWindow.getDeleteAccountButton().setEnabled(true);
            mainWindow.getEditAccountMenuItem().setEnabled(true);
            mainWindow.getCopyUsernameMenuItem().setEnabled(true);
            mainWindow.getCopyPasswordMenuItem().setEnabled(true);
            mainWindow.getDeleteAccountMenuItem().setEnabled(true);
        }
    }

	
    public void options() {
        OptionsDialog oppDialog = new OptionsDialog(mainWindow);
        oppDialog.pack();
        oppDialog.setLocationRelativeTo(mainWindow);
        oppDialog.show();
    }

    
    public void showAbout() {
        AboutDialog aboutDialog = new AboutDialog(mainWindow);
        aboutDialog.pack();
        aboutDialog.setLocationRelativeTo(mainWindow);
        aboutDialog.show();
    }

    
    public void resetSearch() {
        mainWindow.getSearchField().setText("");
    }
    
    
    private static void replaceDatabase(PasswordDatabase existingDatabase, PasswordDatabase newDatabase) throws PasswordDatabaseException {
        // Delete the existing database and then copy the new db into it's place
        String dbFileName = existingDatabase.getDatabaseFile().getAbsolutePath();
        boolean successful = existingDatabase.getDatabaseFile().delete();
        if (successful) {
            successful = newDatabase.getDatabaseFile().renameTo(new File(dbFileName));
            if (!successful) {
                throw new PasswordDatabaseException("Couldn't rename the existing password database (to make way for the new database) [" + existingDatabase.getDatabaseFile().getAbsolutePath() + "] to [" + existingDatabase.getDatabaseFile().getName() + ".tmp]");
            }
        } else {
            throw new PasswordDatabaseException("Couldn't delete the existing password database");
        }
    }

    
    public void showDatabaseProperties() throws IOException, GeneralSecurityException, ProblemReadingDatabaseFile, InvalidPasswordException, PasswordDatabaseException {
        try {
            if (getLatestVersionOfDatabase()) {
                DatabasePropertiesDialog dbPropsDialog = new DatabasePropertiesDialog(mainWindow, getAccountNames(), database);
                dbPropsDialog.pack();
                dbPropsDialog.setLocationRelativeTo(mainWindow);
                dbPropsDialog.show();
                if (dbPropsDialog.getDatabaseNeedsSaving()) {
                    saveDatabase();
                }
            }
        } catch (TransportException e) {
            int response = JOptionPane.showConfirmDialog(mainWindow, "Problem retrieving remote database. Would you like to make this database local only?", "Detach database from remote instance? ", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                database.getDbOptions().setRemoteLocation("");
                database.getDbOptions().setAuthDBEntry("");
                saveDatabase();
            }
        }
    }


    public void openDatabaseFromURL() throws TransportException, IOException, IllegalBlockSizeException, GeneralSecurityException, ProblemReadingDatabaseFile {
        
        // Ask the user for the remote database location
        OpenDatabaseFromURLDialog openDBDialog = new OpenDatabaseFromURLDialog(mainWindow);
        openDBDialog.pack();
        openDBDialog.setLocationRelativeTo(mainWindow);
        openDBDialog.show();
        
        if (openDBDialog.getOkClicked()) {
            // Get the remote database options
            String remoteLocation = openDBDialog.getUrlTextField().getText();
            String username = openDBDialog.getUsernameTextField().getText();
            String password = openDBDialog.getPasswordTextField().getText();

            // Ask the user for a location to save the database file to
            File saveDatabaseTo = getSaveAsFile("Save Database as...");
            
            if (saveDatabaseTo != null) {
                
                // Download the database
                Transport transport = Transport.getTransportForURL(new URL(remoteLocation));
                File downloadedDatabaseFile = transport.getRemoteFile(remoteLocation, username.getBytes(), password.getBytes());

                // Delete the file is it already exists
                if (saveDatabaseTo.exists()) {
                    saveDatabaseTo.delete();
                }

                // Save the downloaded database file to the new location
                Util.copy(downloadedDatabaseFile, saveDatabaseTo);
                
                // Now open the downloaded database 
                openDatabase(saveDatabaseTo.getAbsolutePath());
                
            }
        }
        
    }

    
    public boolean syncWithRemoteDatabase() throws TransportException, IllegalBlockSizeException, IOException, GeneralSecurityException, ProblemReadingDatabaseFile, PasswordDatabaseException {

        boolean syncSuccessful = false;
        
        // Get the remote database options
        String remoteLocation = database.getDbOptions().getRemoteLocation();
        String authDBEntry = database.getDbOptions().getAuthDBEntry();
        byte[] httpUsername = null;
        byte[] httpPassword = null;
        if (!authDBEntry.equals("")) {
            httpUsername = database.getAccount(authDBEntry).getUserId();
            httpPassword = database.getAccount(authDBEntry).getPassword();
        }

        // Download the database that's already at the remote location
        Transport transport = Transport.getTransportForURL(new URL(remoteLocation));
        File remoteDatabaseFile = transport.getRemoteFile(remoteLocation, database.getDatabaseFile().getName(), httpUsername, httpPassword);
        
        // Attempt to decrypt the database using the password the user entered
        PasswordDatabase remoteDatabase = null;
        char[] password = null;
        boolean successfullyDecryptedDb = false;
        try {
            remoteDatabase = new PasswordDatabase(remoteDatabaseFile, database.getPassword());
            successfullyDecryptedDb = true;
        } catch (InvalidPasswordException e) {
            // The password for the downloaded database is different to that of the open database
            // (most likely the user changed the local database's master password)
            boolean okClicked = false;
            do {
                password = askUserForPassword("Please enter the master password for the REMOTE database");
                if (password == null) {
                    okClicked = false;
                } else {
                    okClicked = true;
                    try {
                        remoteDatabase = new PasswordDatabase(remoteDatabaseFile, password);
                        successfullyDecryptedDb = true;
                    } catch (InvalidPasswordException invalidPassword) {
                        JOptionPane.showMessageDialog(mainWindow, "Incorrect password");
                    }
                }
            } while (okClicked && !successfullyDecryptedDb);
        }
                
        /* If the local database revision > remote database version => upload local database 
           If the local database revision < remote database version => replace local database with remote database
           If the local database revision = remote database version => do nothing */
        if (successfullyDecryptedDb) {
            if (database.getRevision() > remoteDatabase.getRevision()) {
                transport.delete(remoteLocation, database.getDatabaseFile().getName(), httpUsername, httpPassword);
                transport.put(remoteLocation, database.getDatabaseFile(), httpUsername, httpPassword);
                syncSuccessful = true;
            } else if (database.getRevision() < remoteDatabase.getRevision()) {
                replaceDatabase(database, remoteDatabase);
                openDatabase(database.getDatabaseFile().getAbsolutePath(), database.getPassword());
                syncSuccessful = true;
            } else {
                syncSuccessful = true;
            }

            if (syncSuccessful) {
                setLocalDatabaseDirty(false);
            }
        }

        return syncSuccessful;
        
    }

    
    public void exitApplication() throws IllegalBlockSizeException, IOException, GeneralSecurityException, ProblemReadingDatabaseFile, InvalidPasswordException, TransportException, PasswordDatabaseException {
        System.exit(0);
    }
    
    
    /**
     * This method prompts the user for the name of a file.
     * If the file exists then it will ask if they want to overwrite (the file isn't overwritten though,
     * that would be done by the calling method)
     * @param title The string title to put on the dialog
     * @return The file to save to or null
     */
    private File getSaveAsFile(String title) {
        File newDatabaseFile;
        
        boolean gotValidFile = false;
        do {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle(title);
            int returnVal = fc.showSaveDialog(mainWindow);
        
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return null;
            }
        
            newDatabaseFile = fc.getSelectedFile();
        
            //Warn the user if the database file already exists
            if (newDatabaseFile.exists()) {
                int i = JOptionPane.showConfirmDialog(mainWindow, "The file " + newDatabaseFile.getAbsolutePath() + " already exists.\nDo you want to overwrite it?", "File Already Exists...", JOptionPane.YES_NO_OPTION);
                if (i == JOptionPane.YES_OPTION) {
                    gotValidFile = true;
                }
            } else {
                gotValidFile = true;
            }
        
        } while (!gotValidFile);
        
        return newDatabaseFile;
    }
    
    
    private void saveDatabase() throws IllegalBlockSizeException, BadPaddingException, IOException {
        database.save();
        if (databaseHasRemoteInstance()) {
            setLocalDatabaseDirty(true);
        } else {
            setLocalDatabaseDirty(false);
        }
    }


    private void setLocalDatabaseDirty(boolean dirty) {
        localDatabaseDirty = dirty;

        if (databaseHasRemoteInstance()) {
            if (localDatabaseDirty) {
                mainWindow.getSyncWithRemoteDatabaseMenuItem().setEnabled(true);
                mainWindow.getSyncWithRemoteDatabaseButton().setEnabled(true);
            } else {
                mainWindow.getSyncWithRemoteDatabaseMenuItem().setEnabled(false);
                mainWindow.getSyncWithRemoteDatabaseButton().setEnabled(false);
            }
        } else {
            mainWindow.getSyncWithRemoteDatabaseMenuItem().setEnabled(false);
            mainWindow.getSyncWithRemoteDatabaseButton().setEnabled(false);
        }
        
        setStatusBarText();
    }
    
    
    private void setStatusBarText() {
        String status = null;
        Color color = null;
        if (databaseHasRemoteInstance()) {
            if (localDatabaseDirty) {
                status = "Unsynchronised";
                color = Color.RED;
            } else {
                status = "Synchronised";
                color = Color.BLACK;
            }
            status = "Revision " + String.valueOf(database.getRevision()) + " - " + status;
        } else {
            status = "Local Database";
            color = Color.BLACK;
        }
        mainWindow.getStatusBar().setText(status);
        mainWindow.getStatusBar().setForeground(color);
    }
    
}
