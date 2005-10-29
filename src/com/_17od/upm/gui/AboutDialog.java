package com._17od.upm.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com._17od.upm.util.Util;

public class AboutDialog extends EscapeDialog {

    
    public AboutDialog(JFrame frame) {
        super(frame, "About UPM", true);
        
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

        JLabel textLabel = new JLabel("http://www.17od.com");
        textLabel.setFont(font2);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(textLabel);

        panel.add(Box.createRigidArea(new Dimension(0,10)));

        JButton okButton = new JButton("OK");
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
