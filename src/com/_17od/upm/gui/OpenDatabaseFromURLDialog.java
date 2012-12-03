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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com._17od.upm.util.Translator;


public class OpenDatabaseFromURLDialog extends EscapeDialog {

    private static final long serialVersionUID = 1L;

    public JTextField urlTextField;
    public JTextField usernameTextField;
    public JPasswordField passwordTextField;
    private boolean okClicked = false;
    
    
    public OpenDatabaseFromURLDialog(final JFrame frame) {
        super(frame, Translator.translate("openDatabaseFromURL"), true);

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

        // The URL Label row
        JLabel urlLabel = new JLabel(Translator.translate("url"));
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 5, 0, 0);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(urlLabel, c);

        // The Remote URL input field row
        urlTextField = new JTextField(20);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 5, 5, 5);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(urlTextField, c);

        // The username label field
        JLabel usernameLabel = new JLabel(Translator.translate("username"));
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 5, 0, 0);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(usernameLabel, c);

        // The username inpur field
        usernameTextField = new JTextField(10);
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 5, 5, 5);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        mainPanel.add(usernameTextField, c);

        // The password label field
        JLabel passwordLabel = new JLabel(Translator.translate("password"));
        c.gridx = 0;
        c.gridy = 4;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 5, 0, 0);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(passwordLabel, c);

        // The username inpur field
        passwordTextField = new JPasswordField(10);
        c.gridx = 0;
        c.gridy = 5;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 5, 5, 5);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        mainPanel.add(passwordTextField, c);

        // Some spacing
        Component verticalSpace = Box.createVerticalGlue();
        c.gridy = 6;
        c.weighty = 1;
        mainPanel.add(verticalSpace, c);

        // The buttons row
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton okButton = new JButton(Translator.translate("ok"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okClicked = true;
                setVisible(false);
                dispose();
            }
        });
        buttonPanel.add(okButton);
        
        JButton cancelButton = new JButton(Translator.translate("cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        buttonPanel.add(cancelButton);

        emptyBorderPanel.add(buttonPanel);

    }


    public JTextField getPasswordTextField() {
        return passwordTextField;
    }

    
    public JTextField getUrlTextField() {
        return urlTextField;
    }

    
    public JTextField getUsernameTextField() {
        return usernameTextField;
    }
    
    
    public boolean getOkClicked() {
        return okClicked;
    }
    
}
