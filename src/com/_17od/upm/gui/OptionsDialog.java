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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import com._17od.upm.util.Preferences;
import com._17od.upm.util.Translator;


public class OptionsDialog extends EscapeDialog {

    private JTextField dbToLoadOnStartup;
    private JTextField httpProxyHost;
    private JTextField httpProxyPort;
    private JComboBox localeComboBox;
    private boolean okClicked = false;
    private JFrame parentFrame;
    private boolean languageChanged;


    public OptionsDialog(JFrame frame) {
        super(frame, Translator.translate("options"), true);
        
        Container container = getContentPane();

        // Create a pane with an empty border for spacing
        Border emptyBorder = BorderFactory.createEmptyBorder(2, 5, 5, 5);
        JPanel emptyBorderPanel = new JPanel();
        emptyBorderPanel.setLayout(new BoxLayout(emptyBorderPanel, BoxLayout.Y_AXIS));
        emptyBorderPanel.setBorder(emptyBorder);
        container.add(emptyBorderPanel);

        // ******************
        // *** The DB TO Load On Startup Section
        // ******************
        // Create a pane with an title etched border
        Border etchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        Border etchedTitleBorder = BorderFactory.createTitledBorder(etchedBorder, ' ' + Translator.translate("startup") + ' ');
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(etchedTitleBorder);
        emptyBorderPanel.add(mainPanel);

        GridBagConstraints c = new GridBagConstraints();

        // The "Database to Load on Startup" row
        JLabel urlLabel = new JLabel(Translator.translate("dbToLoadOnStartup"));
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 5, 0, 0);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        mainPanel.add(urlLabel, c);

        // The "Database to Load on Startup" input field row
        dbToLoadOnStartup = new JTextField(Preferences.get(Preferences.ApplicationOptions.DB_TO_LOAD_ON_STARTUP), 25);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 5, 5, 5);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(dbToLoadOnStartup, c);

        //JButton dbToLoadOnStartupButton = new JButton(Util.loadImage("open.gif"));
        JButton dbToLoadOnStartupButton = new JButton("...");
        dbToLoadOnStartupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getDBToLoadOnStartup();
            }
        });
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(0, 0, 5, 5);
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        mainPanel.add(dbToLoadOnStartupButton, c);

        // Some space
        emptyBorderPanel.add(Box.createVerticalGlue());


        // ******************
        // *** The Language Section
        // ******************
        // Create a pane with an title etched border
        Border languageEtchedTitleBorder = BorderFactory.createTitledBorder(etchedBorder, ' ' + Translator.translate("language") + ' ');
        JPanel languagePanel = new JPanel(new GridBagLayout());
        languagePanel.setBorder(languageEtchedTitleBorder);
        emptyBorderPanel.add(languagePanel);

        // The "Language" label row
        JLabel localeLabel = new JLabel(Translator.translate("language"));
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 5, 0, 0);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        languagePanel.add(localeLabel, c);

        // The "Locale" field row
        localeComboBox = new JComboBox(getSupportedLocaleNames());
        for (int i=0; i<localeComboBox.getItemCount(); i++) {
        	// If the locale language is blank then set it to the English language
        	// I'm not sure why this happens. Maybe it's because the default locale
        	// is English???
        	String currentLanguage = Translator.getCurrentLocale().getLanguage();
        	if (currentLanguage.equals("")) {
        		currentLanguage = "en";
        	}
        	
        	if (currentLanguage.equals(Translator.SUPPORTED_LOCALES[i].getLanguage())) {
                localeComboBox.setSelectedIndex(i);
                break;
        	}
        }
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 5, 5, 0);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        languagePanel.add(localeComboBox, c);

        // Some spacing
        emptyBorderPanel.add(Box.createVerticalGlue());

        // ******************
        // *** The Proxy Section
        // ******************
        // Create a pane with an title etched border
        Border proxyEtchedTitleBorder = BorderFactory.createTitledBorder(etchedBorder, ' ' + Translator.translate("httpProxy") + ' ');
        JPanel proxyPanel = new JPanel(new GridBagLayout());
        proxyPanel.setBorder(proxyEtchedTitleBorder);
        emptyBorderPanel.add(proxyPanel);

        // The "HTTP Proxy" label row
        JLabel proxyLabel = new JLabel(Translator.translate("httpProxy"));
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 5, 0, 0);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        proxyPanel.add(proxyLabel, c);

        // The "HTTP Proxy Port" label row
        JLabel proxyPortLabel = new JLabel(Translator.translate("port"));
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 5, 5, 5);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        proxyPanel.add(proxyPortLabel, c);

        // The "HTTP Proxy" field row
        httpProxyHost = new JTextField(Preferences.get(Preferences.ApplicationOptions.HTTP_PROXY_HOST), 20);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 5, 5, 0);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        proxyPanel.add(httpProxyHost, c);

        httpProxyPort = new JTextField(Preferences.get(Preferences.ApplicationOptions.HTTP_PROXY_PORT), 6);
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 5, 5, 5);
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        proxyPanel.add(httpProxyPort, c);

        // Some spacing
        emptyBorderPanel.add(Box.createVerticalGlue());

        // The buttons row
        JPanel buttonPanel = new JPanel(new FlowLayout());
        emptyBorderPanel.add(buttonPanel);
        JButton okButton = new JButton(Translator.translate("ok"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButtonAction();
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

    }
    

    public boolean okClicked() {
        return okClicked;
    }


    private void okButtonAction() {
        try {
            Preferences.set(Preferences.ApplicationOptions.DB_TO_LOAD_ON_STARTUP, dbToLoadOnStartup.getText());
            Preferences.set(Preferences.ApplicationOptions.HTTP_PROXY_HOST, httpProxyHost.getText());
            Preferences.set(Preferences.ApplicationOptions.HTTP_PROXY_PORT, httpProxyPort.getText());

            // Save the new language and set a flag if it has changed
            String beforeLocale = Preferences.get(Preferences.ApplicationOptions.LOCALE);
            Locale selectedLocale = Translator.SUPPORTED_LOCALES[localeComboBox.getSelectedIndex()];
            String afterLocale = selectedLocale.getLanguage();
            if (!afterLocale.equals(beforeLocale)) {
                Preferences.set(Preferences.ApplicationOptions.LOCALE, selectedLocale.getLanguage());
                Translator.loadBundle(selectedLocale);
                languageChanged = true;
            }

        	Preferences.save();
            setVisible(false);
            dispose();
            okClicked = true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentFrame, e.getStackTrace(), Translator.translate("error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void getDBToLoadOnStartup() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(Translator.translate("dbToOpenOnStartup"));
        int returnVal = fc.showOpenDialog(parentFrame);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File databaseFile = fc.getSelectedFile();
            dbToLoadOnStartup.setText(databaseFile.getAbsoluteFile().toString());
        }
    }

    
    private Object[] getSupportedLocaleNames() {
    	Object[] names = new Object[Translator.SUPPORTED_LOCALES.length]; 

    	for (int i=0; i<Translator.SUPPORTED_LOCALES.length; i++) {
	    	names[i] = Translator.SUPPORTED_LOCALES[i].getDisplayName() +
	    		" (" + Translator.SUPPORTED_LOCALES[i].getDisplayName(Translator.getCurrentLocale())
	    		+ ')';
    	}

    	return names;
    }

    
    public boolean hasLanguageChanged() {
    	return languageChanged;
    }

}
