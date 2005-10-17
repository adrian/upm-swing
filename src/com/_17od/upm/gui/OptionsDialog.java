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
import java.io.File;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import com._17od.upm.util.Preferences;


public class OptionsDialog extends JDialog {

    private JTextField dbToLoadOnStartup;
    private boolean okClicked = false;
    private JFrame parentFrame;
    
    
    public OptionsDialog(JFrame frame) {
        super(frame, "Options", true);

        parentFrame = frame;
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        Container container = getContentPane();

        //The dbToLoadOnStartup Row
        JLabel accountToLoadOnStartupLabel = new JLabel("Account To Load On Startup");
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 0;
        c.weighty = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        container.add(accountToLoadOnStartupLabel, c);
        
        dbToLoadOnStartup = new JTextField(Preferences.getDBToOptionOnStartup(), 25);
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        container.add(dbToLoadOnStartup, c);
        
        JButton dbToLoadOnStartupButton = new JButton("..");
        dbToLoadOnStartupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getDBToLoadOnStartup();
            }
        });
        c.gridx = 2;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        container.add(dbToLoadOnStartupButton, c);
        
        //Seperator Row
        JSeparator sep = new JSeparator();
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.PAGE_END;
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 0;
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
        c.gridy = 3;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(5, 0, 5, 0);
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.NONE;
        container.add(buttonPanel, c);
        
        setResizable(false);
    }
    

    public boolean okClicked() {
        return okClicked;
    }
    
    
    private void okButtonAction() {
        try {
        	Preferences.setDBToOptionOnStartup(dbToLoadOnStartup.getText());
        	Preferences.save();
            setVisible(false);
            dispose();
            okClicked = true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentFrame, e.getStackTrace(), "Error...", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void closeButtonAction() {
        setVisible(false);
        dispose();
    }

    
    private void getDBToLoadOnStartup() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("DB To Open On Startup......");
        int returnVal = fc.showOpenDialog(parentFrame);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File databaseFile = fc.getSelectedFile();
            dbToLoadOnStartup.setText(databaseFile.getAbsoluteFile().toString());
        }
    }
    
}
