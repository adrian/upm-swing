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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.crypto.IllegalBlockSizeException;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com._17od.upm.database.AccountInformation;
import com._17od.upm.database.ProblemReadingDatabaseFile;
import com._17od.upm.util.Preferences;


/**
 * This is the main application entry class
 */
public class MainWindow extends JFrame {

    private static final String applicationName = "Universal Password Manager";
    private static String iconsDir = new File("images").getAbsolutePath();
    
    public static final String NEW_DATABASE_TXT = "New Database";
    public static final String OPEN_DATABASE_TXT = "Open Database";
    public static final String ADD_ACCOUNT_TXT = "Add Account";
    public static final String EDIT_ACCOUNT_TXT = "Edit Account";
    public static final String DELETE_ACCOUNT_TXT = "Delete Account";
    public static final String OPTIONS_TXT = "Options";
    public static final String ABOUT_TXT = "About";

    private JButton newAccountButton;
    private JButton editAccountButton;
    private JButton deleteAccountButton;
    private JButton copyUsernameButton;
    private JButton copyPasswordButton;
    private JButton optionsButton;
    private JTextField searchField;
    
    private JMenuItem newDatabaseMenuItem;
    private JMenuItem openDatabaseMenuItem;
    private JMenuItem exitMenuItem;
    private JMenuItem aboutMenuItem;
    private JMenu fileMenu;
    private JMenu helpMenu;
    private JList accountsListview;
    
