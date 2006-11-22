package com._17od.upm.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;


public class Translator {

	private static Locale locale = Locale.ENGLISH;
	private static ResourceBundle resourceBundle;
	private static MessageFormat formatter;


	public static String translate(String messageName, Object[] params) {
		formatter.applyPattern(resourceBundle.getString(messageName));
		return formatter.format(params);
	}


	public static String translate(String messageName, Object param) {
		formatter.applyPattern(resourceBundle.getString(messageName));
		return formatter.format(new Object[] {param});
	}


	public static String translate(String messageName) {
		return resourceBundle.getString(messageName);
	}


	public static Locale getLocale() {
		if (locale == null) {
		}
		return locale;
	}


    public static void initialise() {
    	// Get the locale to use from the user's preferences (if there is one)
    	String localePreference = Preferences.get(Preferences.ApplicationOptions.LOCALE);
    	if (localePreference != null) {
    		locale = new Locale(localePreference);
    	}

    	resourceBundle = ResourceBundle.getBundle("upm", locale);
    	formatter = new MessageFormat("");
    	formatter.setLocale(locale);
    }

}
