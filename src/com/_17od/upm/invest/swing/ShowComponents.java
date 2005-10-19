/*
 * Created on Oct 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com._17od.upm.invest.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

/**
 * @author smitha
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ShowComponents {

	public static void main(String[] args) {
	    JOptionPane pane = new JOptionPane("Message", JOptionPane.ERROR_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
	    showComponents(pane, 0);
        drawBorder(pane);
        JDialog dialog = pane.createDialog(null, "abc");
        dialog.show();
	}
	
	
    private static void drawBorder(Component c) {
        Border b = BorderFactory.createLineBorder(Color.BLACK);
        if (c instanceof Container) {
            Container cont = (Container) c;
            for (int i=0; i<cont.getComponentCount(); i++) {
                drawBorder(cont.getComponent(i));
            }
        }
    }
    
    
	private static void showComponents(Component c, int indent) {
		System.out.print(space(indent) + c.getClass().getName() + " " + c.getName());
		if (c instanceof Container) {
			Container cont = (Container) c;
			if (cont.getLayout() != null) {
			    System.out.println(" (" + cont.getLayout().getClass().getName() + ")");
			    for (int i=0; i<cont.getComponentCount(); i++) {
			        showComponents(cont.getComponent(i), indent + 1);
			    }
			} else {
                System.out.print('\n');
			}
		} else {
			System.out.print('\n');
		}
	}

	private static String space(int i) {
		String s = new String("");
		while (s.length() < i) {
			s = s + '\t';
		}
		return s;
	}
}
