/*
 * Universal Password Manager
 * Copyright (C) 2005-2011 Adrian Smith
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
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com._17od.upm.database.PasswordDatabase;
import com._17od.upm.transport.Transport;
import com._17od.upm.transport.TransportException;
import com._17od.upm.util.Translator;


public class DatabasePropertiesDialog extends EscapeDialog {

    private boolean databaseNeedsSaving = false;
    
    public DatabasePropertiesDialog(final JFrame frame, ArrayList accountNames, final PasswordDatabase database) {
        super(frame, Translator.translate("databaseProperties"), true);
        
        Container container = getContentPane();

        // Create a pane with an empty border for spacing
        Border emptyBorder = BorderFactory.createEmptyBorder(2, 5, 5, 5);
        JPanel emptyBorderPanel = new JPanel();
        emptyBorderPanel.setLayout(new BoxLayout(emptyBorderPanel, BoxLayout.Y_AXIS));
        emptyBorderPanel.setBorder(emptyBorder);
        container.add(emptyBorderPanel);

        // Create a pane with an title etched border
        Border etchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        Border etchedTitleBorder = BorderFactory.createTitledBorder(etchedBorder, ' ' + Translator.translate("remoteLocation") + ' ');
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(etchedTitleBorder);
        emptyBorderPanel.add(mainPanel);

        GridBagConstraints c = new GridBagConstraints();

        // The Remote URL Label row
        JLabel urlLabel = new JLabel(Translator.translate("url"));
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 3, 0, 0);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        mainPanel.add(urlLabel, c);

        // The Remote URL input field row
        final JTextField urlTextField = new JTextField(database.getDbOptions().getRemoteLocation(), 20);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 3, 3, 3);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        mainPanel.add(urlTextField, c);

        // The Authentication Credentials label row
        JLabel authLabel = new JLabel(Translator.translate("authenticationCredentials"));
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(3, 3, 0, 0);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        mainPanel.add(authLabel, c);

        // The Authentication Credentials input field row
        String[] sAccountNames = new String[accountNames.size() + 1];
        sAccountNames[0] = "";
        System.arraycopy(accountNames.toArray(), 0, sAccountNames, 1, accountNames.size());
        Arrays.sort(sAccountNames);
        final JComboBox auth = new JComboBox(sAccountNames);
        auth.setSelectedItem(database.getDbOptions().getAuthDBEntry());
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 3, 3, 3);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(auth, c);

        // Some spacing
        Component verticalSpace = Box.createVerticalGlue();
        c.gridx = 0;
        c.gridy = 4;
        c.weighty = 1;
        mainPanel.add(verticalSpace, c);
        
        // The buttons row
        JPanel buttonPanel = new JPanel(new FlowLayout());
        emptyBorderPanel.add(buttonPanel);
        JButton okButton = new JButton(Translator.translate("ok"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveDatabaseOptions(frame, urlTextField.getText().trim(), (String) auth.getSelectedItem(), database);
            }
        });
        buttonPanel.add(okButton);
        
        JButton cancelButton = new JButton(Translator.translate("cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                databaseNeedsSaving = false;
                setVisible(false);
                dispose();
            }
        });
        buttonPanel.add(cancelButton);
        
    }
    
    
    private URL validateURL(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            // If we got here the the URL is invalid
        }
        return url;
    }
    
    
    private void saveDatabaseOptions(JFrame parentFrame, String remoteLocation, String authEntry, PasswordDatabase database) {

        boolean canCloseWindow = false;

        // If either the url or authentication entry to use have changed then update 
        // the flag to indicate that the database needs to be saved
        if (!database.getDbOptions().getRemoteLocation().equals(remoteLocation) ||
                !database.getDbOptions().getAuthDBEntry().equals(authEntry)) {
            databaseNeedsSaving = true;
        } else {
            // If the db doesn't need to be saved then we can close this window
            canCloseWindow = true;
        }

        // If the url/remoteLocation is not empty then we need to validate the URL and upload the database
        if (!remoteLocation.equals("")) {

            // Check the validity of the URL given by the user
            URL url = validateURL(remoteLocation);
            if (url != null) {

                // Only allow supported protocols
                if (Transport.isASupportedProtocol(url.getProtocol())) {

                    // If the remote location has changed then upload the database
                    if (!database.getDbOptions().getRemoteLocation().equals(remoteLocation)) {
                        try {
                            Transport transport = Transport.getTransportForURL(url);
                            if (!authEntry.equals("")) {
                                String userId = database.getAccount(authEntry).getUserId();
                                String password = database.getAccount(authEntry).getPassword();
                                transport.put(remoteLocation, database.getDatabaseFile(), userId, password);
                            } else {
                                transport.put(remoteLocation, database.getDatabaseFile());
                            }
                            canCloseWindow = true;
                        } catch (TransportException e ){
                            JOptionPane.showMessageDialog(parentFrame, e.getMessage(), Translator.translate("transportError"), JOptionPane.ERROR_MESSAGE);                            
                        }
                    } else {
                        canCloseWindow = true;
                    }

                } else {
                    JOptionPane.showMessageDialog(parentFrame, Translator.translate("unsupportedProtocol"), Translator.translate("invalidProtocol"), JOptionPane.ERROR_MESSAGE);
                }

            } else {
                // If we got here the the URL is invalid
                JOptionPane.showMessageDialog(parentFrame, Translator.translate("givenURLIsInvalid"), Translator.translate("invalidURL"), JOptionPane.ERROR_MESSAGE);
            }
            
        } else {
            // If we were given a blank URL then the user doesn't want to maintain a remote location so we can safetly exit
            canCloseWindow = true;
        }

        
        // Attempt to save the database and then close the window
        if (canCloseWindow) {
            try {
                if (databaseNeedsSaving) {
                    database.getDbOptions().setAuthDBEntry(authEntry);
                    database.getDbOptions().setRemoteLocation(remoteLocation);
                }
                setVisible(false);
                dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parentFrame, e.getMessage(), Translator.translate("problemSavingDB"), JOptionPane.ERROR_MESSAGE);
            }
        }
        
    }


    public boolean getDatabaseNeedsSaving() {
        return databaseNeedsSaving;
    }

}