    private DatabaseActions dbActions;
    
    
    public MainWindow(String title) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IllegalBlockSizeException, IOException, GeneralSecurityException, ProblemReadingDatabaseFile {
    		super(title);

    		setIconImage(new ImageIcon(iconsDir + "/upm.gif").getImage());
            
        //Use the System look and feel
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        dbActions = new DatabaseActions(this);

        //Set up the content pane.
        addComponentsToPane();

        //Display the window.
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        try {
            //Load the startup database if it's configured
            String db = Preferences.getDBToOptionOnStartup();
            if (db != null && !db.equals("")) {
	            File dbFile = new File(db);
	            if (!dbFile.exists()) {
	            	dbActions.errorHandler(new Exception("The startup database [" + db + "] does not exist"));
	            } else {
	            	dbActions.openDatabase(db);
	            }
            }
        } catch (Exception e) {
            dbActions.errorHandler(e);
        }
        
        //Give the search field focus
        searchField.requestFocus();
    }
   
   
    public static String getIconsDir() {
        return iconsDir;
    }
    
    
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    new MainWindow(applicationName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    
    private void addComponentsToPane() {

        //Ensure the layout manager is a BorderLayout
        if (!(getContentPane().getLayout() instanceof GridBagLayout)) {
        		getContentPane().setLayout(new GridBagLayout());
        }

        //Create the menubar
        setJMenuBar(createMenuBar());
        
        GridBagConstraints c = new GridBagConstraints();

        //The toolbar Row
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        Component toolbar = createToolBar();
        getContentPane().add(toolbar, c);

        //Keep the frame background color consistent
        getContentPane().setBackground(toolbar.getBackground());
		
        //A seperator Row
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        getContentPane().add(new JSeparator(), c);
        
        //The search field row
        searchField = new JTextField(20);
        searchField.setEnabled(false);
        searchField.setMinimumSize(searchField.getPreferredSize());
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                //This method never seems to be called
            }
            public void insertUpdate(DocumentEvent e) {
                dbActions.filter();
            }
            public void removeUpdate(DocumentEvent e) {
                dbActions.filter();
            }
        });
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 1, 5, 1);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        getContentPane().add(searchField, c);

        //The accounts listview row
        accountsListview = new JList();
        accountsListview.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountsListview.setSelectedIndex(0);
        accountsListview.setVisibleRowCount(10);
        accountsListview.setModel(new DefaultListModel());
        JScrollPane accountsScrollList = new JScrollPane(accountsListview, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        accountsListview.addFocusListener(new FocusAdapter() {
        	public void focusGained(FocusEvent e) {
        		if (accountsListview.getModel().getSize() > 0 && accountsListview.getSelectedIndex() == -1) {
        			accountsListview.setSelectionInterval(0, 0);
        		}
        	}
        });
        accountsListview.addListSelectionListener(new ListSelectionListener() {
        	public void valueChanged(ListSelectionEvent e) {
                dbActions.setButtonState();
        	}
        });
        accountsListview.addMouseListener(new MouseAdapter() {
           public void mouseClicked(MouseEvent e) {
               if (e.getClickCount() == 2) {
                   editAccountButton.doClick();
               }
           }
        });
        accountsListview.addKeyListener(new KeyAdapter() {
        	public void keyReleased(KeyEvent e) {
        		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        			editAccountButton.doClick();
        		}
        	}
        });
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 1, 1, 1);
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.BOTH;
        getContentPane().add(accountsScrollList, c);

    }
    
    
    private JToolBar createToolBar() {
        
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        
        // The "Add Account" button
        newAccountButton = new JButton();
        newAccountButton.setToolTipText(ADD_ACCOUNT_TXT);
        newAccountButton.setIcon(new ImageIcon(iconsDir + "/add_account.gif"));
        newAccountButton.setDisabledIcon(new ImageIcon(iconsDir + "/add_account_d.gif"));;
        newAccountButton.addActionListener(dbActions);
        newAccountButton.setEnabled(false);
        newAccountButton.setActionCommand(ADD_ACCOUNT_TXT);
        toolbar.add(newAccountButton);

        // The "Edit Account" button
        editAccountButton = new JButton();
        editAccountButton.setToolTipText(EDIT_ACCOUNT_TXT);
        editAccountButton.setIcon(new ImageIcon(iconsDir + "/edit_account.gif"));
        editAccountButton.setDisabledIcon(new ImageIcon(iconsDir + "/edit_account_d.gif"));;
        editAccountButton.addActionListener(dbActions);
        editAccountButton.setEnabled(false);
        editAccountButton.setActionCommand(EDIT_ACCOUNT_TXT);
        toolbar.add(editAccountButton);

        // The "Delete Account" button
        deleteAccountButton = new JButton();
        deleteAccountButton.setToolTipText(DELETE_ACCOUNT_TXT);
        deleteAccountButton.setIcon(new ImageIcon(iconsDir + "/delete_account.gif"));
        deleteAccountButton.setDisabledIcon(new ImageIcon(iconsDir + "/delete_account_d.gif"));;
        deleteAccountButton.addActionListener(dbActions);
        deleteAccountButton.setEnabled(false);
        deleteAccountButton.setActionCommand(DELETE_ACCOUNT_TXT);
        toolbar.add(deleteAccountButton);

        toolbar.addSeparator();

        // The "Copy Username" button
        copyUsernameButton = new JButton();
        copyUsernameButton.setToolTipText("Copy username to clipboard");
        copyUsernameButton.setIcon(new ImageIcon(iconsDir + "/copy_username.gif"));
        copyUsernameButton.setDisabledIcon(new ImageIcon(iconsDir + "/copy_username_d.gif"));;
        copyUsernameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyUsernameToClipboard();
            }
        });
        copyUsernameButton.setEnabled(false);
        toolbar.add(copyUsernameButton);

        // The "Copy Password" button
        copyPasswordButton = new JButton();
        copyPasswordButton.setToolTipText("Copy password to clipboard");
        copyPasswordButton.setIcon(new ImageIcon(iconsDir + "/copy_password.gif"));
        copyPasswordButton.setDisabledIcon(new ImageIcon(iconsDir + "/copy_password_d.gif"));;
        copyPasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyPasswordToClipboard();
            }
        });
        copyPasswordButton.setEnabled(false);
        toolbar.add(copyPasswordButton);

        toolbar.addSeparator();

        // The "Option" button
        optionsButton = new JButton();
        optionsButton.setToolTipText(OPTIONS_TXT);
        optionsButton.setIcon(new ImageIcon(iconsDir + "/options.gif"));
        optionsButton.setDisabledIcon(new ImageIcon(iconsDir + "/options_d.gif"));;
        optionsButton.addActionListener(dbActions);
        optionsButton.setEnabled(true);
        optionsButton.setActionCommand(OPTIONS_TXT);
        toolbar.add(optionsButton);

        return toolbar;
    }


    private JMenuBar createMenuBar() {

        JMenuBar menuBar = new JMenuBar();
        
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);
        
        newDatabaseMenuItem = new JMenuItem(NEW_DATABASE_TXT, KeyEvent.VK_N);
        fileMenu.add(newDatabaseMenuItem);
        newDatabaseMenuItem.addActionListener(dbActions);
 
        openDatabaseMenuItem = new JMenuItem(OPEN_DATABASE_TXT, KeyEvent.VK_O);
        fileMenu.add(openDatabaseMenuItem);
        openDatabaseMenuItem.addActionListener(dbActions);

        fileMenu.addSeparator();
        
        exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        fileMenu.add(exitMenuItem);
        exitMenuItem.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			System.exit(0);
        		}
        });

        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);

        aboutMenuItem = new JMenuItem(ABOUT_TXT, KeyEvent.VK_A);
        helpMenu.add(aboutMenuItem);
        aboutMenuItem.addActionListener(dbActions);

        return menuBar;
        
    }
    
    
    public JList getAccountsListview() {
    		return accountsListview;
    }
    
    private void copyUsernameToClipboard() {
    	AccountInformation accInfo = dbActions.getSelectedAccount();
    	copyToClipboard(new String(accInfo.getUserId()));
    }

    private void copyPasswordToClipboard() {
    	AccountInformation accInfo = dbActions.getSelectedAccount();
    	copyToClipboard(new String(accInfo.getPassword()));
    }


    private void copyToClipboard(String s) {
        StringSelection stringSelection = new StringSelection(s);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);
    }
    
	public JButton getCopyPasswordButton() {
		return copyPasswordButton;
	}


	public JButton getCopyUsernameButton() {
		return copyUsernameButton;
	}


	public JButton getEditAccountButton() {
		return editAccountButton;
	}


	public JButton getNewAccountButton() {
		return newAccountButton;
	}


	public JButton getOptionsButton() {
		return optionsButton;
	}


    public JButton getDeleteAccountButton() {
        return deleteAccountButton;
    }

    
    public JTextField getSearchField() {
        return searchField;
    }
    
}
