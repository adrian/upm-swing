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
package com._17od.upm.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com._17od.upm.crypto.CryptoException;
import com._17od.upm.crypto.InvalidPasswordException;
import com._17od.upm.database.AccountInformation;
import com._17od.upm.database.AccountsCSVMarshaller;
import com._17od.upm.database.ExportException;
import com._17od.upm.database.ImportException;
import com._17od.upm.database.PasswordDatabase;
import com._17od.upm.database.PasswordDatabasePersistence;
import com._17od.upm.database.ProblemReadingDatabaseFile;
import com._17od.upm.gui.MainWindow.ChangeDatabaseAction;
import com._17od.upm.transport.Transport;
import com._17od.upm.transport.TransportException;
import com._17od.upm.util.FileChangedCallback;
import com._17od.upm.util.FileMonitor;
import com._17od.upm.util.Preferences;
import com._17od.upm.util.Translator;
import com._17od.upm.util.Util;


public class DatabaseActions {

    private static Log LOG = LogFactory.getLog(DatabaseActions.class);

    private MainWindow mainWindow;
    private PasswordDatabase database;
    private ArrayList accountNames;
    private boolean localDatabaseDirty = true;
    private PasswordDatabasePersistence dbPers;
    private FileMonitor fileMonitor;
    private boolean databaseNeedsReload = false;

    private boolean lockIfInactive;
    private int msToWaitBeforeClosingDB;

    private boolean runSetDBDirtyThread = true;


    public DatabaseActions(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }


