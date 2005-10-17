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

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com._17od.upm.database.AccountInformation;


public class AccountDialog extends JDialog {

    private AccountInformation pAccount;
    private JTextField userId;
    private JTextField password;
    private JTextArea notes;
    private JTextField url;
    private JTextField accountName;
    private boolean okClicked = false;
    
    
    public AccountDialog(AccountInformation account, JFrame frame, String title, boolean accNameEditable) {
        super(frame, title, true);

        pAccount = account;
        
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        Container container = getContentPane();

        //The AccountName Row
        JLabel accountLabel = new JLabel("Account");
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        container.add(accountLabel, c);
        
        accountName = new JTextField(new String(pAccount.getAccountName()), 20);
        accountName.setEditable(accNameEditable);
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1.0;
        c.weighty = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        container.add(accountName, c);

        
        //Userid Row
        JLabel useridLabel = new JLabel("User Id");
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        container.add(useridLabel, c);
        
        userId = new JTextField(new String(pAccount.getUserId()), 20);
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1.0;
        c.weighty = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        container.add(userId, c);
        
        
        //Password Row
        JLabel passwordLabel = new JLabel("Password");
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        container.add(passwordLabel, c);
        
        password = new JTextField(new String(pAccount.getPassword()), 20);
        c.gridx = 1;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1.0;
        c.weighty = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        container.add(password, c);
        
        
        //URL Row
        JLabel urlLabel = new JLabel("URL");
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        container.add(urlLabel, c);
        
        url = new JTextField(new String(pAccount.getUrl()), 20);
        c.gridx = 1;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1.0;
        c.weighty = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        container.add(url, c);
        
        
        //Notes Row
        JLabel notesLabel = new JLabel("Notes");
        c.gridx = 0;
        c.gridy = 4;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        container.add(notesLabel, c);
        
        notes = new JTextArea(new String(pAccount.getNotes()), 10, 20);
        JScrollPane notesScrollPane = new JScrollPane(notes);
        c.gridx = 1;
        c.gridy = 4;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        container.add(notesScrollPane, c);
        
        
        //Seperator Row
        JSeparator sep = new JSeparator();
        c.gridx = 0;
        c.gridy = 5;
        c.anchor = GridBagConstraints.PAGE_END;
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 1.0;
        c.weighty = 0;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        container.add(sep, c);


        //Button Row
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButtonAction();
            }
        });
        buttonPanel.add(okButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeButtonAction();
            }
        });
        buttonPanel.add(cancelButton);
        c.gridx = 0;
        c.gridy = 6;
        c.anchor = GridBagConstraints.PAGE_END;
        c.insets = new Insets(5, 0, 5, 0);
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.NONE;
        container.add(buttonPanel, c);
        
    }
    

    public boolean okClicked() {
        return okClicked;
    }
    
    
    public AccountInformation getAccount() {
        return pAccount;
    }
    
    
    private void okButtonAction() {
        pAccount.setAccountName(accountName.getText());
        pAccount.setUserId(userId.getText().getBytes());
        pAccount.setPassword(password.getText().getBytes());
        pAccount.setUrl(url.getText().getBytes());
        pAccount.setNotes(notes.getText().getBytes());
        setVisible(false);
        dispose();
        okClicked = true;
    }

    
    private void closeButtonAction() {
        okClicked = false;
        setVisible(false);
        dispose();
    }

}
