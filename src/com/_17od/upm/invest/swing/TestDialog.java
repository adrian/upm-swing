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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
		final JDialog dialog = new JDialog(frame, true);
		dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
		dialog.getContentPane().setBackground(Color.BLUE);

		JPanel panel = new JPanel(new SpringLayout());
		panel.setBackground(Color.GREEN);
		JLabel label = new JLabel("User Id");
		JTextField userId = new JTextField(20);
		//userId.setMaximumSize(userId.getPreferredSize());
		label.setLabelFor(userId);
		panel.add(label);
		panel.add(userId);
		SpringUtilities.makeCompactGrid(panel,
                1, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        dialog.getContentPane().add(panel);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton button = new JButton("Close");
        dialog.getContentPane().add(button);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
		
		dialog.pack();
		dialog.show();
	}
	
}