    /**
     * This method asks the user for the name of a new database and then creates
     * it. If the file already exists then the user is asked if they'd like to
     * overwrite it.
     * @throws CryptoException
     * @throws IOException
     */
    public void newDatabase() throws IOException, CryptoException {

        File newDatabaseFile = getSaveAsFile(Translator.translate("newPasswordDatabase"));
        if (newDatabaseFile == null) {
            return;
        }

        final JPasswordField masterPassword = new JPasswordField("");
        boolean passwordsMatch = false;
        do {

            //Get a new master password for this database from the user
            JPasswordField confirmedMasterPassword = new JPasswordField("");
            JOptionPane pane = new JOptionPane(new Object[] {Translator.translate("enterMasterPassword"), masterPassword, Translator.translate("confirmation"), confirmedMasterPassword}, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            JDialog dialog = pane.createDialog(mainWindow, Translator.translate("masterPassword"));
            dialog.addWindowFocusListener(new WindowAdapter() {
                public void windowGainedFocus(WindowEvent e) {
                    masterPassword.requestFocusInWindow();
                }
            });
            dialog.show();

            if (pane.getValue().equals(new Integer(JOptionPane.OK_OPTION))) {
                if (!Arrays.equals(masterPassword.getPassword(), confirmedMasterPassword.getPassword())) {
                    JOptionPane.showMessageDialog(mainWindow, Translator.translate("passwordsDontMatch"));
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

        database = new PasswordDatabase(newDatabaseFile);
        dbPers = new PasswordDatabasePersistence(masterPassword.getPassword());
        saveDatabase();
        accountNames = new ArrayList();
        doOpenDatabaseActions();

        // If a "Database to Load on Startup" hasn't been set yet then ask the
        // user if they'd like to open this database on startup.
        if (Preferences.get(Preferences.ApplicationOptions.DB_TO_LOAD_ON_STARTUP) == null ||
                Preferences.get(Preferences.ApplicationOptions.DB_TO_LOAD_ON_STARTUP).equals("")) {
            int option = JOptionPane.showConfirmDialog(mainWindow,
                    Translator.translate("setNewLoadOnStartupDatabase"),
                    Translator.translate("newPasswordDatabase"),
                    JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                Preferences.set(
                        Preferences.ApplicationOptions.DB_TO_LOAD_ON_STARTUP,
                        newDatabaseFile.getAbsolutePath());
                Preferences.save();
            }
        }
    }


    public void changeMasterPassword() throws IOException, ProblemReadingDatabaseFile, CryptoException, PasswordDatabaseException, TransportException {

        if (getLatestVersionOfDatabase()) {
            //The first task is to get the current master password
            boolean passwordCorrect = false;
            boolean okClicked = true;
            do {
                char[] password = askUserForPassword(Translator.translate("enterDatabasePassword"));
                if (password == null) {
                    okClicked = false;
                } else {
                    try {
                        dbPers.load(database.getDatabaseFile(), password);
                        passwordCorrect = true;
                    } catch (InvalidPasswordException e) {
                        JOptionPane.showMessageDialog(mainWindow, Translator.translate("incorrectPassword"));
                    }
                }
            } while (!passwordCorrect && okClicked);

            //If the master password was entered correctly then the next step is to get the new master password
            if (passwordCorrect == true) {

                    final JPasswordField masterPassword = new JPasswordField("");
                    boolean passwordsMatch = false;
                    Object buttonClicked;

                    //Ask the user for the new master password
                    //This loop will continue until the two passwords entered match or until the user hits the cancel button
                    do {


                        JPasswordField confirmedMasterPassword = new JPasswordField("");
                        JOptionPane pane = new JOptionPane(new Object[] {Translator.translate("enterNewMasterPassword"), masterPassword, Translator.translate("confirmation"), confirmedMasterPassword}, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
                        JDialog dialog = pane.createDialog(mainWindow, Translator.translate("changeMasterPassword"));
                        dialog.addWindowFocusListener(new WindowAdapter() {
                            public void windowGainedFocus(WindowEvent e) {
                                masterPassword.requestFocusInWindow();
                            }
                        });
                        dialog.show();

                        buttonClicked = pane.getValue();
                        if (buttonClicked.equals(new Integer(JOptionPane.OK_OPTION))) {
                            if (!Arrays.equals(masterPassword.getPassword(), confirmedMasterPassword.getPassword())) {
                                JOptionPane.showMessageDialog(mainWindow, Translator.translate("passwordsDontMatch"));
                            } else {
                                passwordsMatch = true;
                            }
                        }

                    } while (buttonClicked.equals(new Integer(JOptionPane.OK_OPTION)) && !passwordsMatch);

                    //If the user clicked OK and the passwords match then change the database password
                    if (buttonClicked.equals(new Integer(JOptionPane.OK_OPTION)) && passwordsMatch) {
                        this.dbPers.getEncryptionService().initCipher(masterPassword.getPassword());
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
        JOptionPane.showMessageDialog(mainWindow, errorMessage, Translator.translate("error"), JOptionPane.ERROR_MESSAGE);
    }

    private void doCloseDatabaseActions() {
        mainWindow.getAddAccountButton().setEnabled(false);
        mainWindow.getAddAccountMenuItem().setEnabled(false);
        mainWindow.getSearchField().setEnabled(false);
        mainWindow.getSearchField().setText("");
        mainWindow.getSearchIcon().setEnabled(false);
        mainWindow.getResetSearchButton().setEnabled(false);
        mainWindow.getChangeMasterPasswordMenuItem().setEnabled(false);
        mainWindow.getDatabasePropertiesMenuItem().setEnabled(false);
        mainWindow.getExportMenuItem().setEnabled(false);
        mainWindow.getImportMenuItem().setEnabled(false);

        mainWindow.setTitle(MainWindow.getApplicationName());

        mainWindow.getStatusBar().setText("");
        databaseNeedsReload = false;

        SortedListModel listview = (SortedListModel) mainWindow.getAccountsListview().getModel();
        listview.clear();

        mainWindow.getEditAccountButton().setEnabled(false);
        mainWindow.getCopyUsernameButton().setEnabled(false);
        mainWindow.getCopyPasswordButton().setEnabled(false);
        mainWindow.getLaunchURLButton().setEnabled(false);
        mainWindow.getDeleteAccountButton().setEnabled(false);
        mainWindow.getEditAccountMenuItem().setEnabled(false);
        mainWindow.getCopyUsernameMenuItem().setEnabled(false);
        mainWindow.getCopyPasswordMenuItem().setEnabled(false);
        mainWindow.getLaunchURLMenuItem().setEnabled(false);
        mainWindow.getDeleteAccountMenuItem().setEnabled(false);
        mainWindow.getViewAccountMenuItem().setEnabled(false);
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
        mainWindow.getExportMenuItem().setEnabled(true);
        mainWindow.getImportMenuItem().setEnabled(true);

        mainWindow.setTitle(database.getDatabaseFile() + " - " + MainWindow.getApplicationName());

        setLocalDatabaseDirty(true);
        databaseNeedsReload = false;

        accountNames = getAccountNames();
        populateListview(accountNames);

        // Start a thread to listen for changes to the db file
        FileChangedCallback callback = new FileChangedCallback() {
            public void fileChanged(File file) {
                databaseNeedsReload = true;
                mainWindow.setFileChangedPanelVisible(true);
            }
        };
        fileMonitor = new FileMonitor(database.getDatabaseFile(), callback);
        Thread thread = new Thread(fileMonitor);
        thread.start();

        // If the user asked for the db to close after a period of
        // inactivity then register a listener to capture window focus
        // events.
        configureAutoLock();

        // Give the search field focus.
        // I'm using requestFocusInWindow() rather than
        // requestFocus() because the javadocs recommend it.
        mainWindow.getSearchField().requestFocusInWindow();

        mainWindow.getDatabaseFileChangedPanel().setVisible(false);
    }

    private void configureAutoLock() {
        lockIfInactive = Preferences.get(
                Preferences.ApplicationOptions.DATABASE_AUTO_LOCK, "false").
                    equals("true");
        msToWaitBeforeClosingDB = Preferences.getInt(
                Preferences.ApplicationOptions.DATABASE_AUTO_LOCK_TIME, 5)
                    * 60 * 1000;

        if (lockIfInactive) {
            LOG.debug("Enabling autoclose when focus lost");
            if (mainWindow.getWindowFocusListeners().length == 0) {
                mainWindow.addWindowFocusListener(new AutoLockDatabaseListener());
            }
        } else {
            LOG.debug("Disabling autoclose when focus lost");
            for (int i=0; i<mainWindow.getWindowFocusListeners().length; i++) {
                mainWindow.removeWindowFocusListener(
                        mainWindow.getWindowFocusListeners()[i]);
            }
        }
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

        final JPasswordField masterPassword = new JPasswordField("");
        JOptionPane pane = new JOptionPane(new Object[] {message, masterPassword }, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = pane.createDialog(mainWindow, Translator.translate("masterPassword"));
        dialog.addWindowFocusListener(new WindowAdapter() {
            public void windowGainedFocus(WindowEvent e) {
                masterPassword.requestFocusInWindow();
            }
        });
        dialog.show();

        if (pane.getValue() != null && pane.getValue().equals(new Integer(JOptionPane.OK_OPTION))) {
            password = masterPassword.getPassword();
        }

        return password;
    }


    public void openDatabase(String databaseFilename) throws IOException, ProblemReadingDatabaseFile, CryptoException {
        openDatabase(databaseFilename, null);
    }


    public void openDatabase(String databaseFilename, char[] password) throws IOException, ProblemReadingDatabaseFile, CryptoException {

        boolean passwordCorrect = false;
        boolean okClicked = true;
        while (!passwordCorrect && okClicked) {
            // If we weren't given a password then ask the user to enter one
            if (password == null) {
                password = askUserForPassword(Translator.translate("enterDatabasePassword"));
                if (password == null) {
                    okClicked = false;
                }
            } else {
                okClicked = true;
            }

            if (okClicked) {
                try {
                    dbPers = new PasswordDatabasePersistence();
                    database = dbPers.load(new File(databaseFilename), password);
                    passwordCorrect = true;
                } catch (InvalidPasswordException e) {
                    JOptionPane.showMessageDialog(mainWindow, Translator.translate("incorrectPassword"));
                    password = null;
                }
            }
        }

        if (passwordCorrect) {
            doOpenDatabaseActions();
        }

    }


    public void openDatabase() throws IOException, ProblemReadingDatabaseFile, CryptoException {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(Translator.translate("openDatabase"));
        int returnVal = fc.showOpenDialog(mainWindow);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File databaseFile = fc.getSelectedFile();
            if (databaseFile.exists()) {
                openDatabase(databaseFile.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(mainWindow, Translator.translate("fileDoesntExistWithName", databaseFile.getAbsolutePath()), Translator.translate("fileDoesntExist"), JOptionPane.ERROR_MESSAGE);
            }
        }

        // Stop any "SetDBDirtyThread"s that are running
        runSetDBDirtyThread = false;
    }


    public void deleteAccount() throws IOException, CryptoException, TransportException, ProblemReadingDatabaseFile, PasswordDatabaseException {

        if (getLatestVersionOfDatabase()) {
            SortedListModel listview = (SortedListModel) mainWindow.getAccountsListview().getModel();
            String selectedAccName = (String) mainWindow.getAccountsListview().getSelectedValue();

            int buttonSelected = JOptionPane.showConfirmDialog(mainWindow, Translator.translate("askConfirmDeleteAccount", selectedAccName), Translator.translate("confirmDeleteAccount"), JOptionPane.YES_NO_OPTION);
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


    public void addAccount() throws IOException, CryptoException, TransportException, ProblemReadingDatabaseFile, PasswordDatabaseException {

        if (getLatestVersionOfDatabase()) {

            //Initialise the AccountDialog
            AccountInformation accInfo = new AccountInformation();
            AccountDialog accDialog = new AccountDialog(accInfo, mainWindow, false, accountNames);
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


    private boolean getLatestVersionOfDatabase() throws TransportException, ProblemReadingDatabaseFile, IOException, CryptoException, PasswordDatabaseException {
        boolean latestVersionDownloaded = false;

        // Ensure we're working with the latest version of the database
        if (databaseHasRemoteInstance() && localDatabaseDirty) {
            int answer = JOptionPane.showConfirmDialog(mainWindow, Translator.translate("askSyncWithRemoteDB"), Translator.translate("syncDatabase"), JOptionPane.YES_NO_OPTION);
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


    public void viewAccount() {
        AccountInformation accInfo = getSelectedAccount();
        AccountDialog accDialog = new AccountDialog(accInfo, mainWindow, true, accountNames);
        accDialog.pack();
        accDialog.setLocationRelativeTo(mainWindow);
        accDialog.show();
    }


    public void editAccount(String accountName) throws TransportException,
            ProblemReadingDatabaseFile, IOException, CryptoException,
            PasswordDatabaseException, InvalidPasswordException, UPMException {

        if (getLatestVersionOfDatabase()) {
            AccountInformation accInfo = database.getAccount(accountName);
            if (accInfo == null) {
                throw new UPMException(
                        Translator.translate(
                                "accountDoesntExist", accountName));
            }

            AccountDialog accDialog = new AccountDialog(accInfo, mainWindow, false, accountNames);
            accDialog.pack();
            accDialog.setLocationRelativeTo(mainWindow);
            accDialog.show();

            //If the ok button was clicked then save the account to the database and update the
            //listview with the new account name (if it's changed)
            if (accDialog.okClicked() && accDialog.getAccountChanged()) {
                accInfo = accDialog.getAccount();
                database.deleteAccount(accountName);
                database.addAccount(accInfo);
                //If the new account name is different to the old account name then update the
                //accountNames array and refilter the listview
                if (!accInfo.getAccountName().equals(accountName)) {
                    // User might change the account name for the Authentication Entry
                    // so this has to be checked
                    if (accountName.equals(database.getDbOptions().getAuthDBEntry())) {
                        database.getDbOptions().setAuthDBEntry(accInfo.getAccountName());
                    }
                    int i = accountNames.indexOf(accountName);
                    accountNames.remove(i);
                    accountNames.add(accInfo.getAccountName());
                    //[1375390] Ensure that the listview is properly filtered after an edit
                    filter();
                }
                saveDatabase();
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
            mainWindow.getLaunchURLButton().setEnabled(false);
            mainWindow.getDeleteAccountButton().setEnabled(false);
            mainWindow.getEditAccountMenuItem().setEnabled(false);
            mainWindow.getCopyUsernameMenuItem().setEnabled(false);
            mainWindow.getCopyPasswordMenuItem().setEnabled(false);
            mainWindow.getLaunchURLMenuItem().setEnabled(false);
            mainWindow.getDeleteAccountMenuItem().setEnabled(false);
            mainWindow.getViewAccountMenuItem().setEnabled(false);
        } else {
            mainWindow.getEditAccountButton().setEnabled(true);
            mainWindow.getCopyUsernameButton().setEnabled(true);
            mainWindow.getCopyPasswordButton().setEnabled(true);
            mainWindow.getLaunchURLButton().setEnabled(true);
            mainWindow.getDeleteAccountButton().setEnabled(true);
            mainWindow.getEditAccountMenuItem().setEnabled(true);
            mainWindow.getCopyUsernameMenuItem().setEnabled(true);
            mainWindow.getCopyPasswordMenuItem().setEnabled(true);
            mainWindow.getLaunchURLMenuItem().setEnabled(true);
            mainWindow.getDeleteAccountMenuItem().setEnabled(true);
            mainWindow.getViewAccountMenuItem().setEnabled(true);
        }
    }


    public void options() {
        OptionsDialog oppDialog = new OptionsDialog(mainWindow);
        oppDialog.pack();
        oppDialog.setLocationRelativeTo(mainWindow);
        oppDialog.show();

        configureAutoLock();

        if (oppDialog.hasLanguageChanged()) {
            mainWindow.initialiseControlsWithDefaultLanguage();
            if (database != null) {
                setStatusBarText();
            }
        }
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


    public void showDatabaseProperties() throws ProblemReadingDatabaseFile, IOException, CryptoException, PasswordDatabaseException {
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
            int response = JOptionPane.showConfirmDialog(mainWindow, Translator.translate("problemRetrievingRemoteDB"), Translator.translate("detachDatabase"), JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                database.getDbOptions().setRemoteLocation("");
                database.getDbOptions().setAuthDBEntry("");
                saveDatabase();
            }
        }
    }


    public void openDatabaseFromURL() throws TransportException, IOException, ProblemReadingDatabaseFile, CryptoException {

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
            File saveDatabaseTo = getSaveAsFile(Translator.translate("saveDatabaseAs"));

            if (saveDatabaseTo != null) {

                // Download the database
                Transport transport = Transport.getTransportForURL(new URL(remoteLocation));
                File downloadedDatabaseFile = transport.getRemoteFile(remoteLocation, username, password);

                // Delete the file is it already exists
                if (saveDatabaseTo.exists()) {
                    saveDatabaseTo.delete();
                }

                // Save the downloaded database file to the new location
                Util.copyFile(downloadedDatabaseFile, saveDatabaseTo);

                // Now open the downloaded database
                openDatabase(saveDatabaseTo.getAbsolutePath());

            }
        }

    }

    public void reloadDatabase()
            throws InvalidPasswordException, ProblemReadingDatabaseFile, IOException {
        PasswordDatabase reloadedDb = null;
        try {
            reloadedDb = dbPers.load(database.getDatabaseFile());
        } catch (InvalidPasswordException e) {
            // The password for the reloaded database is different to that of
            // the open database
            boolean okClicked = false;
            do {
                char[] password = askUserForPassword(Translator.translate("enterDatabasePassword"));
                if (password == null) {
                    okClicked = false;
                } else {
                    okClicked = true;
                    try {
                        reloadedDb = dbPers.load(database.getDatabaseFile(), password);
                    } catch (InvalidPasswordException invalidPassword) {
                        JOptionPane.showMessageDialog(mainWindow, Translator.translate("incorrectPassword"));
                    } catch (CryptoException e1) {
                        errorHandler(e);
                    }
                }
            } while (okClicked && reloadedDb == null);
        }

        if (reloadedDb != null) {
            database = reloadedDb;
            doOpenDatabaseActions();
        }
    }

    public void reloadDatabaseBefore(ChangeDatabaseAction editAction)
            throws InvalidPasswordException, ProblemReadingDatabaseFile,
            IOException {
        boolean proceedWithAction = false;
        if (this.databaseNeedsReload) {
            int answer = JOptionPane.showConfirmDialog(mainWindow,
                    Translator.translate("askReloadDatabase"),
                    Translator.translate("reloadDatabase"),
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                proceedWithAction = reloadDatabaseFromDisk();
            }
        } else {
            proceedWithAction = true;
        }

        if (proceedWithAction) {
            editAction.doAction();
        }
    }

    public boolean reloadDatabaseFromDisk() throws InvalidPasswordException,
            ProblemReadingDatabaseFile, IOException {
        boolean reloadSuccessful = false;

        PasswordDatabase reloadedDb = null;
        try {
            reloadedDb = dbPers.load(database.getDatabaseFile());
        } catch (InvalidPasswordException e) {
            // The password for the reloaded database is different to that of
            // the open database
            boolean okClicked = false;
            do {
                char[] password = askUserForPassword(Translator
                        .translate("enterDatabasePassword"));
                if (password == null) {
                    okClicked = false;
                } else {
                    okClicked = true;
                    try {
                        reloadedDb = dbPers.load(database.getDatabaseFile(),
                                password);
                    } catch (InvalidPasswordException invalidPassword) {
                        JOptionPane.showMessageDialog(mainWindow,
                                Translator.translate("incorrectPassword"));
                    } catch (CryptoException e1) {
                        errorHandler(e);
                    }
                }
            } while (okClicked && reloadedDb == null);
        }

        if (reloadedDb != null) {
            database = reloadedDb;
            doOpenDatabaseActions();
            reloadSuccessful = true;
        }

        return reloadSuccessful;
    }

    public boolean syncWithRemoteDatabase() throws TransportException, ProblemReadingDatabaseFile, IOException, CryptoException, PasswordDatabaseException {

        boolean syncSuccessful = false;

        try {
            fileMonitor.pause();

            mainWindow.getContentPane().setCursor(new Cursor(Cursor.WAIT_CURSOR));

            // Get the remote database options
            String remoteLocation = database.getDbOptions().getRemoteLocation();
            String authDBEntry = database.getDbOptions().getAuthDBEntry();
            String httpUsername = null;
            String httpPassword = null;
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
                remoteDatabase = dbPers.load(remoteDatabaseFile);
                successfullyDecryptedDb = true;
            } catch (InvalidPasswordException e) {
                // The password for the downloaded database is different to that of the open database
                // (most likely the user changed the local database's master password)
                boolean okClicked = false;
                do {
                    password = askUserForPassword(Translator.translate("enterPaswordForRemoteDB"));
                    if (password == null) {
                        okClicked = false;
                    } else {
                        okClicked = true;
                        try {
                            remoteDatabase = dbPers.load(remoteDatabaseFile, password);
                            successfullyDecryptedDb = true;
                        } catch (InvalidPasswordException invalidPassword) {
                            JOptionPane.showMessageDialog(mainWindow, Translator.translate("incorrectPassword"));
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
                    Util.copyFile(remoteDatabaseFile, database.getDatabaseFile());
                    database = new PasswordDatabase(
                            remoteDatabase.getRevisionObj(),
                            remoteDatabase.getDbOptions(),
                            remoteDatabase.getAccountsHash(),
                            database.getDatabaseFile());
                    doOpenDatabaseActions();
                    syncSuccessful = true;
                } else {
                    syncSuccessful = true;
                }

                if (syncSuccessful) {
                    setLocalDatabaseDirty(false);

                    // Create a thread that will mark the database dirty after
                    // a short period. Without this the database would remain
                    // in a synced state until the user makes a change. The
                    // longer we wait before syncing up the greater chance there
                    // is that we'll miss changes made elsewhere and end up
                    // with a conflicting version of the database.
                    final long dirtyThreadStartTime = System.currentTimeMillis();
                    runSetDBDirtyThread = true;
                    Thread setDBDirtyThread = new Thread(new Runnable() {
                        public void run() {
                            while (runSetDBDirtyThread) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e1) {}
                                long currentTime = System.currentTimeMillis();
                                if (currentTime - dirtyThreadStartTime > 5 * 60 * 1000) {
                                    LOG.info("SetDBDirtyThread setting database dirty");
                                    setLocalDatabaseDirty(true);
                                    runSetDBDirtyThread = false;
                                }
                            }
                        }
                    });
                    setDBDirtyThread.setName("SetDBDirty");
                    setDBDirtyThread.start();
                    LOG.info("Started SetDBDirtyThread thread");

                }
            }

        } finally {
            mainWindow.getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            fileMonitor.start();
        }

        return syncSuccessful;

    }


    public void exitApplication() {
        System.exit(0);
    }


    public void export() {
        File exportFile = getSaveAsFile(Translator.translate("exportFile"));
        if (exportFile == null) {
            return;
        }

        if (exportFile.exists()) {
            exportFile.delete();
        }

        AccountsCSVMarshaller marshaller = new AccountsCSVMarshaller();
        try {
            marshaller.marshal(this.database.getAccounts(), exportFile);
        } catch (ExportException e) {
            JOptionPane.showMessageDialog(mainWindow, e.getMessage(), Translator.translate("problemExporting"), JOptionPane.ERROR_MESSAGE);
        }
    }


    public void importAccounts() throws TransportException, ProblemReadingDatabaseFile, IOException, CryptoException, PasswordDatabaseException {
        if (getLatestVersionOfDatabase()) {
            // Prompt for the file to import
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle(Translator.translate("import"));
            int returnVal = fc.showOpenDialog(mainWindow);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File csvFile = fc.getSelectedFile();

                // Unmarshall the accounts from the CSV file
                try {
                    AccountsCSVMarshaller marshaller = new AccountsCSVMarshaller();
                    ArrayList accountsInCSVFile = marshaller.unmarshal(csvFile);
                    ArrayList accountsToImport = new ArrayList();

                    boolean importCancelled = false;
                    // Add each account to the open database. If the account
                    // already exits the prompt to overwrite
                    for (int i=0; i<accountsInCSVFile.size(); i++) {
                        AccountInformation importedAccount = (AccountInformation) accountsInCSVFile.get(i);
                        if (database.getAccount(importedAccount.getAccountName()) != null) {
                            Object[] options = {"Overwrite Existing", "Keep Existing", "Cancel"};
                            int answer = JOptionPane.showOptionDialog(
                                    mainWindow,
                                    Translator.translate("importExistingQuestion", importedAccount.getAccountName()),
                                    Translator.translate("importExistingTitle"),
                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    options,
                                    options[1]);

                            if (answer == 1) {
                                continue; // If keep existing then continue to the next iteration
                            } else if (answer == 2) {
                                importCancelled = true;
                                break; // Cancel the import
                            }
                        }

                        accountsToImport.add(importedAccount);
                    }

                    if (!importCancelled && accountsToImport.size() > 0) {
                        for (int i=0; i<accountsToImport.size(); i++) {
                            AccountInformation accountToImport = (AccountInformation) accountsToImport.get(i);
                            database.deleteAccount(accountToImport.getAccountName());
                            database.addAccount(accountToImport);
                        }
                        saveDatabase();
                        accountNames = getAccountNames();
                        filter();
                    }

                } catch (ImportException e) {
                    JOptionPane.showMessageDialog(mainWindow, e.getMessage(), Translator.translate("problemImporting"), JOptionPane.ERROR_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(mainWindow, e.getMessage(), Translator.translate("problemImporting"), JOptionPane.ERROR_MESSAGE);
                } catch (CryptoException e) {
                    JOptionPane.showMessageDialog(mainWindow, e.getMessage(), Translator.translate("problemImporting"), JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }


    /**
     * This method prompts the user for the name of a file.
     * If the file exists then it will ask if they want to overwrite (the file isn't overwritten though,
     * that would be done by the calling method)
     * @param title The string title to put on the dialog
     * @return The file to save to or null
     */
    private File getSaveAsFile(String title) {
        File selectedFile;

        boolean gotValidFile = false;
        do {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle(title);
            int returnVal = fc.showSaveDialog(mainWindow);

            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return null;
            }

            selectedFile = fc.getSelectedFile();

            //Warn the user if the database file already exists
            if (selectedFile.exists()) {
                Object[] options = {"Yes", "No"};
                int i = JOptionPane.showOptionDialog(mainWindow,
                        Translator.translate("fileAlreadyExistsWithFileName", selectedFile.getAbsolutePath()) + '\n' +
                            Translator.translate("overwrite"),
                            Translator.translate("fileAlreadyExists"),
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1]);
                if (i == JOptionPane.YES_OPTION) {
                    gotValidFile = true;
                }
            } else {
                gotValidFile = true;
            }

        } while (!gotValidFile);

        return selectedFile;
    }


    private void saveDatabase() throws IOException, CryptoException {
        dbPers.save(database);
        if (fileMonitor != null) {
            fileMonitor.start();
        }
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
                status = Translator.translate("unsynchronised");
                color = Color.RED;
            } else {
                status = Translator.translate("synchronised");
                color = Color.BLACK;
            }
            status = Translator.translate("revision") + ' ' + String.valueOf(database.getRevision()) + " - " + status;
        } else {
            status = Translator.translate("localDatabase");
            color = Color.BLACK;
        }
        mainWindow.getStatusBar().setText(status);
        mainWindow.getStatusBar().setForeground(color);
    }


    private class AutoLockDatabaseListener implements WindowFocusListener {

        private String databaseClosedOnTimer;
        private Timer closeDBTimer;

        public synchronized void windowGainedFocus(WindowEvent we) {
            if (closeDBTimer != null) {
                LOG.debug("Stopping closeDBTimer");
                closeDBTimer.removeActionListener(
                        closeDBTimer.getActionListeners()[0]);
                closeDBTimer = null;
            }
            if (databaseClosedOnTimer != null) {
                try {
                    openDatabase(databaseClosedOnTimer);
                } catch (Exception e) {
                    errorHandler(e);
                }
                databaseClosedOnTimer = null;
            }
        }

        /**
         * If the app loses focus, there's an open db and there's no closeDBTimer
         * already registered then start a timer to close the database after the
         * configured number of minutes.
         */
        public synchronized void windowLostFocus(WindowEvent e) {
            // If the window receiving focus is within this application then the
            // app isn't not losing focus so no further action is required.
            if (e.getOppositeWindow() != null &&
                    e.getOppositeWindow().getOwner() == mainWindow) {
                LOG.debug("Focus switched to another window within this app");
                return;
            }

            if (database != null && closeDBTimer == null){
                closeDBTimer = new Timer(msToWaitBeforeClosingDB , null);
                closeDBTimer.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        LOG.debug("Closing database due to inactivity");
                        databaseClosedOnTimer =
                                database.getDatabaseFile().getAbsolutePath();
                        doCloseDatabaseActions();
                        database = null;
                        closeDBTimer = null;
                    }
                });
                closeDBTimer.setRepeats(false);
                closeDBTimer.start();
                LOG.debug("Started lost focus timer, " + msToWaitBeforeClosingDB);
            }
        }

    }
}
