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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;


public class ErrorMessageDialog extends JDialog {

    public ErrorMessageDialog(JFrame frame, Exception e) {
        super(frame, "Error", true);
        
        if (!(getContentPane().getLayout() instanceof BorderLayout)) {
            getContentPane().setLayout(new BorderLayout());
        }
        
        JPanel messagePanel = new JPanel(new BorderLayout(10, 10));
        Box iconBox = new Box(BoxLayout.Y_AXIS);
        iconBox.add(new JLabel(UIManager.getIcon("OptionPane.errorIcon")));
        iconBox.add(Box.createGlue());
        messagePanel.add(iconBox, BorderLayout.WEST);
        String errorMessage = e.getClass().getName();
        if (e.getMessage() != null) {
            errorMessage = errorMessage + e.getMessage();
        }
        JLabel errorMessageLabel = new JLabel(errorMessage);
        messagePanel.add(errorMessageLabel, BorderLayout.CENTER);
        getContentPane().add(messagePanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JButton("Details"));
        buttonPanel.add(new JButton("Close"));
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        
    }
    
}
