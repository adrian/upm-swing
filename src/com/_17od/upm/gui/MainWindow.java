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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.Color;
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
import java.security.GeneralSecurityException;
import javax.crypto.IllegalBlockSizeException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.commons.validator.routines.UrlValidator;
import com._17od.upm.crypto.InvalidPasswordException;
import com._17od.upm.database.AccountInformation;
import com._17od.upm.database.ProblemReadingDatabaseFile;
import com._17od.upm.platformspecific.PlatformSpecificCode;
import com._17od.upm.util.Preferences;
import com._17od.upm.util.Translator;
import com._17od.upm.util.Util;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is the main application entry class
 */
public class MainWindow extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static final String applicationName = "Universal Password Manager";

	public static final String NEW_DATABASE_TXT = "newDatabaseMenuItem";
	public static final String OPEN_DATABASE_TXT = "openDatabaseMenuItem";
	public static final String OPEN_DATABASE_FROM_URL_TXT = "openDatabaseFromURLMenuItem";
	public static final String SYNC_DATABASE_TXT = "syncWithRemoteDatabaseMenuItem";
	public static final String CHANGE_MASTER_PASSWORD_TXT = "changeMasterPasswordMenuItem";
	public static final String DATABASE_PROPERTIES_TXT = "databasePropertiesMenuItem";
	public static final String ADD_ACCOUNT_TXT = "addAccountMenuItem";
	public static final String EDIT_ACCOUNT_TXT = "editAccountMenuItem";
	public static final String DELETE_ACCOUNT_TXT = "deleteAccountMenuItem";
	public static final String VIEW_ACCOUNT_TXT = "viewAccountMenuItem";
	public static final String COPY_USERNAME_TXT = "copyUsernameMenuItem";
	public static final String COPY_PASSWORD_TXT = "copyPasswordMenuItem";
	public static final String LAUNCH_URL_TXT = "launchURLMenuItem";
	public static final String OPTIONS_TXT = "optionsMenuItem";
	public static final String ABOUT_TXT = "aboutMenuItem";
	public static final String RESET_SEARCH_TXT = "resetSearchMenuItem";
	public static final String EXIT_TXT = "exitMenuItem";
	public static final String EXPORT_TXT = "exportMenuItem";
	public static final String IMPORT_TXT = "importMenuItem";
	public static final String LOCK_TIMER_TXT = "lock";

	private JButton addAccountButton;
	private JButton editAccountButton;
	private JButton deleteAccountButton;
	private JButton copyUsernameButton;
	private JButton copyPasswordButton;
	private JButton launchURLButton;
	private JButton optionsButton;
	private JButton syncDatabaseButton;
	private JTextField searchField;
	private JButton resetSearchButton;
	private JLabel searchIcon;

	private JMenu databaseMenu;
	private JMenuItem newDatabaseMenuItem;
	private JMenuItem openDatabaseMenuItem;
	private JMenuItem openDatabaseFromURLMenuItem;
	private JMenuItem syncWithRemoteDatabaseMenuItem;
	private JMenuItem changeMasterPasswordMenuItem;
	private JMenuItem databasePropertiesMenuItem;
	private JMenuItem exitMenuItem;
	private JMenu helpMenu;
	private JMenuItem aboutMenuItem;
	private JMenu accountMenu;
	private JMenuItem addAccountMenuItem;
	private JMenuItem editAccountMenuItem;
	private JMenuItem deleteAccountMenuItem;
	private JMenuItem viewAccountMenuItem;
	private JMenuItem copyUsernameMenuItem;
	private JMenuItem copyPasswordMenuItem;
	private JMenuItem launchURLMenuItem;
	private JMenuItem exportMenuItem;
	private JMenuItem importMenuItem;

	private JList accountsListview;
	private JLabel statusBar = new JLabel(" ");
	private JPanel databaseFileChangedPanel;
	public static MainWindow AppWindow;

	private DatabaseActions dbActions;

	public MainWindow(String title) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException, IllegalBlockSizeException, IOException, GeneralSecurityException,
			ProblemReadingDatabaseFile {
		super(title);

		setIconImage(Util.loadImage("upm.gif").getImage());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		PlatformSpecificCode.getInstance().initialiseApplication(this);

		dbActions = new DatabaseActions(this);

		// Set up the content pane.
		addComponentsToPane();

		// Add listener to store current position and size on closing
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				storeWindowBounds();
				try {
					Preferences.save();
				} catch (IOException ex) {
					// Not much we can do at this point
					ex.printStackTrace();
				}
			}

		});

		// Display the window.
		pack();
		setLocationRelativeTo(null);
		boolean restore = Preferences.get(Preferences.ApplicationOptions.REMEMBER_WINDOW_POSITION, "false")
				.equals("true");
		if (restore) {
			restoreWindowBounds();
		}
		Boolean appAlwaysonTop = new Boolean(
				Preferences.get(Preferences.ApplicationOptions.MAINWINDOW_ALWAYS_ON_TOP, "false"));
		setAlwaysOnTop(appAlwaysonTop.booleanValue());
		setVisible(true);

		try {
			// Load the startup database if it's configured
			String db = Preferences.get(Preferences.ApplicationOptions.DB_TO_LOAD_ON_STARTUP);
			if (db != null && !db.equals("")) {
				File dbFile = new File(db);
				if (!dbFile.exists()) {
					dbActions.errorHandler(new Exception(Translator.translate("dbDoesNotExist", db)));
				} else {
					dbActions.openDatabase(db);
				}
			}
		} catch (Exception e) {
			dbActions.errorHandler(e);
		}

		// Give the search field focus
		// I'm using requestFocusInWindow() rathar than requestFocus()
		// because the javadocs recommend it
		searchField.requestFocusInWindow();

	}

	public static void setAppAlwaysonTop(boolean val) {
		AppWindow.setAlwaysOnTop(val);
		// AppWindow.revalidate();

	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				try {
					// Use the System look and feel
					Preferences.load();
					Translator.initialise();
					Double jvmVersion = new Double(System.getProperty("java.specification.version"));
					if (jvmVersion.doubleValue() < 1.4) {
						JOptionPane.showMessageDialog(null, Translator.translate("requireJava14"),
								Translator.translate("problem"), JOptionPane.ERROR_MESSAGE);
						System.exit(1);
					} else {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
						AppWindow = new MainWindow(applicationName);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void addComponentsToPane() {

		// Ensure the layout manager is a BorderLayout
		if (!(getContentPane().getLayout() instanceof GridBagLayout)) {
			getContentPane().setLayout(new GridBagLayout());
		}

		// Create the menubar
		setJMenuBar(createMenuBar());

		GridBagConstraints c = new GridBagConstraints();

		// The toolbar Row
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.insets = new Insets(0, 0, 0, 0);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		Component toolbar = createToolBar();
		getContentPane().add(toolbar, c);

		// Keep the frame background color consistent
		getContentPane().setBackground(toolbar.getBackground());

		// The seperator Row
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, 0, 0, 0);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		getContentPane().add(new JSeparator(), c);

		// The search field row
		searchIcon = new JLabel(Util.loadImage("search.gif"));
		searchIcon.setDisabledIcon(Util.loadImage("search_d.gif"));
		searchIcon.setEnabled(false);
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(5, 1, 5, 1);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		getContentPane().add(searchIcon, c);

		searchField = new JTextField(15);
		searchField.setEnabled(false);
		searchField.setMinimumSize(searchField.getPreferredSize());
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				// This method never seems to be called
			}

			public void insertUpdate(DocumentEvent e) {
				dbActions.filter();
			}

			public void removeUpdate(DocumentEvent e) {
				dbActions.filter();
			}
		});
		searchField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					dbActions.resetSearch();
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					// If the user hits the enter key in the search field and
					// there's only one item
					// in the listview then open that item (this code assumes
					// that the one item in
					// the listview has already been selected. this is done
					// automatically in the
					// DatabaseActions.filter() method)
					if (accountsListview.getModel().getSize() == 1) {
						viewAccountMenuItem.doClick();
					}
				}
			}
		});
		c.gridx = 1;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(5, 1, 5, 1);
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		getContentPane().add(searchField, c);

		resetSearchButton = new JButton(Util.loadImage("stop.gif"));
		resetSearchButton.setDisabledIcon(Util.loadImage("stop_d.gif"));
		resetSearchButton.setEnabled(false);
		resetSearchButton.setToolTipText(Translator.translate(RESET_SEARCH_TXT));
		resetSearchButton.setActionCommand(RESET_SEARCH_TXT);
		resetSearchButton.addActionListener(this);
		resetSearchButton.setBorder(BorderFactory.createEmptyBorder());
		resetSearchButton.setFocusable(false);
		c.gridx = 2;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(5, 1, 5, 1);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		getContentPane().add(resetSearchButton, c);

		// The accounts listview row
		accountsListview = new JList();
		accountsListview.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		accountsListview.setSelectedIndex(0);
		accountsListview.setVisibleRowCount(10);
		accountsListview.setModel(new SortedListModel());
		JScrollPane accountsScrollList = new JScrollPane(accountsListview, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		accountsListview.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				// If the listview gets focus, there is one ore more items in
				// the listview and there is nothing
				// already selected, then select the first item in the list
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
					viewAccountMenuItem.doClick();
				}
			}
		});
		accountsListview.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					viewAccountMenuItem.doClick();
				}
			}
		});
		// Create a shortcut to delete account functionality with DEL(delete)
		// key

		accountsListview.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {

					try {
						dbActions.reloadDatabaseBefore(new DeleteAccountAction());
					} catch (InvalidPasswordException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ProblemReadingDatabaseFile e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
		});

		c.gridx = 0;
		c.gridy = 3;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(0, 1, 1, 1);
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH;
		getContentPane().add(accountsScrollList, c);

		// The "File Changed" panel
		c.gridx = 0;
		c.gridy = 4;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(0, 1, 0, 1);
		c.ipadx = 3;
		c.ipady = 3;
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH;
		databaseFileChangedPanel = new JPanel();
		databaseFileChangedPanel.setLayout(new BoxLayout(databaseFileChangedPanel, BoxLayout.X_AXIS));
		databaseFileChangedPanel.setBackground(new Color(249, 172, 60));
		databaseFileChangedPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		JLabel fileChangedLabel = new JLabel("Database file changed");
		fileChangedLabel.setAlignmentX(LEFT_ALIGNMENT);
		databaseFileChangedPanel.add(fileChangedLabel);
		databaseFileChangedPanel.add(Box.createHorizontalGlue());
		JButton reloadButton = new JButton("Reload");
		reloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dbActions.reloadDatabaseFromDisk();
				} catch (Exception ex) {
					dbActions.errorHandler(ex);
				}
			}
		});
		databaseFileChangedPanel.add(reloadButton);
		databaseFileChangedPanel.setVisible(false);
		getContentPane().add(databaseFileChangedPanel, c);

		// Add the statusbar
		c.gridx = 0;
		c.gridy = 5;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(0, 1, 1, 1);
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		getContentPane().add(statusBar, c);

	}

	public void setFileChangedPanelVisible(boolean visible) {
		databaseFileChangedPanel.setVisible(visible);
	}

	private JToolBar createToolBar() {

		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setRollover(true);

		// The "Add Account" button
		addAccountButton = new JButton();
		addAccountButton.setToolTipText(Translator.translate(ADD_ACCOUNT_TXT));
		addAccountButton.setIcon(Util.loadImage("add_account.gif"));
		addAccountButton.setDisabledIcon(Util.loadImage("add_account_d.gif"));
		;
		addAccountButton.addActionListener(this);
		addAccountButton.setEnabled(false);
		addAccountButton.setActionCommand(ADD_ACCOUNT_TXT);
		toolbar.add(addAccountButton);

		// The "Edit Account" button
		editAccountButton = new JButton();
		editAccountButton.setToolTipText(Translator.translate(EDIT_ACCOUNT_TXT));
		editAccountButton.setIcon(Util.loadImage("edit_account.gif"));
		editAccountButton.setDisabledIcon(Util.loadImage("edit_account_d.gif"));
		;
		editAccountButton.addActionListener(this);
		editAccountButton.setEnabled(false);
		editAccountButton.setActionCommand(EDIT_ACCOUNT_TXT);
		toolbar.add(editAccountButton);

		// The "Delete Account" button
		deleteAccountButton = new JButton();
		deleteAccountButton.setToolTipText(Translator.translate(DELETE_ACCOUNT_TXT));
		deleteAccountButton.setIcon(Util.loadImage("delete_account.gif"));
		deleteAccountButton.setDisabledIcon(Util.loadImage("delete_account_d.gif"));
		;
		deleteAccountButton.addActionListener(this);
		deleteAccountButton.setEnabled(false);
		deleteAccountButton.setActionCommand(DELETE_ACCOUNT_TXT);
		toolbar.add(deleteAccountButton);

		toolbar.addSeparator();

		// The "Copy Username" button
		copyUsernameButton = new JButton();
		copyUsernameButton.setToolTipText(Translator.translate(COPY_USERNAME_TXT));
		copyUsernameButton.setIcon(Util.loadImage("copy_username.gif"));
		copyUsernameButton.setDisabledIcon(Util.loadImage("copy_username_d.gif"));
		;
		copyUsernameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyUsernameToClipboard();
			}
		});
		copyUsernameButton.setEnabled(false);
		toolbar.add(copyUsernameButton);

		// The "Copy Password" button
		copyPasswordButton = new JButton();
		copyPasswordButton.setToolTipText(Translator.translate(COPY_PASSWORD_TXT));
		copyPasswordButton.setIcon(Util.loadImage("copy_password.gif"));
		copyPasswordButton.setDisabledIcon(Util.loadImage("copy_password_d.gif"));
		;
		copyPasswordButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyPasswordToClipboard();
			}
		});
		copyPasswordButton.setEnabled(false);
		toolbar.add(copyPasswordButton);

		// The "Launch URL" button
		launchURLButton = new JButton();
		launchURLButton.setToolTipText(Translator.translate(LAUNCH_URL_TXT));
		launchURLButton.setIcon(Util.loadImage("launch_URL.gif"));
		launchURLButton.setDisabledIcon(Util.loadImage("launch_URL_d.gif"));
		;
		launchURLButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				AccountInformation accInfo = dbActions.getSelectedAccount();
				String uRl = accInfo.getUrl();

				// Check if the selected url is null or emty and inform the user
				// via JoptioPane message
				if ((uRl == null) || (uRl.length() == 0)) {
					JOptionPane.showMessageDialog(launchURLButton.getParent(),
							Translator.translate("EmptyUrlJoptionpaneMsg"),
							Translator.translate("UrlErrorJoptionpaneTitle"), JOptionPane.WARNING_MESSAGE);

					// Check if the selected url is a valid formated url(via
					// urlIsValid() method) and inform the user via JoptioPane
					// message
				} else if (!(urlIsValid(uRl))) {
					JOptionPane.showMessageDialog(launchURLButton.getParent(),
							Translator.translate("InvalidUrlJoptionpaneMsg"),
							Translator.translate("UrlErrorJoptionpaneTitle"), JOptionPane.WARNING_MESSAGE);

					// Call the method LaunchSelectedURL() using the selected
					// url as input
				} else {
					LaunchSelectedURL(uRl);

				}
			}
		});
		launchURLButton.setEnabled(false);
		toolbar.add(launchURLButton);

		toolbar.addSeparator();

		// The "Option" button
		optionsButton = new JButton();
		optionsButton.setToolTipText(Translator.translate(OPTIONS_TXT));
		optionsButton.setIcon(Util.loadImage("options.gif"));
		optionsButton.setDisabledIcon(Util.loadImage("options_d.gif"));
		;
		optionsButton.addActionListener(this);
		optionsButton.setEnabled(true);
		optionsButton.setActionCommand(OPTIONS_TXT);
		toolbar.add(optionsButton);

		toolbar.addSeparator();

		// The Sync database button
		syncDatabaseButton = new JButton();
		syncDatabaseButton.setToolTipText(Translator.translate(SYNC_DATABASE_TXT));
		syncDatabaseButton.setIcon(Util.loadImage("sync.png"));
		syncDatabaseButton.setDisabledIcon(Util.loadImage("sync_d.png"));
		;
		syncDatabaseButton.addActionListener(this);
		syncDatabaseButton.setEnabled(false);
		syncDatabaseButton.setActionCommand(SYNC_DATABASE_TXT);
		toolbar.add(syncDatabaseButton);

		return toolbar;
	}

	private JMenuBar createMenuBar() {

		JMenuBar menuBar = new JMenuBar();

		databaseMenu = new JMenu(Translator.translate("databaseMenu"));
		databaseMenu.setMnemonic(KeyEvent.VK_D);
		menuBar.add(databaseMenu);

		newDatabaseMenuItem = new JMenuItem(Translator.translate(NEW_DATABASE_TXT), KeyEvent.VK_N);
		newDatabaseMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		databaseMenu.add(newDatabaseMenuItem);
		newDatabaseMenuItem.addActionListener(this);
		newDatabaseMenuItem.setActionCommand(NEW_DATABASE_TXT);

		openDatabaseMenuItem = new JMenuItem(Translator.translate(OPEN_DATABASE_TXT), KeyEvent.VK_O);
		openDatabaseMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		databaseMenu.add(openDatabaseMenuItem);
		openDatabaseMenuItem.addActionListener(this);
		openDatabaseMenuItem.setActionCommand(OPEN_DATABASE_TXT);

		openDatabaseFromURLMenuItem = new JMenuItem(Translator.translate(OPEN_DATABASE_FROM_URL_TXT), KeyEvent.VK_L);
		openDatabaseFromURLMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		databaseMenu.add(openDatabaseFromURLMenuItem);
		openDatabaseFromURLMenuItem.addActionListener(this);
		openDatabaseFromURLMenuItem.setActionCommand(OPEN_DATABASE_FROM_URL_TXT);

		databaseMenu.addSeparator();

		syncWithRemoteDatabaseMenuItem = new JMenuItem(Translator.translate(SYNC_DATABASE_TXT), KeyEvent.VK_S);
		syncWithRemoteDatabaseMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		databaseMenu.add(syncWithRemoteDatabaseMenuItem);
		syncWithRemoteDatabaseMenuItem.addActionListener(this);
		syncWithRemoteDatabaseMenuItem.setEnabled(false);
		syncWithRemoteDatabaseMenuItem.setActionCommand(SYNC_DATABASE_TXT);

		changeMasterPasswordMenuItem = new JMenuItem(Translator.translate(CHANGE_MASTER_PASSWORD_TXT), KeyEvent.VK_G);
		changeMasterPasswordMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		databaseMenu.add(changeMasterPasswordMenuItem);
		changeMasterPasswordMenuItem.addActionListener(this);
		changeMasterPasswordMenuItem.setEnabled(false);
		changeMasterPasswordMenuItem.setActionCommand(CHANGE_MASTER_PASSWORD_TXT);

		databasePropertiesMenuItem = new JMenuItem(Translator.translate(DATABASE_PROPERTIES_TXT), KeyEvent.VK_I);
		databasePropertiesMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		databaseMenu.add(databasePropertiesMenuItem);
		databasePropertiesMenuItem.addActionListener(this);
		databasePropertiesMenuItem.setEnabled(false);
		databasePropertiesMenuItem.setActionCommand(DATABASE_PROPERTIES_TXT);

		databaseMenu.addSeparator();

		exportMenuItem = new JMenuItem(Translator.translate(EXPORT_TXT));
		databaseMenu.add(exportMenuItem);
		exportMenuItem.addActionListener(this);
		exportMenuItem.setEnabled(false);
		exportMenuItem.setActionCommand(EXPORT_TXT);

		importMenuItem = new JMenuItem(Translator.translate(IMPORT_TXT));
		databaseMenu.add(importMenuItem);
		importMenuItem.addActionListener(this);
		importMenuItem.setEnabled(false);
		importMenuItem.setActionCommand(IMPORT_TXT);

		accountMenu = new JMenu(Translator.translate("accountMenu"));
		accountMenu.setMnemonic(KeyEvent.VK_A);
		menuBar.add(accountMenu);

		addAccountMenuItem = new JMenuItem(Translator.translate(ADD_ACCOUNT_TXT), KeyEvent.VK_A);
		addAccountMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		accountMenu.add(addAccountMenuItem);
		addAccountMenuItem.addActionListener(this);
		addAccountMenuItem.setEnabled(false);
		addAccountMenuItem.setActionCommand(ADD_ACCOUNT_TXT);

		editAccountMenuItem = new JMenuItem(Translator.translate(EDIT_ACCOUNT_TXT), KeyEvent.VK_E);
		editAccountMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		accountMenu.add(editAccountMenuItem);
		editAccountMenuItem.addActionListener(this);
		editAccountMenuItem.setEnabled(false);
		editAccountMenuItem.setActionCommand(EDIT_ACCOUNT_TXT);

		deleteAccountMenuItem = new JMenuItem(Translator.translate(DELETE_ACCOUNT_TXT), KeyEvent.VK_D);
		deleteAccountMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		accountMenu.add(deleteAccountMenuItem);
		deleteAccountMenuItem.addActionListener(this);
		deleteAccountMenuItem.setEnabled(false);
		deleteAccountMenuItem.setActionCommand(DELETE_ACCOUNT_TXT);

		viewAccountMenuItem = new JMenuItem(Translator.translate(VIEW_ACCOUNT_TXT), KeyEvent.VK_V);
		viewAccountMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		accountMenu.add(viewAccountMenuItem);
		viewAccountMenuItem.addActionListener(this);
		viewAccountMenuItem.setEnabled(false);
		viewAccountMenuItem.setActionCommand(VIEW_ACCOUNT_TXT);

		copyUsernameMenuItem = new JMenuItem(Translator.translate(COPY_USERNAME_TXT), KeyEvent.VK_U);
		copyUsernameMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_U, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		accountMenu.add(copyUsernameMenuItem);
		copyUsernameMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyUsernameToClipboard();
			}
		});
		copyUsernameMenuItem.setEnabled(false);
		copyUsernameMenuItem.setActionCommand(COPY_USERNAME_TXT);

		copyPasswordMenuItem = new JMenuItem(Translator.translate(COPY_PASSWORD_TXT), KeyEvent.VK_P);
		copyPasswordMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		accountMenu.add(copyPasswordMenuItem);
		copyPasswordMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyPasswordToClipboard();
			}
		});

		copyPasswordMenuItem.setEnabled(false);
		copyPasswordMenuItem.setActionCommand(COPY_PASSWORD_TXT);

		launchURLMenuItem = new JMenuItem(Translator.translate(LAUNCH_URL_TXT), KeyEvent.VK_B);
		launchURLMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_B, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		accountMenu.add(launchURLMenuItem);
		launchURLMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				AccountInformation accInfo = dbActions.getSelectedAccount();
				String uRl = accInfo.getUrl();

				// Check if the selected url is null or emty and inform the user
				// via JoptioPane message
				if ((uRl == null) || (uRl.length() == 0)) {
					JOptionPane.showMessageDialog(accountMenu.getParent().getParent(),
							Translator.translate("EmptyUrlJoptionpaneMsg"),
							Translator.translate("UrlErrorJoptionpaneTitle"), JOptionPane.WARNING_MESSAGE);

					// Check if the selected url is a valid formated url(via
					// urlIsValid() method) and inform the user via JoptioPane
					// message
				} else if (!(urlIsValid(uRl))) {
					JOptionPane.showMessageDialog(accountMenu.getParent().getParent(),
							Translator.translate("InvalidUrlJoptionpaneMsg"),
							Translator.translate("UrlErrorJoptionpaneTitle"), JOptionPane.WARNING_MESSAGE);

					// Call the method LaunchSelectedURL() using the selected
					// url as input
				} else {
					LaunchSelectedURL(uRl);

				}
			}
		});

		launchURLMenuItem.setEnabled(false);
		launchURLMenuItem.setActionCommand(LAUNCH_URL_TXT);

		exitMenuItem = new JMenuItem(Translator.translate(EXIT_TXT), KeyEvent.VK_X);
		exitMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		exitMenuItem.addActionListener(this);
		exitMenuItem.setActionCommand(EXIT_TXT);

		aboutMenuItem = new JMenuItem(Translator.translate(ABOUT_TXT), KeyEvent.VK_A);
		aboutMenuItem.addActionListener(this);
		aboutMenuItem.setActionCommand(ABOUT_TXT);

		// Because the MAC version of UPM will have a program item in the menu
		// bar then these items
		// only need to be added on non-mac platforms
		if (!PlatformSpecificCode.isMAC()) {
			databaseMenu.addSeparator();
			databaseMenu.add(exitMenuItem);

			helpMenu = new JMenu(Translator.translate("helpMenu"));
			helpMenu.setMnemonic(KeyEvent.VK_H);
			menuBar.add(helpMenu);

			helpMenu.add(aboutMenuItem);
		}

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

	// Use com.apache.commons.validator library in order to check the
	// validity(proper formating, e.x http://www.url.com) of the given url
	private boolean urlIsValid(String urL) {

		UrlValidator urlValidator = new UrlValidator();
		if (urlValidator.isValid(urL)) {
			return true;
		} else {
			return false;
		}

	}

	// Method that get(as input) the selected Account URL and open this URL via
	// the default browser of our platform

	private void LaunchSelectedURL(String url) {

		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();

			try {
				desktop.browse(new URI(url));

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
			// Linux and Mac specific code in order to launch url
		} else {
			Runtime runtime = Runtime.getRuntime();

			try {
				runtime.exec("xdg-open " + url);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * Writes current window position and size to the preferences
	 */
	private void storeWindowBounds() {
		Preferences.set(Preferences.ApplicationOptions.XLOC, Integer.toString(this.getX()));
		Preferences.set(Preferences.ApplicationOptions.YLOC, Integer.toString(this.getY()));
		Preferences.set(Preferences.ApplicationOptions.WWIDTH, Integer.toString(this.getWidth()));
		Preferences.set(Preferences.ApplicationOptions.WHEIGHT, Integer.toString(this.getHeight()));
	}

	/**
	 * Restores the window position and size to those found in the preferences
	 * Checks if the window can still be displayed, if not, revert to default
	 * position
	 */
	private void restoreWindowBounds() {
		int x = Preferences.getInt(Preferences.ApplicationOptions.XLOC, this.getX());
		int y = Preferences.getInt(Preferences.ApplicationOptions.YLOC, this.getY());
		// check if this position can still be displayed to avoid problems
		// for people who dragged the window on a screen that is no longer
		// connected.
		if (getGraphicsConfigurationContaining(x, y) == null) {
			x = this.getX();
			y = this.getY();
		}
		int width = Preferences.getInt(Preferences.ApplicationOptions.WWIDTH, (this.getWidth()));
		int height = Preferences.getInt(Preferences.ApplicationOptions.WHEIGHT, this.getHeight());

		this.setBounds(x, y, width, height);
	}

	/**
	 * Utility function for restoreWindowBounds
	 */
	private GraphicsConfiguration getGraphicsConfigurationContaining(int x, int y) {
		ArrayList configs = new ArrayList();
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = env.getScreenDevices();
		for (int i = 0; i < devices.length; i++) {
			GraphicsConfiguration[] gconfigs = devices[i].getConfigurations();
			configs.addAll(Arrays.asList(gconfigs));
		}
		for (int i = 0; i < configs.size(); i++) {
			GraphicsConfiguration config = ((GraphicsConfiguration) configs.get(i));
			Rectangle bounds = config.getBounds();
			if (bounds.contains(x, y)) {
				return config;
			}
		}
		return null;
	}

	/**
	 * Convenience method to iterate over all graphics configurations.
	 */
	private static ArrayList getConfigs() {
		ArrayList result = new ArrayList();
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = env.getScreenDevices();
		for (int i = 0; i < devices.length; i++) {
			GraphicsConfiguration[] configs = devices[i].getConfigurations();
			result.addAll(Arrays.asList(configs));
		}
		return result;
	}

	public JButton getCopyPasswordButton() {
		return copyPasswordButton;
	}

	public JButton getLaunchURLButton() {
		return launchURLButton;
	}

	public JButton getCopyUsernameButton() {
		return copyUsernameButton;
	}

	public JButton getEditAccountButton() {
		return editAccountButton;
	}

	public JButton getAddAccountButton() {
		return addAccountButton;
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

	public JLabel getSearchIcon() {
		return searchIcon;
	}

	public JButton getResetSearchButton() {
		return resetSearchButton;
	}

	public JMenuItem getAboutMenuItem() {
		return aboutMenuItem;
	}

	public JMenuItem getExitMenuItem() {
		return exitMenuItem;
	}

	public JMenuItem getCopyPasswordMenuItem() {
		return copyPasswordMenuItem;
	}

	public JMenuItem getLaunchURLMenuItem() {
		return launchURLMenuItem;
	}

	public JMenuItem getCopyUsernameMenuItem() {
		return copyUsernameMenuItem;
	}

	public JMenuItem getDeleteAccountMenuItem() {
		return deleteAccountMenuItem;
	}

	public JMenuItem getViewAccountMenuItem() {
		return viewAccountMenuItem;
	}

	public JMenuItem getEditAccountMenuItem() {
		return editAccountMenuItem;
	}

	public static String getApplicationName() {
		return applicationName;
	}

	public JMenuItem getAddAccountMenuItem() {
		return addAccountMenuItem;
	}

	public JMenuItem getChangeMasterPasswordMenuItem() {
		return changeMasterPasswordMenuItem;
	}

	public JMenuItem getDatabasePropertiesMenuItem() {
		return databasePropertiesMenuItem;
	}

	public void actionPerformed(ActionEvent event) {

		try {
			if (event.getActionCommand() == MainWindow.NEW_DATABASE_TXT) {
				dbActions.newDatabase();
			} else if (event.getActionCommand() == MainWindow.OPEN_DATABASE_TXT) {
				dbActions.openDatabase();
			} else if (event.getActionCommand() == MainWindow.OPEN_DATABASE_FROM_URL_TXT) {
				dbActions.openDatabaseFromURL();
			} else if (event.getActionCommand() == MainWindow.SYNC_DATABASE_TXT) {
				dbActions.syncWithRemoteDatabase();
			} else if (event.getActionCommand() == MainWindow.ADD_ACCOUNT_TXT) {
				dbActions.reloadDatabaseBefore(new AddAccountAction());
			} else if (event.getActionCommand() == MainWindow.EDIT_ACCOUNT_TXT) {
				String selectedAccName = (String) this.accountsListview.getSelectedValue();
				dbActions.reloadDatabaseBefore(new EditAccountAction(selectedAccName));
			} else if (event.getActionCommand() == MainWindow.DELETE_ACCOUNT_TXT) {
				dbActions.reloadDatabaseBefore(new DeleteAccountAction());
			} else if (event.getActionCommand() == MainWindow.VIEW_ACCOUNT_TXT) {
				dbActions.viewAccount();
			} else if (event.getActionCommand() == MainWindow.OPTIONS_TXT) {
				dbActions.options();
			} else if (event.getActionCommand() == MainWindow.ABOUT_TXT) {
				dbActions.showAbout();
			} else if (event.getActionCommand() == MainWindow.RESET_SEARCH_TXT) {
				dbActions.resetSearch();
			} else if (event.getActionCommand() == MainWindow.CHANGE_MASTER_PASSWORD_TXT) {
				dbActions.reloadDatabaseBefore(new ChangeMasterPasswordAction());
			} else if (event.getActionCommand() == MainWindow.DATABASE_PROPERTIES_TXT) {
				dbActions.reloadDatabaseBefore(new ShowDatabasePropertiesAction());
			} else if (event.getActionCommand() == MainWindow.EXIT_TXT) {
				dbActions.exitApplication();
			} else if (event.getActionCommand() == MainWindow.EXPORT_TXT) {
				dbActions.export();
			} else if (event.getActionCommand() == MainWindow.IMPORT_TXT) {
				dbActions.reloadDatabaseBefore(new ImportAccountsAction());
			}
		} catch (Exception e) {
			dbActions.errorHandler(e);
		}
	}

	public JButton getSyncWithRemoteDatabaseButton() {
		return syncDatabaseButton;
	}

	public JMenuItem getSyncWithRemoteDatabaseMenuItem() {
		return syncWithRemoteDatabaseMenuItem;
	}

	public JMenuItem getExportMenuItem() {
		return exportMenuItem;
	}

	public JMenuItem getImportMenuItem() {
		return importMenuItem;
	}

	public JLabel getStatusBar() {
		return statusBar;
	}

	public JPanel getDatabaseFileChangedPanel() {
		return databaseFileChangedPanel;
	}

	/**
	 * Initialise all the menus, buttons, etc to take account of the language
	 * selected by the user
	 */
	public void initialiseControlsWithDefaultLanguage() {
		databaseMenu.setText(Translator.translate("databaseMenu"));
		newDatabaseMenuItem.setText(Translator.translate(NEW_DATABASE_TXT));
		openDatabaseMenuItem.setText(Translator.translate(OPEN_DATABASE_TXT));
		openDatabaseFromURLMenuItem.setText(Translator.translate(OPEN_DATABASE_FROM_URL_TXT));
		syncWithRemoteDatabaseMenuItem.setText(Translator.translate(SYNC_DATABASE_TXT));
		changeMasterPasswordMenuItem.setText(Translator.translate(CHANGE_MASTER_PASSWORD_TXT));
		databasePropertiesMenuItem.setText(Translator.translate(DATABASE_PROPERTIES_TXT));
		accountMenu.setText(Translator.translate("accountMenu"));
		addAccountMenuItem.setText(Translator.translate(ADD_ACCOUNT_TXT));
		editAccountMenuItem.setText(Translator.translate(EDIT_ACCOUNT_TXT));
		deleteAccountMenuItem.setText(Translator.translate(DELETE_ACCOUNT_TXT));
		viewAccountMenuItem.setText(Translator.translate(VIEW_ACCOUNT_TXT));
		copyUsernameMenuItem.setText(Translator.translate(COPY_USERNAME_TXT));
		copyPasswordMenuItem.setText(Translator.translate(COPY_PASSWORD_TXT));
		launchURLMenuItem.setText(Translator.translate(LAUNCH_URL_TXT));
		exitMenuItem.setText(Translator.translate(EXIT_TXT));
		aboutMenuItem.setText(Translator.translate(ABOUT_TXT));
		exportMenuItem.setText(Translator.translate(EXPORT_TXT));
		importMenuItem.setText(Translator.translate(IMPORT_TXT));

		// Because the MAC version of UPM will have a program item in the menu
		// bar then these items
		// only need to be added on non-mac platforms
		if (!PlatformSpecificCode.isMAC()) {
			helpMenu.setText(Translator.translate("helpMenu"));
		}

		addAccountButton.setToolTipText(Translator.translate(ADD_ACCOUNT_TXT));
		editAccountButton.setToolTipText(Translator.translate(EDIT_ACCOUNT_TXT));
		deleteAccountButton.setToolTipText(Translator.translate(DELETE_ACCOUNT_TXT));
		copyUsernameButton.setToolTipText(Translator.translate(COPY_USERNAME_TXT));
		copyPasswordButton.setToolTipText(Translator.translate(COPY_PASSWORD_TXT));
		launchURLButton.setToolTipText(Translator.translate(LAUNCH_URL_TXT));
		optionsButton.setToolTipText(Translator.translate(OPTIONS_TXT));
		syncDatabaseButton.setToolTipText(Translator.translate(SYNC_DATABASE_TXT));
		resetSearchButton.setToolTipText(Translator.translate(RESET_SEARCH_TXT));
	}

	public interface ChangeDatabaseAction {
		public void doAction();
	}

	private class EditAccountAction implements ChangeDatabaseAction {
		private String accountToEdit;

		public EditAccountAction(String accountToEdit) {
			this.accountToEdit = accountToEdit;
		}

		public void doAction() {
			try {
				dbActions.editAccount(accountToEdit);
			} catch (Exception e) {
				dbActions.errorHandler(e);
			}
		}
	}

	private class ChangeMasterPasswordAction implements ChangeDatabaseAction {
		public void doAction() {
			try {
				dbActions.changeMasterPassword();
			} catch (Exception e) {
				dbActions.errorHandler(e);
			}
		}
	}

	private class DeleteAccountAction implements ChangeDatabaseAction {
		public void doAction() {
			try {
				dbActions.deleteAccount();
			} catch (Exception e) {
				dbActions.errorHandler(e);
			}
		}
	}

	private class AddAccountAction implements ChangeDatabaseAction {
		public void doAction() {
			try {
				dbActions.addAccount();
			} catch (Exception e) {
				dbActions.errorHandler(e);
			}
		}
	}

	private class ShowDatabasePropertiesAction implements ChangeDatabaseAction {
		public void doAction() {
			try {
				dbActions.showDatabaseProperties();
			} catch (Exception e) {
				dbActions.errorHandler(e);
			}
		}
	}

	private class ImportAccountsAction implements ChangeDatabaseAction {
		public void doAction() {
			try {
				dbActions.importAccounts();
			} catch (Exception e) {
				dbActions.errorHandler(e);
			}
		}
	}

}
