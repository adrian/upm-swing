package com._17od.upm.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class PasswordHelper implements ActionListener
{
    boolean called = false;

    public void actionPerformed(ActionEvent e)
    {
        if(e.getActionCommand().equals("HINT"))
        {
            String hints = "Password should:\n- Be between 8-12 characters length\n- Have at least one special character" +
                    "\n- Have at least one uppercase letter\n- Not be a dictionary word\n- Not be a recent password\n";
            JOptionPane.showMessageDialog(null, hints);
            //JOptionPane.showMessageDialog(null, "Your mom is a jOptionPane.");
            //System.out.println(" Password Hints ");
            called = true;
        }
    }

    public boolean getCalledState()
    {
        return called;
    }
}
