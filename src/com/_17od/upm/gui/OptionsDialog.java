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
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import com._17od.upm.util.Preferences;


public class OptionsDialog extends JDialog {

    private JTextField dbToLoadOnStartup;
    private boolean okClicked = false;
    private JFrame parentFrame;
    
    
    public OptionsDialog(JFrame frame) {
        super(frame, "Options", true);

        parentFrame = frame;
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

        //JPanel borderPane = new JPanel(new GridBagLayout());
        JPanel borderPane = new JPanel();

        //Put a border on the panel
        Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        Border etchedBorder = BorderFactory.createEtchedBorder();
        Border titledBorder = BorderFactory.createTitledBorder(etchedBorder, " Startup Database ");
        Border compoundBorder = BorderFactory.createCompoundBorder(emptyBorder, titledBorder);
        borderPane.setBorder(compoundBorder);

        //Add the components to the panel
        JLabel accountToLoadOnStartupLabel = new JLabel("Database To Load On Startup");
        borderPane.add(accountToLoadOnStartupLabel);
        dbToLoadOnStartup = new JTextField(Preferences.getDBToOptionOnStartup(), 25);
        borderPane.add(dbToLoadOnStartup);
        JButton dbToLoadOnStartupButton = new JButton("..");
        dbToLoadOnStartupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getDBToLoadOnStartup();
            }
        });
        borderPane.add(dbToLoadOnStartupButton);
        getContentPane().add(borderPane);

        //Add the OK/Cancel buttons to the dialog
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
        getContentPane().add(buttonPanel);

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
