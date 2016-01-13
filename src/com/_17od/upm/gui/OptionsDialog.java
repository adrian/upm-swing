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

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.apache.commons.codec.binary.Base64;

import com._17od.upm.util.Preferences;
import com._17od.upm.util.Translator;
import com._17od.upm.util.Util;

public class OptionsDialog extends EscapeDialog {

	private static final long serialVersionUID = 1L;

	private JTextField dbToLoadOnStartup;
	private JCheckBox enableProxyCheckbox;
	private JCheckBox hideAccountPasswordCheckbox;
	private JCheckBox inclEscCharstoPassCheckbox;
	private JCheckBox storeWindowPosCheckbox;
	private JCheckBox appAlwaysonTopCheckbox;
	private JLabel accountPasswordLengthLabel;
	private JTextField accountPasswordLength;
	private JTextField httpProxyHost;
	private JTextField httpProxyPort;
	private JTextField httpProxyUsername;
	private JPasswordField httpProxyPassword;
	private JCheckBox hidePasswordCheckbox;
	private JCheckBox databaseAutoLockCheckbox;
	private JTextField databaseAutoLockTime;
	private JCheckBox acceptSelfSignedCertsCheckbox;
	private JLabel proxyLabel;
	private JLabel proxyPortLabel;
	private JLabel proxyUsernameLabel;
	private JLabel proxyPasswordLabel;
	private JComboBox localeComboBox;
	private boolean okClicked = false;
	private JFrame parentFrame;
	private boolean languageChanged;
	private char defaultEchoChar;

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
		Border etchedTitleBorder = BorderFactory.createTitledBorder(etchedBorder,
				' ' + Translator.translate("general") + ' ');
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(etchedTitleBorder);
		emptyBorderPanel.add(mainPanel);

		GridBagConstraints c = new GridBagConstraints();

