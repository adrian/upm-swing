package com._17od.upm.gui;

import java.awt.Frame;

import javax.swing.JDialog;

public class GetPasswordDialog extends JDialog {

    private GetPasswordDialog(Frame parent) {
        super(parent, true);
    }
    
    public static void showDialog(Frame parent) {
        GetPasswordDialog d = new GetPasswordDialog(parent);
    }
    
}
