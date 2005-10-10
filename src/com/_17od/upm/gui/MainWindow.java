/*
 * $Id: AccountInformation.java 31 2005-09-04 15:57:49Z Adrian Smith $
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


/**
 * This is the main application entry class
 */
public class MainWindow extends JFrame {

    private static final String applicationName = "Universal Password Manager";
    private static String iconsDir = new File("images").getAbsolutePath();
    
    public static final String NEW_DATABASE_TXT = "New Database";
    public static final String OPEN_DATABASE_TXT = "Open Database";

    private JButton newAccountButton;
    private JButton editAccountButton;
    private JButton copyUsernameButton;
    private JButton copyPasswordButton;
    private JButton optionsButton;
    
    private JMenuItem newDatabaseMenuItem;
    private JMenuItem openDatabaseMenuItem;
    private JMenuItem exitMenuItem;
    private JMenu fileMenu;
    private DefaultListModel accountsListModel;
    
    
    public MainWindow(String title) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
    		super(title);

        //Use the System look and feel
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set up the content pane.
        addComponentsToPane();

        //Display the window.
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
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
        if (!(getContentPane().getLayout() instanceof BorderLayout)) {
        		getContentPane().setLayout(new BorderLayout());
        }

        //The toolbar
        getContentPane().add(createToolBar(), BorderLayout.PAGE_START);

        //The menubar
        setJMenuBar(createMenuBar());

        //Create a panel to contain the search dialog and accounts listview
        JPanel centralPanel = new JPanel();
        BoxLayout layout = new BoxLayout(centralPanel, BoxLayout.Y_AXIS);
        centralPanel.setLayout(layout);

        //JSeparator sep = new JSeparator();
        //sep.setMaximumSize(sep.getPreferredSize());
        //sep.setAlignmentY(Component.LEFT_ALIGNMENT);
        //centralPanel.add(sep);

        centralPanel.add(Box.createVerticalStrut(5));
        
        //The search field
        JTextField searchField = new JTextField("Search", 20);
        searchField.setMaximumSize(searchField.getPreferredSize());
        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchField.setAlignmentY(Component.TOP_ALIGNMENT);
        centralPanel.add(searchField);

        centralPanel.add(Box.createVerticalStrut(5));
        
        //The Accounts listview
        JList accounts = new JList();
        accounts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accounts.setSelectedIndex(0);
        accounts.setVisibleRowCount(10);
        accountsListModel = new DefaultListModel();
        accounts.setModel(accountsListModel);
        JScrollPane accountsScrollList = new JScrollPane(accounts, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        accountsScrollList.setAlignmentX(Component.LEFT_ALIGNMENT);
        centralPanel.add(accountsScrollList);

        getContentPane().add(centralPanel, BorderLayout.CENTER);

    }
    
    
    private JToolBar createToolBar() {
        
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        
        // The "Add Account" button
        newAccountButton = new JButton();
        newAccountButton.setToolTipText("Add Account");
        newAccountButton.setIcon(new ImageIcon(iconsDir + "/new_document_24.gif"));
        newAccountButton.setDisabledIcon(new ImageIcon(iconsDir + "/new_document_24_d.gif"));;
        newAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addAccount();
            }
        });
        newAccountButton.setEnabled(false);
        toolbar.add(newAccountButton);

        // The "Edit Account" button
        editAccountButton = new JButton();
        editAccountButton.setToolTipText("Edit Account");
        editAccountButton.setIcon(new ImageIcon(iconsDir + "/cut_clipboard_24.gif"));
        editAccountButton.setDisabledIcon(new ImageIcon(iconsDir + "/cut_clipboard_24_d.gif"));;
        editAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editAccount();
            }
        });
        editAccountButton.setEnabled(false);
        toolbar.add(editAccountButton);
        
        toolbar.addSeparator();

        // The "Copy Username" button
        copyUsernameButton = new JButton();
        copyUsernameButton.setToolTipText("Copy username to clipboard");
        copyUsernameButton.setIcon(new ImageIcon(iconsDir + "/copy_clipboard_24.gif"));
        copyUsernameButton.setDisabledIcon(new ImageIcon(iconsDir + "/copy_clipboard_24_d.gif"));;
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
        copyPasswordButton.setIcon(new ImageIcon(iconsDir + "/copy_to_folder_24.gif"));
        copyPasswordButton.setDisabledIcon(new ImageIcon(iconsDir + "/copy_to_folder_24_d.gif"));;
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
        optionsButton.setToolTipText("Options");
        optionsButton.setIcon(new ImageIcon(iconsDir + "/properties_doc_24.gif"));
        optionsButton.setDisabledIcon(new ImageIcon(iconsDir + "/properties_doc_24_d.gif"));;
        optionsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                options();
            }
        });
        optionsButton.setEnabled(false);
        toolbar.add(optionsButton);

        return toolbar;
    }
    
    
    private JMenuBar createMenuBar() {

        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        
        DatabaseActions dbActions = new DatabaseActions(this);
        
        newDatabaseMenuItem = new JMenuItem(NEW_DATABASE_TXT);
        fileMenu.add(newDatabaseMenuItem);
        newDatabaseMenuItem.addActionListener(dbActions);
 
        openDatabaseMenuItem = new JMenuItem(OPEN_DATABASE_TXT);
        fileMenu.add(openDatabaseMenuItem);
        openDatabaseMenuItem.addActionListener(dbActions);

        fileMenu.addSeparator();
        
        exitMenuItem = new JMenuItem("Exit");
        fileMenu.add(exitMenuItem);
        exitMenuItem.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			System.exit(0);
        		}
        });

        return menuBar;
        
    }
    
    
    public DefaultListModel getAccountsModel() {
    		return accountsListModel;
    }
    
    private void addAccount() {
        //TODO Add impl
    }

    private void editAccount() {
        //TODO Add impl
    }

    private void options() {
        //TODO Add impl
    }
    
    private void copyUsernameToClipboard() {
        //TODO Add impl
    }

    private void copyPasswordToClipboard() {
        //TODO Add impl
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

}
