package com._17od.upm.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class OpenDatabaseFromURLDialog extends EscapeDialog {

    public JTextField urlTextField;
    public JTextField usernameTextField;
    public JTextField passwordTextField;
    private boolean okClicked = false;
    
    
    public OpenDatabaseFromURLDialog(final JFrame frame) {
        super(frame, "Open Database From URL", true);
        
        Container container = getContentPane();

        GridBagConstraints c = new GridBagConstraints();
        container.setLayout(new GridBagLayout());

        // The URL Label row
        JLabel urlLabel = new JLabel("URL");
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 5, 0, 0);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        container.add(urlLabel, c);

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
        container.add(urlTextField, c);

        // The username label field
        JLabel usernameLabel = new JLabel("User Name");
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 5, 0, 0);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        container.add(usernameLabel, c);

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
        container.add(usernameTextField, c);

        // The password label field
        JLabel passwordLabel = new JLabel("Password");
        c.gridx = 0;
        c.gridy = 4;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 5, 0, 0);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        container.add(passwordLabel, c);

        // The username inpur field
        passwordTextField = new JTextField(10);
        c.gridx = 0;
        c.gridy = 5;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 5, 5, 5);
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        container.add(passwordTextField, c);

        // Some spacing
        Component verticalSpace = Box.createVerticalGlue();
        c.gridy = 6;
        c.weighty = 1;
        container.add(verticalSpace, c);

        // The buttons row
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okClicked = true;
            }
        });
        buttonPanel.add(okButton);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        buttonPanel.add(cancelButton);

        c.gridy = 7;
        c.weighty = 1;
        c.anchor = GridBagConstraints.CENTER;
        container.add(buttonPanel, c);

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
