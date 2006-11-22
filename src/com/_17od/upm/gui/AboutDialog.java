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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com._17od.upm.util.Translator;
import com._17od.upm.util.Util;

public class AboutDialog extends EscapeDialog {

    
    public AboutDialog(JFrame frame) {
        super(frame, Translator.translate("aboutUPM"), true);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        panel.add(Box.createRigidArea(new Dimension(0,5)));

        JLabel logo = new JLabel(Util.loadImage("upm.gif"));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(logo);        
        
        Font font1 = new Font("Helvetica", Font.PLAIN,  22);
        JLabel productLabel = new JLabel("UPM");
        productLabel.setFont(font1);
        productLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(productLabel);
        
        panel.add(Box.createRigidArea(new Dimension(200,10)));
        
        Font font2 = new Font("Helvetica", Font.PLAIN,  11);
        String version = AboutDialog.class.getPackage().getImplementationVersion();
        JLabel versionLabel = new JLabel(version);
        versionLabel.setFont(font2);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(versionLabel);

        panel.add(Box.createRigidArea(new Dimension(0,5)));

        JLabel textLabel = new JLabel("http://upm.sourceforge.net");
        textLabel.setFont(font2);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(textLabel);

        panel.add(Box.createRigidArea(new Dimension(0,10)));

        JButton okButton = new JButton(Translator.translate("ok"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(okButton);

        panel.add(Box.createRigidArea(new Dimension(0,10)));

        getContentPane().add(panel);
        
        setResizable(false);
        
    }


}
