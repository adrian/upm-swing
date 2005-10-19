/*
 * Created on Oct 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com._17od.upm.invest.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;


/**
 * @author smitha
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestDialog implements ActionListener {

	private JFrame frame;

	
	public TestDialog() {
		frame = new JFrame("Hello World Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton button = new JButton("A Button");
		button.addActionListener(this);
		
        frame.getContentPane().add(button);
		
		frame.pack();
		frame.show();
	}
	

	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new TestDialog();
            }
        });
	}


	public void actionPerformed(ActionEvent arg0) {
		JOptionPane pane = new JOptionPane();
		pane.setMessageType(JOptionPane.ERROR_MESSAGE);
		JDialog dialog = pane.createDialog(frame, "Test Dialog");
		dialog.show();
	}


}
