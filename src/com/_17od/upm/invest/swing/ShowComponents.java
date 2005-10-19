/*
 * Created on Oct 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com._17od.upm.invest.swing;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JOptionPane;

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
	}
	
	
	private static void showComponents(Component c, int indent) {
		System.out.print(space(indent) + c.getClass().getName());
		if (c instanceof Container) {
			Container cont = (Container) c;
			if (cont.getLayout() != null) {
				System.out.println(" (" + cont.getLayout().getClass().getName() + ")");
			}
			for (int i=0; i<cont.getComponentCount(); i++) {
				showComponents(cont.getComponent(i), indent + 1);
			}
		} else {
			System.out.print('\n');
		}
	}
	
	private static String space(int i) {
		String s = new String("");
		while (s.length() < i) {
			s = s + " ";
		}
		return s;
	}
}
