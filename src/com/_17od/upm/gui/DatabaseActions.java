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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
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


public class DatabaseActions implements ActionListener {

    private MainWindow mainWindow;
    private PasswordDatabase database;
    private ArrayList accountNames;
    
    	
    public DatabaseActions(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

	
    public void actionPerformed(ActionEvent event) {
        try {
            if (event.getActionCommand() == MainWindow.NEW_DATABASE_TXT) {
                newDatabase();
            } else if (event.getActionCommand() == MainWindow.OPEN_DATABASE_TXT) {
                openDatabase();
            } else if (event.getActionCommand() == MainWindow.ADD_ACCOUNT_TXT) {
                addAccount();
            } else if (event.getActionCommand() == MainWindow.EDIT_ACCOUNT_TXT) {
                editAccount();
            } else if (event.getActionCommand() == MainWindow.DELETE_ACCOUNT_TXT) {
                deleteAccount();
            } else if (event.getActionCommand() == MainWindow.OPTIONS_TXT) {
                options();
            } else if (event.getActionCommand() == MainWindow.ABOUT_TXT) {
                showAbout();
            } else if (event.getActionCommand() == MainWindow.RESET_SEARCH_TXT) {
                resetSearch();
            } else if (event.getActionCommand() == MainWindow.CHANGE_MASTER_PASSWORD_TXT) {
                changeMasterPassword();
            }
        } catch (Exception e) {
            errorHandler(e);
        }
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
    private void newDatabase() throws IllegalBlockSizeException, IOException, GeneralSecurityException, ProblemReadingDatabaseFile, InvalidPasswordException {
    
        File newDatabaseFile;
        boolean gotValidFile = false;
        do {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("New Password Database...");
            int returnVal = fc.showSaveDialog(mainWindow);
        
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
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
        database.save();
        accountNames = new ArrayList();
        doOpenDatabaseActions();
    
    }


    private void changeMasterPassword() throws IllegalBlockSizeException, IOException, GeneralSecurityException, ProblemReadingDatabaseFile {

    	//The first task is to get the current master password
        boolean passwordCorrect = false;
        boolean okClicked = true;
        do {
            JPasswordField masterPassword = new JPasswordField("");
            JOptionPane pane = new JOptionPane(new Object[] {"Please enter your master password", masterPassword }, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            JDialog dialog = pane.createDialog(mainWindow, "Master Password...");
            dialog.show();
            
            if (pane.getValue().equals(new Integer(JOptionPane.OK_OPTION))) {
                try {
                    new PasswordDatabase(database.getDatabaseFile(), masterPassword.getPassword());
                    passwordCorrect = true;
                } catch (InvalidPasswordException e) {
                    JOptionPane.showMessageDialog(mainWindow, "Incorrect password");
                }
            } else {
                okClicked = false;
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
		        database.save();
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
        mainWindow.setTitle(MainWindow.getApplicationName() + " - " + database.getDatabaseFile());
        mainWindow.getSearchField().setText("");
        mainWindow.getSearchIcon().setEnabled(true);
        mainWindow.getResetSearchButton().setEnabled(true);
        mainWindow.getChangeMasterPasswordMenuItem().setEnabled(true);

        accountNames = new ArrayList();
        ArrayList dbAccounts = database.getAccounts();
        for (int i=0; i<dbAccounts.size(); i++) {
            AccountInformation ai = (AccountInformation) dbAccounts.get(i);
            String accountName = (String) ai.getAccountName();
            accountNames.add(accountName);
        }

        populateListview(accountNames);
    }
    
    
    public void openDatabase(String databaseFilename) throws IllegalBlockSizeException, IOException, GeneralSecurityException, ProblemReadingDatabaseFile {

        boolean passwordCorrect = false;
        boolean okClicked = true;
        while (!passwordCorrect && okClicked) {
            JPasswordField masterPassword = new JPasswordField("");
            JOptionPane pane = new JOptionPane(new Object[] {"Please enter your master password", masterPassword }, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            JDialog dialog = pane.createDialog(mainWindow, "Master Password...");
            dialog.show();
            
            if (pane.getValue().equals(new Integer(JOptionPane.OK_OPTION))) {
                try {
                    database = new PasswordDatabase(new File(databaseFilename), masterPassword.getPassword());
                    passwordCorrect = true;
                } catch (InvalidPasswordException e) {
                    JOptionPane.showMessageDialog(mainWindow, "Incorrect password");
                }
            } else {
                okClicked = false;
            }
        }
        
        if (passwordCorrect == true) {
            doOpenDatabaseActions();
        }

    }
    
    
    private void openDatabase() throws IllegalBlockSizeException, IOException, GeneralSecurityException, ProblemReadingDatabaseFile {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Open Password Database...");
        int returnVal = fc.showOpenDialog(mainWindow);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File databaseFile = fc.getSelectedFile();
            openDatabase(databaseFile.getAbsolutePath());
        }
    
    }
    

    public void deleteAccount() throws IllegalBlockSizeException, BadPaddingException, IOException {
        SortedListModel listview = (SortedListModel) mainWindow.getAccountsListview().getModel();
        String selectedAccName = (String) mainWindow.getAccountsListview().getSelectedValue();

        int buttonSelected = JOptionPane.showConfirmDialog(mainWindow, "Are you sure you want to delete the account [" + selectedAccName + "]", "Confirm delete account", JOptionPane.YES_NO_OPTION);
        if (buttonSelected == JOptionPane.OK_OPTION) {
            //Remove the account from the listview, accountNames arraylist & the database
            listview.removeElement(selectedAccName);
            int i = accountNames.indexOf(selectedAccName);
            accountNames.remove(i);
            database.deleteAccount(selectedAccName);
            database.save();
            //[1375385] Call the filter method so that the listview is 
            //reinitialised with the remaining matching items
            filter();
        }
    }
    
    
    public void addAccount() throws IllegalBlockSizeException, BadPaddingException, IOException {
		
        //Initialise the AccountDialog
        AccountInformation accInfo = new AccountInformation();
        AccountDialog accDialog = new AccountDialog(accInfo, mainWindow, "Add Account", true);
        accDialog.pack();
        accDialog.setLocationRelativeTo(mainWindow);

        //Keep looping until the user provides a non-existant account or they hit cancel
        boolean validAccount = false;
        do {
            accDialog.show();
            if (accDialog.okClicked()) {
                accInfo = accDialog.getAccount();
                //[1375397] Ensure that an account with the name name doesn't already exist
                //(by checking 'accountNames' we're checking both visible and filtered accounts 
                if (accountNames.indexOf(accInfo.getAccountName()) != -1) {
                    JOptionPane.showMessageDialog(mainWindow, "An account with the name [" + accInfo.getAccountName() + "] already exists", "Account already exists...", JOptionPane.ERROR_MESSAGE);
                } else {
                    validAccount = true;
                }
            }
        } while (!validAccount && accDialog.okClicked());

        //If the user press OK then save the new account to the database
        if (accDialog.okClicked()) {
            database.deleteAccount(accInfo.getAccountName());
            database.addAccount(accInfo);
            database.save();
            accountNames.add(accInfo.getAccountName());
            //[1375390] Ensure that the listview is properly filtered after an add
            filter();
        }

    }

    
    public AccountInformation getSelectedAccount() {
        String selectedAccName = (String) mainWindow.getAccountsListview().getSelectedValue();
        return database.getAccount(selectedAccName);
    }
    
    
    private void editAccount() throws IllegalBlockSizeException, BadPaddingException, IOException {

        AccountInformation accInfo = getSelectedAccount();
        String selectedAccName = (String) accInfo.getAccountName();
        AccountDialog accDialog = new AccountDialog(accInfo, mainWindow, "Edit Account", true);
        accDialog.pack();
        accDialog.setLocationRelativeTo(mainWindow);
        accDialog.show();

        //If the ok button was clicked then save the account to the database and update the 
        //listview with the new account name (if it's changed) 
        if (accDialog.okClicked()) {
            accInfo = accDialog.getAccount();
            database.deleteAccount(selectedAccName);
            database.addAccount(accInfo);
            database.save();
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

    
    private void showAbout() {
        AboutDialog aboutDialog = new AboutDialog(mainWindow);
        aboutDialog.pack();
        aboutDialog.setLocationRelativeTo(mainWindow);
        aboutDialog.show();
    }

    
    public void resetSearch() {
        mainWindow.getSearchField().setText("");
    }
    
}