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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import com._17od.upm.util.Translator;

public class AboutDialog extends EscapeDialog {

    private static final long serialVersionUID = 1L;

    public AboutDialog(JFrame frame) {
        super(frame, Translator.translate("aboutUPM"), true);

        String version = AboutDialog.class.getPackage().getImplementationVersion();
        if (version == null) {
            version = "<version unknown>";
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JTextArea jTextArea = new JTextArea();
        StringBuffer aboutText = new StringBuffer();
        aboutText.append("Universal Password Manager\n");
        aboutText.append(version);
        aboutText.append("\n\n");
        aboutText.append("Copyright \u00a9 2005-2013 Adrian Smith & Contributors\n\n");
        aboutText.append("adrian@17od.com\n");
        aboutText.append("http://upm.sourceforge.net");
        jTextArea.setText(aboutText.toString());
        jTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        jTextArea.setEditable(false);
        jTextArea.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jTextArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(jTextArea);
        panel.add(new JSeparator());

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton okButton = new JButton(Translator.translate("ok"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(okButton);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        getRootPane().setDefaultButton(okButton);
        getContentPane().add(panel);
        
        setResizable(false);
        
    }

}