		// The "Database to Load on Startup" row
		JLabel urlLabel = new JLabel(Translator.translate("dbToLoadOnStartup"));
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 5, 3, 0);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.NONE;
		mainPanel.add(urlLabel, c);

		// The "Database to Load on Startup" input field row
		dbToLoadOnStartup = new JTextField(Preferences.get(Preferences.ApplicationOptions.DB_TO_LOAD_ON_STARTUP), 25);
		dbToLoadOnStartup.setHorizontalAlignment(JTextField.LEFT);
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 5, 5, 5);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(dbToLoadOnStartup, c);

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

		// The "Language" label row
		JLabel localeLabel = new JLabel(Translator.translate("language"));
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 5, 3, 0);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.NONE;
		mainPanel.add(localeLabel, c);

		// The "Locale" field row
		localeComboBox = new JComboBox(getSupportedLocaleNames());
		for (int i = 0; i < localeComboBox.getItemCount(); i++) {
			// If the locale language is blank then set it to the English
			// language
			// I'm not sure why this happens. Maybe it's because the default
			// locale
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
		c.gridy = 3;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 5, 8, 5);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(localeComboBox, c);

		// The "Hide account password" row
		Boolean hideAccountPassword = new Boolean(
				Preferences.get(Preferences.ApplicationOptions.ACCOUNT_HIDE_PASSWORD, "true"));
		hideAccountPasswordCheckbox = new JCheckBox(Translator.translate("hideAccountPassword"),
				hideAccountPassword.booleanValue());
		c.gridx = 0;
		c.gridy = 4;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 2, 5, 0);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		mainPanel.add(hideAccountPasswordCheckbox, c);

		// The "Database auto lock" row
		Boolean databaseAutoLock = new Boolean(
				Preferences.get(Preferences.ApplicationOptions.DATABASE_AUTO_LOCK, "false"));
		databaseAutoLockCheckbox = new JCheckBox(Translator.translate("databaseAutoLock"),
				databaseAutoLock.booleanValue());
		c.gridx = 0;
		c.gridy = 5;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 2, 5, 0);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		mainPanel.add(databaseAutoLockCheckbox, c);
		databaseAutoLockCheckbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				databaseAutoLockTime.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
			}
		});

		// The "Database auto lock" field row
		databaseAutoLockTime = new JTextField(Preferences.get(Preferences.ApplicationOptions.DATABASE_AUTO_LOCK_TIME),
				5);
		c.gridx = 1;
		c.gridy = 5;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 5, 5, 0);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(databaseAutoLockTime, c);
		databaseAutoLockTime.setEnabled(databaseAutoLockCheckbox.isSelected());

		// The "Generated password length" row
		accountPasswordLengthLabel = new JLabel(Translator.translate("generatedPasswodLength"));
		c.gridx = 0;
		c.gridy = 6;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 2, 5, 0);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		mainPanel.add(accountPasswordLengthLabel, c);

		accountPasswordLength = new JTextField(
				Preferences.get(Preferences.ApplicationOptions.ACCOUNT_PASSWORD_LENGTH, "8"), 5);
		c.gridx = 1;
		c.gridy = 6;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 5, 5, 0);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(accountPasswordLength, c);

		// The "Include Escape Characters to Generated Passwords" row
		Boolean inclEscCharstoPass = new Boolean(
				Preferences.get(Preferences.ApplicationOptions.INCLUDE_ESCAPE_CHARACTERS, "true"));
		inclEscCharstoPassCheckbox = new JCheckBox((Translator.translate("includePunctuationCharacters")),
				inclEscCharstoPass.booleanValue());
		c.gridx = 0;
		c.gridy = 7;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 2, 5, 0);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		mainPanel.add(inclEscCharstoPassCheckbox, c);

		// The "Store Window position" row
		Boolean storeWindowPos = Boolean
				.valueOf(Preferences.get(Preferences.ApplicationOptions.REMEMBER_WINDOW_POSITION, "false"));
		storeWindowPosCheckbox = new JCheckBox((Translator.translate("storeWindowPosition")),
				storeWindowPos.booleanValue());
		c.gridx = 0;
		c.gridy = 8;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 2, 5, 0);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		mainPanel.add(storeWindowPosCheckbox, c);

		// The "Application always on top" row
		Boolean appAlwaysonTop = new Boolean(
				Preferences.get(Preferences.ApplicationOptions.MAINWINDOW_ALWAYS_ON_TOP, "false"));
		appAlwaysonTopCheckbox = new JCheckBox((Translator.translate("applicationAlwaysonTop")),
				appAlwaysonTop.booleanValue());
		c.gridx = 0;
		c.gridy = 9;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 2, 5, 0);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		mainPanel.add(appAlwaysonTopCheckbox, c);

		// Some spacing
		emptyBorderPanel.add(Box.createVerticalGlue());

		// ******************
		// *** The HTTPS Section
		// ******************
		// Create a pane with an title etched border
		Border httpsEtchedTitleBorder = BorderFactory.createTitledBorder(etchedBorder, " HTTPS ");
		final JPanel httpsPanel = new JPanel(new GridBagLayout());
		httpsPanel.setBorder(httpsEtchedTitleBorder);
		emptyBorderPanel.add(httpsPanel);

		// The "Accept Self Sigend Certificates" checkbox row
		Boolean acceptSelfSignedCerts = new Boolean(
				Preferences.get(Preferences.ApplicationOptions.HTTPS_ACCEPT_SELFSIGNED_CERTS, "false"));
		acceptSelfSignedCertsCheckbox = new JCheckBox(Translator.translate("acceptSelfSignedCerts"),
				acceptSelfSignedCerts.booleanValue());
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 2, 5, 0);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		httpsPanel.add(acceptSelfSignedCertsCheckbox, c);

		// ******************
		// *** The Proxy Section
		// ******************
		// Create a pane with an title etched border
		Border proxyEtchedTitleBorder = BorderFactory.createTitledBorder(etchedBorder,
				' ' + Translator.translate("httpProxy") + ' ');
		final JPanel proxyPanel = new JPanel(new GridBagLayout());
		proxyPanel.setBorder(proxyEtchedTitleBorder);
		emptyBorderPanel.add(proxyPanel);

		// The "Enable Proxy" row
		Boolean proxyEnabled = new Boolean(Preferences.get(Preferences.ApplicationOptions.HTTP_PROXY_ENABLED));
		enableProxyCheckbox = new JCheckBox(Translator.translate("enableProxy"), proxyEnabled.booleanValue());
		enableProxyCheckbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					enableProxyComponents(true);
				} else {
					enableProxyComponents(false);
				}
			}
		});
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 2, 5, 0);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		proxyPanel.add(enableProxyCheckbox, c);

		// The "HTTP Proxy" label row
		proxyLabel = new JLabel(Translator.translate("httpProxy"));
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 5, 3, 0);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.NONE;
		proxyPanel.add(proxyLabel, c);

		// The "HTTP Proxy Port" label
		proxyPortLabel = new JLabel(Translator.translate("port"));
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 5, 3, 5);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		proxyPanel.add(proxyPortLabel, c);

		// The "HTTP Proxy" field row
		httpProxyHost = new JTextField(Preferences.get(Preferences.ApplicationOptions.HTTP_PROXY_HOST), 20);
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 5, 5, 0);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		proxyPanel.add(httpProxyHost, c);

		httpProxyPort = new JTextField(Preferences.get(Preferences.ApplicationOptions.HTTP_PROXY_PORT), 6);
		c.gridx = 1;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 5, 5, 5);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		proxyPanel.add(httpProxyPort, c);

		// The "HTTP Proxy Username" label row
		proxyUsernameLabel = new JLabel(Translator.translate("httpProxyUsername"));
		c.gridx = 0;
		c.gridy = 3;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 5, 3, 0);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.NONE;
		proxyPanel.add(proxyUsernameLabel, c);

		// The "HTTP Proxy Username" field row
		httpProxyUsername = new JTextField(Preferences.get(Preferences.ApplicationOptions.HTTP_PROXY_USERNAME), 20);
		c.gridx = 0;
		c.gridy = 4;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 5, 5, 5);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		proxyPanel.add(httpProxyUsername, c);

		// The "HTTP Proxy Password" label row
		proxyPasswordLabel = new JLabel(Translator.translate("httpProxyPassword"));
		c.gridx = 0;
		c.gridy = 5;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 5, 3, 0);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.NONE;
		proxyPanel.add(proxyPasswordLabel, c);

		// The "HTTP Proxy Password" field row
		String encodedPassword = Preferences.get(Preferences.ApplicationOptions.HTTP_PROXY_PASSWORD);
		String decodedPassword = null;
		if (encodedPassword != null) {
			decodedPassword = new String(Base64.decodeBase64(encodedPassword.getBytes()));
		}
		httpProxyPassword = new JPasswordField(decodedPassword, 20);
		c.gridx = 0;
		c.gridy = 6;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 5, 5, 5);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		proxyPanel.add(httpProxyPassword, c);

		hidePasswordCheckbox = new JCheckBox(Translator.translate("hide"), true);
		defaultEchoChar = httpProxyPassword.getEchoChar();
		hidePasswordCheckbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					httpProxyPassword.setEchoChar(defaultEchoChar);
				} else {
					httpProxyPassword.setEchoChar((char) 0);
				}
			}
		});
		c.gridx = 1;
		c.gridy = 6;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 5, 5, 0);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		proxyPanel.add(hidePasswordCheckbox, c);

		// Some spacing
		emptyBorderPanel.add(Box.createVerticalGlue());

		// The buttons row
		JPanel buttonPanel = new JPanel(new FlowLayout());
		emptyBorderPanel.add(buttonPanel);
		JButton okButton = new JButton(Translator.translate("ok"));
		// Link Enter key to okButton
		getRootPane().setDefaultButton(okButton);
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

		enableProxyComponents(proxyEnabled.booleanValue());
	}

	private void enableProxyComponents(boolean enable) {
		httpProxyHost.setEnabled(enable);
		httpProxyPort.setEnabled(enable);
		httpProxyUsername.setEnabled(enable);
		httpProxyPassword.setEnabled(enable);
		proxyLabel.setEnabled(enable);
		proxyPortLabel.setEnabled(enable);
		proxyUsernameLabel.setEnabled(enable);
		proxyPasswordLabel.setEnabled(enable);
		hidePasswordCheckbox.setEnabled(enable);
	}

	public boolean okClicked() {
		return okClicked;
	}

	private void okButtonAction() {
		try {
			if (databaseAutoLockCheckbox.isSelected()) {
				if (databaseAutoLockTime.getText() == null || databaseAutoLockTime.getText().trim().equals("")
						|| !Util.isNumeric(databaseAutoLockTime.getText())) {
					JOptionPane.showMessageDialog(OptionsDialog.this,
							Translator.translate("invalidValueForDatabaseAutoLockTime"),
							Translator.translate("problem"), JOptionPane.ERROR_MESSAGE);
					databaseAutoLockTime.requestFocusInWindow();
					return;
				}
			}

			if (accountPasswordLength.getText() == null || accountPasswordLength.getText().trim().equals("")
					|| !Util.isNumeric(accountPasswordLength.getText())) {
				JOptionPane.showMessageDialog(OptionsDialog.this,
						Translator.translate("invalidValueForAccountPasswordLength"), Translator.translate("problem"),
						JOptionPane.ERROR_MESSAGE);
				databaseAutoLockTime.requestFocusInWindow();
				return;
			}

			Preferences.set(Preferences.ApplicationOptions.DB_TO_LOAD_ON_STARTUP, dbToLoadOnStartup.getText());
			Preferences.set(Preferences.ApplicationOptions.ACCOUNT_HIDE_PASSWORD,
					String.valueOf(hideAccountPasswordCheckbox.isSelected()));
			Preferences.set(Preferences.ApplicationOptions.INCLUDE_ESCAPE_CHARACTERS,
					String.valueOf(inclEscCharstoPassCheckbox.isSelected()));
			Preferences.set(Preferences.ApplicationOptions.REMEMBER_WINDOW_POSITION,
					String.valueOf(storeWindowPosCheckbox.isSelected()));
			Preferences.set(Preferences.ApplicationOptions.MAINWINDOW_ALWAYS_ON_TOP,
					String.valueOf(appAlwaysonTopCheckbox.isSelected()));
			Preferences.set(Preferences.ApplicationOptions.DATABASE_AUTO_LOCK,
					String.valueOf(databaseAutoLockCheckbox.isSelected()));
			Preferences.set(Preferences.ApplicationOptions.DATABASE_AUTO_LOCK_TIME, databaseAutoLockTime.getText());
			Preferences.set(Preferences.ApplicationOptions.ACCOUNT_PASSWORD_LENGTH, accountPasswordLength.getText());
			Preferences.set(Preferences.ApplicationOptions.HTTPS_ACCEPT_SELFSIGNED_CERTS,
					String.valueOf(acceptSelfSignedCertsCheckbox.isSelected()));
			Preferences.set(Preferences.ApplicationOptions.HTTP_PROXY_HOST, httpProxyHost.getText());
			Preferences.set(Preferences.ApplicationOptions.HTTP_PROXY_PORT, httpProxyPort.getText());
			Preferences.set(Preferences.ApplicationOptions.HTTP_PROXY_USERNAME, httpProxyUsername.getText());
			String encodedPassword = new String(
					Base64.encodeBase64(new String(httpProxyPassword.getPassword()).getBytes()));
			Preferences.set(Preferences.ApplicationOptions.HTTP_PROXY_PASSWORD, encodedPassword);
			Preferences.set(Preferences.ApplicationOptions.HTTP_PROXY_ENABLED,
					String.valueOf(enableProxyCheckbox.isSelected()));

			MainWindow.setAppAlwaysonTop(appAlwaysonTopCheckbox.isSelected());

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
			JOptionPane.showMessageDialog(parentFrame, e.getStackTrace(), Translator.translate("error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void getDBToLoadOnStartup() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Translator.translate("dbToOpenOnStartup"));
		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File databaseFile = fc.getSelectedFile();
			dbToLoadOnStartup.setText(databaseFile.getAbsoluteFile().toString());
		}
	}

	private Object[] getSupportedLocaleNames() {
		Object[] names = new Object[Translator.SUPPORTED_LOCALES.length];

		for (int i = 0; i < Translator.SUPPORTED_LOCALES.length; i++) {
			names[i] = Translator.SUPPORTED_LOCALES[i].getDisplayLanguage(Translator.getCurrentLocale()) + " ("
					+ Translator.SUPPORTED_LOCALES[i].getDisplayLanguage(Translator.SUPPORTED_LOCALES[i]) + ')';
		}

		return names;
	}

	public boolean hasLanguageChanged() {
		return languageChanged;
	}

}