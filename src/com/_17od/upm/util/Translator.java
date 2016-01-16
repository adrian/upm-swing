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
package com._17od.upm.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;


public class Translator {

    public static Locale[] SUPPORTED_LOCALES = {
        Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN,Locale.ITALIAN, new Locale("es"),
        new Locale("nl"),new Locale("pl"),new Locale("el"),new Locale("cs")};
        
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


    public static Locale getCurrentLocale() {
        return resourceBundle.getLocale();
    }


    public static void initialise() {
        Locale locale = new Locale("en");
        String localePreference = Preferences.get(Preferences.ApplicationOptions.LOCALE);
        if (localePreference != null) {
            locale = new Locale(localePreference);
        }
        loadBundle(locale);
    }


    public static void loadBundle(Locale locale) {
        resourceBundle = ResourceBundle.getBundle("upm", locale);
        formatter = new MessageFormat("");
        formatter.setLocale(locale);
    }

}
