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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;

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

	
    public void errorHandler(Exception e) {
        e.printStackTrace();
        String errorMessage = e.getMessage();
        if (errorMessage == null) {
            errorMessage = e.getClass().getName();
        }
        JOptionPane.showMessageDialog(mainWindow, errorMessage, "Error...", JOptionPane.ERROR_MESSAGE);
    }
    
    
    private void doOpenDatabaseActions() {
        mainWindow.getNewAccountButton().setEnabled(true);
        mainWindow.getSearchField().setEnabled(true);
        mainWindow.setTitle(mainWindow.getTitle() + " - " + database.getDatabaseFile());
        mainWindow.getSearchField().setText("");
        mainWindow.getSearchIcon().setEnabled(true);
        mainWindow.getResetSearchButton().setEnabled(true);

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
        DefaultListModel listview = (DefaultListModel) mainWindow.getAccountsListview().getModel();
        String selectedAccName = (String) mainWindow.getAccountsListview().getSelectedValue();

        int buttonSelected = JOptionPane.showConfirmDialog(mainWindow, "Are you sure you want to delete the account [" + selectedAccName + "]", "Confirm delete account", JOptionPane.YES_NO_OPTION);
        if (buttonSelected == JOptionPane.OK_OPTION) {
            //Remove the account from the listview, accountNames arraylist & the database
            int i = listview.indexOf(selectedAccName);
            listview.remove(i);
            i = accountNames.indexOf(selectedAccName);
            accountNames.remove(i);
            database.deleteAccount(selectedAccName);
            database.save();
            setButtonState();
        }
    }
    
    
    public void addAccount() throws IllegalBlockSizeException, BadPaddingException, IOException {
		
        DefaultListModel listview = (DefaultListModel) mainWindow.getAccountsListview().getModel();

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
                if (listview.contains(accInfo.getAccountName())) {
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
            populateListview(accountNames);
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
        DefaultListModel listview = (DefaultListModel) mainWindow.getAccountsListview().getModel();
        if (accDialog.okClicked()) {
            accInfo = accDialog.getAccount();
            database.deleteAccount(selectedAccName);
            database.addAccount(accInfo);
            database.save();
            if (!accInfo.getAccountName().equals(selectedAccName)) {
                int i = listview.indexOf(selectedAccName);
                listview.remove(i);
                listview.insertElementAt(accInfo.getAccountName(), i);
                i = accountNames.indexOf(selectedAccName);
                accountNames.remove(i);
                accountNames.add(accInfo.getAccountName());
                populateListview(accountNames);
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
    }


    public void populateListview(ArrayList accountNames) {
        DefaultListModel listview = (DefaultListModel) mainWindow.getAccountsListview().getModel();
        listview.clear();

        Collections.sort(accountNames);

        for (int i=0; i<accountNames.size(); i++) {
            listview.addElement(accountNames.get(i));
        }

        setButtonState();
    }

    
    public void setButtonState() {
        if (mainWindow.getAccountsListview().getSelectedValue() == null || mainWindow.getAccountsListview().getSelectedValue().equals("")) {
            mainWindow.getEditAccountButton().setEnabled(false);
            mainWindow.getCopyUsernameButton().setEnabled(false);
            mainWindow.getCopyPasswordButton().setEnabled(false);
            mainWindow.getDeleteAccountButton().setEnabled(false);
        } else {
            mainWindow.getEditAccountButton().setEnabled(true);
            mainWindow.getCopyUsernameButton().setEnabled(true);
            mainWindow.getCopyPasswordButton().setEnabled(true);
            mainWindow.getDeleteAccountButton().setEnabled(true);
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