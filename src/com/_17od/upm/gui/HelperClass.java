package com._17od.upm.gui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HelperClass implements ActionListener{

	public void actionPerformed(ActionEvent e) {
		System.out.println("Pressed ;) ");
		
		if(Desktop.isDesktopSupported())
		{
		  try {
			Desktop.getDesktop().browse(new URI(makeValidHttp("https://www.github.com/adrian/upm-swing")));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		}

		
	}
	public String makeValidHttp(String webpage){
		System.out.println("Valid HTTP Made :)");
		try{
			if (webpage.substring(0,11).equals("http://www.") || webpage.substring(0,12).equals("https://www.") ){
				return webpage;
			}
			
			if (webpage.substring(0,4).equals("www.")){
				return "http://"+webpage;
			}
			} catch (Exception e){ 
			//	System.out.println(e);
				}
			return "http://www."+webpage;
	}

}
