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
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.validator.routines.UrlValidator;

import com._17od.upm.database.AccountInformation;
import com._17od.upm.util.Preferences;
import com._17od.upm.util.Translator;
import com._17od.upm.util.Util;

public class AccountDialog extends EscapeDialog {

	private static final long serialVersionUID = 1L;
	private static final char[] ALLOWED_CHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
			'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
			'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9' };
	// Extended CharArray list which include also 24 Escape characters for
	// stronger password generation
	private static final char[] EXTRA_ALLOWED_CHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2',
			'3', '4', '5', '6', '7', '8', '9', '~', '!', '@', '#', '$', '%', '^', '&', '*', '(', ',', ')', '_', '-',
			'+', '=', '|', '/', '<', '>', '.', '?', ';', ':' };

	/*
	 * We will use the (4)four above CharArray lists(UPPERCASE_CHARS,
	 * LOWERCASE_CHARS, NUMBER_CHARS, ESCAPE_CHARS) to ensure that the generated
	 * passwords will be more complex. If the user has selected to include
	 * escape characters to generated passwords and the length of the passwords
	 * is 4 or above, then we will use some methods in order to generate
	 * passwords that will have at least 1 lower case + 1 upper case + 1 number
	 * + 1 escape character. On the other hand, if the user has not selected to
	 * include escape characters to generated passwords and the length of the
	 * passwords is at least 3, then we will use methods in order to generate
	 * passwords that will have at least 1 lower case + 1 upper case + 1 number.
	 */
	private static final char[] UPPERCASE_CHARS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'

	};

	private static final char[] LOWERCASE_CHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'

	};

	private static final char[] NUMBER_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	private static final char[] PUNCTUATION_CHARS = { '~', '!', '@', '#', '$', '%', '^', '&', '*', '(', ',', ')', '_',
			'-', '+', '=', '|', '/', '<', '>', '.', '?', ';', ':' };

	private AccountInformation pAccount;
	private JTextField userId;
	private JPasswordField password;
	private JTextArea notes;
	private JTextField url;
	private JTextField accountName;
	private boolean okClicked = false;
	private ArrayList existingAccounts;
	private JFrame parentWindow;
	private boolean accountChanged = false;
	private char defaultEchoChar;

	public AccountDialog(AccountInformation account, JFrame parentWindow, boolean readOnly,
			ArrayList existingAccounts) {
		super(parentWindow, true);

		boolean addingAccount = false;
		
		//Request focus on Account JDialog when mouse clicked
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					requestFocus();
					
				}
			}});

		// Set the title based on weather we've been opened in readonly mode and
		// weather the
		// Account passed in is empty or not
		String title = null;
		if (readOnly) {
			title = Translator.translate("viewAccount");
		} else if (!readOnly && account.getAccountName().trim().equals("")) {
			title = Translator.translate("addAccount");
			addingAccount = true;
		} else {
			title = Translator.translate("editAccount");
		}
		setTitle(title);

		this.pAccount = account;
		this.existingAccounts = existingAccounts;
		this.parentWindow = parentWindow;

		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		Container container = getContentPane();

		// The AccountName Row
		JLabel accountLabel = new JLabel(Translator.translate("account"));
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 10, 10, 10);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		container.add(accountLabel, c);

		// This panel will hold the Account field and the copy and paste
		// buttons.
		JPanel accountPanel = new JPanel(new GridBagLayout());

		accountName = new JTextField(new String(pAccount.getAccountName()), 20);
		if (readOnly) {
			accountName.setEditable(false);
		}
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 0, 0, 5);
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		accountPanel.add(accountName, c);
		accountName.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				accountName.selectAll();
			}
		});

		JButton acctCopyButton = new JButton();
		acctCopyButton.setIcon(Util.loadImage("copy-icon.png"));
		acctCopyButton.setToolTipText("Copy");
		acctCopyButton.setEnabled(true);
		acctCopyButton.setMargin(new Insets(0, 0, 0, 0));
		acctCopyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				copyTextField(accountName);
			}
		});
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 0, 0, 5);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		accountPanel.add(acctCopyButton, c);

		JButton acctPasteButton = new JButton();
		acctPasteButton.setIcon(Util.loadImage("paste-icon.png"));
		acctPasteButton.setToolTipText("Paste");
		acctPasteButton.setEnabled(!readOnly);
		acctPasteButton.setMargin(new Insets(0, 0, 0, 0));
		acctPasteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				pasteToTextField(accountName);
			}
		});
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 0, 0, 5);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		accountPanel.add(acctPasteButton, c);

		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 10, 10, 10);
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		container.add(accountPanel, c);

		// Userid Row
		JLabel useridLabel = new JLabel(Translator.translate("userid"));
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 10, 10, 10);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		container.add(useridLabel, c);

		// This panel will hold the User ID field and the copy and paste
		// buttons.
		JPanel idPanel = new JPanel(new GridBagLayout());

		userId = new JTextField(new String(pAccount.getUserId()), 20);
		if (readOnly) {
			userId.setEditable(false);
		}
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 0, 0, 5);
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		idPanel.add(userId, c);
		userId.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				userId.selectAll();
			}
		});

		JButton idCopyButton = new JButton();
		idCopyButton.setIcon(Util.loadImage("copy-icon.png"));
		idCopyButton.setToolTipText("Copy");
		idCopyButton.setEnabled(true);
		idCopyButton.setMargin(new Insets(0, 0, 0, 0));
		idCopyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				copyTextField(userId);
			}
		});
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 0, 0, 5);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		idPanel.add(idCopyButton, c);

		JButton idPasteButton = new JButton();
		idPasteButton.setIcon(Util.loadImage("paste-icon.png"));
		idPasteButton.setToolTipText("Paste");
		idPasteButton.setEnabled(!readOnly);
		idPasteButton.setMargin(new Insets(0, 0, 0, 0));
		idPasteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				pasteToTextField(userId);
			}
		});
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 0, 0, 5);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		idPanel.add(idPasteButton, c);

		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 10, 10, 10);
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		container.add(idPanel, c);

		// Password Row
		JLabel passwordLabel = new JLabel(Translator.translate("password"));
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.insets = new Insets(15, 10, 10, 10);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		container.add(passwordLabel, c);

		// This panel will hold the password, generate password button, copy and
		// paste buttons, and hide password checkbox
		JPanel passwordPanel = new JPanel(new GridBagLayout());

		password = new JPasswordField(new String(pAccount.getPassword()), 20);
		// allow CTRL-C on the password field
		password.putClientProperty("JPasswordField.cutCopyAllowed", Boolean.TRUE);
		password.setEditable(!readOnly);
		password.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				password.selectAll();
			}
		});
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 0, 0, 5);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		passwordPanel.add(password, c);

		JButton generateRandomPasswordButton = new JButton(Translator.translate("generate"));
		if (readOnly) {
			generateRandomPasswordButton.setEnabled(false);
		}
		generateRandomPasswordButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				// Get the user's preference about including or not Escape
				// Characters to generated passwords
				Boolean includeEscapeChars = new Boolean(
						Preferences.get(Preferences.ApplicationOptions.INCLUDE_ESCAPE_CHARACTERS, "true"));
				int pwLength = Preferences.getInt(Preferences.ApplicationOptions.ACCOUNT_PASSWORD_LENGTH, 8);
				String Password;

				if ((includeEscapeChars.booleanValue()) && (pwLength > 3)) {
					// Verify that the generated password satisfies the criteria
					// for strong passwords(including Escape Characters)
					do {
						Password = GeneratePassword(pwLength, includeEscapeChars.booleanValue());
					} while (!(CheckPassStrong(Password, includeEscapeChars.booleanValue())));

				} else if (!(includeEscapeChars.booleanValue()) && (pwLength > 2)) {
					// Verify that the generated password satisfies the criteria
					// for strong passwords(excluding Escape Characters)
					do {
						Password = GeneratePassword(pwLength, includeEscapeChars.booleanValue());
					} while (!(CheckPassStrong(Password, includeEscapeChars.booleanValue())));

				} else {
					// Else a weak password of 3 or less chars will be produced
					Password = GeneratePassword(pwLength, includeEscapeChars.booleanValue());
				}
				password.setText(Password);
			}
		});
		if (addingAccount) {
			generateRandomPasswordButton.doClick();
		}
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 0, 0, 5);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		passwordPanel.add(generateRandomPasswordButton, c);

		JButton pwCopyButton = new JButton();
		pwCopyButton.setIcon(Util.loadImage("copy-icon.png"));
		pwCopyButton.setToolTipText("Copy");
		pwCopyButton.setEnabled(true);
		pwCopyButton.setMargin(new Insets(0, 0, 0, 0));
		pwCopyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				copyTextField(password);
			}
		});
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 0, 0, 5);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		passwordPanel.add(pwCopyButton, c);

		JButton pwPasteButton = new JButton();
		pwPasteButton.setIcon(Util.loadImage("paste-icon.png"));
		pwPasteButton.setToolTipText("Paste");
		pwPasteButton.setEnabled(!readOnly);
		pwPasteButton.setMargin(new Insets(0, 0, 0, 0));
		pwPasteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				pasteToTextField(password);
			}
		});
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 0, 0, 5);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		passwordPanel.add(pwPasteButton, c);

		JCheckBox hidePasswordCheckbox = new JCheckBox(Translator.translate("hide"), true);
		defaultEchoChar = password.getEchoChar();
		hidePasswordCheckbox.setMargin(new Insets(5, 0, 5, 0));
		hidePasswordCheckbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					password.setEchoChar(defaultEchoChar);
				} else {
					password.setEchoChar((char) 0);
				}
			}
		});

		Boolean hideAccountPassword = new Boolean(
				Preferences.get(Preferences.ApplicationOptions.ACCOUNT_HIDE_PASSWORD, "true"));
		hidePasswordCheckbox.setSelected(hideAccountPassword.booleanValue());

		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 0, 0, 0);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		passwordPanel.add(hidePasswordCheckbox, c);

		c.gridx = 1;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 10, 10, 10);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		container.add(passwordPanel, c);

		// URL Row
		JLabel urlLabel = new JLabel(Translator.translate("url"));
		c.gridx = 0;
		c.gridy = 3;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 10, 10, 10);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		container.add(urlLabel, c);

		// This panel will hold the URL field and the copy and paste buttons.
		JPanel urlPanel = new JPanel(new GridBagLayout());

		url = new JTextField(new String(pAccount.getUrl()), 20);
		if (readOnly) {
			url.setEditable(false);
		}
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 0, 0, 5);
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		urlPanel.add(url, c);
		url.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				url.selectAll();
			}
		});

		final JButton urlLaunchButton = new JButton();
		urlLaunchButton.setIcon(Util.loadImage("launch-url-sm.png"));
		urlLaunchButton.setToolTipText("Launch URL");
		urlLaunchButton.setEnabled(true);
		urlLaunchButton.setMargin(new Insets(0, 0, 0, 0));
		urlLaunchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String urlText = url.getText();

				// Check if the selected url is null or emty and inform the user
				// via JoptioPane message
				if ((urlText == null) || (urlText.length() == 0)) {
					JOptionPane.showMessageDialog(urlLaunchButton.getParent(),
							Translator.translate("EmptyUrlJoptionpaneMsg"),
							Translator.translate("UrlErrorJoptionpaneTitle"), JOptionPane.WARNING_MESSAGE);
					// Check if the selected url is a valid formated url(via
					// urlIsValid() method) and inform the user via JoptioPane
					// message
				} else if (!(urlIsValid(urlText))) {
					JOptionPane.showMessageDialog(urlLaunchButton.getParent(),
							Translator.translate("InvalidUrlJoptionpaneMsg"),
							Translator.translate("UrlErrorJoptionpaneTitle"), JOptionPane.WARNING_MESSAGE);
					// Call the method LaunchSelectedURL() using the selected
					// url as input
				} else {
					LaunchSelectedURL(urlText);
				}
			}
		});
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 0, 0, 5);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		urlPanel.add(urlLaunchButton, c);

		JButton urlCopyButton = new JButton();
		urlCopyButton.setIcon(Util.loadImage("copy-icon.png"));
		urlCopyButton.setToolTipText("Copy");
		urlCopyButton.setEnabled(true);
		urlCopyButton.setMargin(new Insets(0, 0, 0, 0));
		urlCopyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				copyTextField(url);
			}
		});
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 0, 0, 5);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		urlPanel.add(urlCopyButton, c);

		JButton urlPasteButton = new JButton();
		urlPasteButton.setIcon(Util.loadImage("paste-icon.png"));
		urlPasteButton.setToolTipText("Paste");
		urlPasteButton.setEnabled(!readOnly);
		urlPasteButton.setMargin(new Insets(0, 0, 0, 0));
		urlPasteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				pasteToTextField(url);
			}
		});
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 0, 0, 5);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		urlPanel.add(urlPasteButton, c);

		c.gridx = 1;
		c.gridy = 3;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 10, 10, 10);
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		container.add(urlPanel, c);

		// Notes Row
		JLabel notesLabel = new JLabel(Translator.translate("notes"));
		c.gridx = 0;
		c.gridy = 4;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.insets = new Insets(10, 10, 10, 10);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		container.add(notesLabel, c);

		// This panel will hold the Notes text area and the copy and paste
		// buttons.
		JPanel notesPanel = new JPanel(new GridBagLayout());

		notes = new JTextArea(new String(pAccount.getNotes()), 10, 20);
		if (readOnly) {
			notes.setEditable(false);
		}
		notes.setLineWrap(true); // Enable line wrapping.
		notes.setWrapStyleWord(true); // Line wrap at whitespace.
		JScrollPane notesScrollPane = new JScrollPane(notes);
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 0, 0, 5);
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		notesPanel.add(notesScrollPane, c);

		JButton notesCopyButton = new JButton();
		notesCopyButton.setIcon(Util.loadImage("copy-icon.png"));
		notesCopyButton.setToolTipText("Copy");
		notesCopyButton.setEnabled(true);
		notesCopyButton.setMargin(new Insets(0, 0, 0, 0));
		notesCopyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				copyTextArea(notes);
			}
		});
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.insets = new Insets(0, 0, 0, 5);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		notesPanel.add(notesCopyButton, c);

		JButton notesPasteButton = new JButton();
		notesPasteButton.setIcon(Util.loadImage("paste-icon.png"));
		notesPasteButton.setToolTipText("Paste");
		notesPasteButton.setEnabled(!readOnly);
		notesPasteButton.setMargin(new Insets(0, 0, 0, 0));
		notesPasteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				pasteToTextArea(notes);
			}
		});
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.insets = new Insets(0, 0, 0, 5);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		notesPanel.add(notesPasteButton, c);

		c.gridx = 1;
		c.gridy = 4;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 10, 10, 10);
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		container.add(notesPanel, c);

		// Seperator Row
		JSeparator sep = new JSeparator();
		c.gridx = 0;
		c.gridy = 5;
		c.anchor = GridBagConstraints.PAGE_END;
		c.insets = new Insets(0, 0, 0, 0);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		container.add(sep, c);

		// Button Row
		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton(Translator.translate("ok"));
		// Link Enter key to okButton
		getRootPane().setDefaultButton(okButton);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButtonAction();
			}
		});
		buttonPanel.add(okButton);
		if (!readOnly) {
			JButton cancelButton = new JButton(Translator.translate("cancel"));
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					closeButtonAction();
				}
			});
			buttonPanel.add(cancelButton);
		}
		c.gridx = 0;
		c.gridy = 6;
		c.anchor = GridBagConstraints.PAGE_END;
		c.insets = new Insets(5, 0, 5, 0);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.NONE;
		container.add(buttonPanel, c);
	} // End AccountDialog constructor

	public boolean okClicked() {
		return okClicked;
	} // End okClicked()

	public AccountInformation getAccount() {
		return pAccount;
	} // End getAccount()

	private void okButtonAction() {

		// Check if the account name has changed.
		if (!pAccount.getAccountName().equals(accountName.getText().trim())) {
			accountChanged = true;
		}

		// [1375397] Ensure that an account with the supplied name doesn't
		// already exist.
		// By checking 'accountNames' we're checking both visible and filtered
		// accounts
		//
		// Only check if an account with the same name exists if the account
		// name has actually changed
		if (accountChanged && existingAccounts.indexOf(accountName.getText().trim()) > -1) {
			JOptionPane.showMessageDialog(parentWindow,
					Translator.translate("accountAlreadyExistsWithName", accountName.getText().trim()),
					Translator.translate("accountAlreadyExists"), JOptionPane.ERROR_MESSAGE);
		} else {
			// Check for changes
			if (!pAccount.getUserId().equals(userId.getText())) {
				accountChanged = true;
			}
			if (!pAccount.getPassword().equals(password.getText())) {
				accountChanged = true;
			}
			if (!pAccount.getUrl().equals(url.getText())) {
				accountChanged = true;
			}
			if (!pAccount.getNotes().equals(notes.getText())) {
				accountChanged = true;
			}

			pAccount.setAccountName(accountName.getText().trim());
			pAccount.setUserId(userId.getText());
			pAccount.setPassword(password.getText());
			pAccount.setUrl(url.getText());
			pAccount.setNotes(notes.getText());

			setVisible(false);
			dispose();
			okClicked = true;
		}
	} // End okButtonAction()

	public boolean getAccountChanged() {
		return accountChanged;
	} // End getAccountChanged()

	private void closeButtonAction() {
		okClicked = false;
		setVisible(false);
		dispose();
	} // End closeButtonAction()

	/**
	 * This method takes as input the user's preferences about password length,
	 * including or excluding Escape Characters, and randomly generates a
	 * password. Then, the method returns the generated password as a String.
	 *
	 * @param PassLength
	 * @param InclEscChars
	 * @return passwordBuffer.toString()
	 */
	private static String GeneratePassword(int PassLength, boolean InclEscChars) {
		SecureRandom random = new SecureRandom();
		StringBuffer passwordBuffer = new StringBuffer();

		if (InclEscChars) {
			for (int i = 0; i < PassLength; i++) {
				passwordBuffer.append(EXTRA_ALLOWED_CHARS[random.nextInt(EXTRA_ALLOWED_CHARS.length)]);
			}
			return passwordBuffer.toString();

		} else {
			for (int i = 0; i < PassLength; i++) {
				passwordBuffer.append(ALLOWED_CHARS[random.nextInt(ALLOWED_CHARS.length)]);
			}
			return passwordBuffer.toString();
		}
	} // End GeneratePassword()

	/**
	 * This method returns true if the generated password satisfies the criteria
	 * of a strong password, including or excluding Escape Characters. If not,
	 * then returns false.
	 *
	 * @param Pass
	 * @param InclEscChars
	 * @return true or false, depending on strength criteria.
	 */
	private static boolean CheckPassStrong(String Pass, boolean InclEscChars) {
		if (InclEscChars) {
			if ((InclUpperCase(Pass)) && (InclLowerCase(Pass)) && (InclNumber(Pass)) && (InclEscape(Pass))) {
				return true;
			} else {
				return false;
			}
		} else {
			if ((InclUpperCase(Pass)) && (InclLowerCase(Pass)) && (InclNumber(Pass))) {
				return true;
			} else {
				return false;
			}
		}
	} // End CheckPassStrong()

	/**
	 * This method returns true if the generated password contains at least one
	 * Upper Case character. If not, then the method returns false.
	 *
	 * @param GeneratedPass
	 * @return true or false, depending on existence of one upper case letter.
	 */
	private static boolean InclUpperCase(String GeneratedPass) {
		char[] PassWordArray = GeneratedPass.toCharArray();
		boolean find = false;
		outerloop: for (int i = 0; i < PassWordArray.length; i++) {
			for (int j = 0; j < UPPERCASE_CHARS.length; j++) {
				if (PassWordArray[i] == UPPERCASE_CHARS[j]) {
					find = true;
					break outerloop;
				}
			}
		}
		if (find) {
			return true;
		} else {
			return false;
		}
	} // End InclUpperCase()

	/**
	 * This method returns true if the generated password contains at least one
	 * Lower Case character. If not, then the method returns false.
	 *
	 * @param GeneratedPass
	 * @return true or false, depending on existence of one lower case letter.
	 */
	private static boolean InclLowerCase(String GeneratedPass) {
		char[] PassWordArray = GeneratedPass.toCharArray();
		boolean find = false;
		outerloop: for (int i = 0; i < PassWordArray.length; i++) {
			for (int j = 0; j < LOWERCASE_CHARS.length; j++) {
				if (PassWordArray[i] == LOWERCASE_CHARS[j]) {
					find = true;
					break outerloop;
				}
			}
		}
		if (find) {
			return true;
		} else {
			return false;
		}
	} // End InclLowerCase()

	/**
	 * This method returns true if the generated password contains at least one
	 * Number. If not, then the method returns false.
	 *
	 * @param GeneratedPass
	 * @return true or false, depending on existence of one number.
	 */
	private static boolean InclNumber(String GeneratedPass) {
		char[] PassWordArray = GeneratedPass.toCharArray();
		boolean find = false;
		outerloop: for (int i = 0; i < PassWordArray.length; i++) {
			for (int j = 0; j < NUMBER_CHARS.length; j++) {
				if (PassWordArray[i] == NUMBER_CHARS[j]) {
					find = true;
					break outerloop;
				}
			}
		}
		if (find) {
			return true;
		} else {
			return false;
		}
	} // End InclNumber()

	/**
	 * This method returns true if the generated password contains at least one
	 * Escape character. If not, then the method returns false.
	 *
	 * @param GeneratedPass
	 * @return true or false, depending on existence of one escape character.
	 */
	private static boolean InclEscape(String GeneratedPass) {
		char[] PassWordArray = GeneratedPass.toCharArray();
		boolean find = false;
		outerloop: for (int i = 0; i < PassWordArray.length; i++) {
			for (int j = 0; j < PUNCTUATION_CHARS.length; j++) {
				if (PassWordArray[i] == PUNCTUATION_CHARS[j]) {
					find = true;
					break outerloop;
				}
			}
		}
		if (find) {
			return true;
		} else {
			return false;
		}
	} // End InclEscape()

	/**
	 * This method takes in a JTextField object and then copies the text of that
	 * text field to the system clipboard.
	 * 
	 * @param textField
	 */
	public void copyTextField(JTextField textField) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection selected = new StringSelection(textField.getText());
		clipboard.setContents(selected, selected);
	}

	/**
	 * This method takes in a JTextArea object and then copies the selected text
	 * in that text area to the system clipboard.
	 * 
	 * @param textArea
	 */
	public void copyTextArea(JTextArea textArea) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection selected = new StringSelection(textArea.getSelectedText());
		clipboard.setContents(selected, selected);
	}

	/**
	 * This method takes in a JTextField object and then sets the text of that
	 * text field to the contents of the system clipboard.
	 * 
	 * @param textField
	 */
	public void pasteToTextField(JTextField textField) {
		String text = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable clipText = clipboard.getContents(null);
		if ((clipText != null) && clipText.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				text = (String) clipText.getTransferData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		textField.setText(text);
	}

	/**
	 * This method takes in a JTextArea object and then inserts the contents of
	 * the system clipboard into that text area at the cursor position.
	 * 
	 * @param textArea
	 */
	public void pasteToTextArea(JTextArea textArea) {
		String text = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable clipText = clipboard.getContents(null);
		if ((clipText != null) && clipText.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				text = (String) clipText.getTransferData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		textArea.insert(text, textArea.getCaretPosition());
		textArea.requestFocus();
	}

	/**
	 * Use com.apache.commons.validator library in order to check the validity
	 * (proper formating, e.x http://www.url.com) of the given URL.
	 * 
	 * @param urL
	 * @return
	 */
	private boolean urlIsValid(String urL) {

		UrlValidator urlValidator = new UrlValidator();
		if (urlValidator.isValid(urL)) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Method that get(as input) the selected Account URL and open this URL via
	 * the default browser of our platform.
	 * 
	 * @param url
	 */
	private void LaunchSelectedURL(String url) {

		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();

			try {
				desktop.browse(new URI(url));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else { // Linux and Mac specific code in order to launch url
			Runtime runtime = Runtime.getRuntime();

			try {
				runtime.exec("xdg-open " + url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
