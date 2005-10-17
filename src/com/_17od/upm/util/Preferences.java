/*
 * $Id$
 * 
 * Universal Password Manager
 * Copyright (C) 2005 Adrian Smith
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;


/**
 * This class provides the configuration services to the rest of the application
 */
public class Preferences {

    private static final String DB_TO_LOAD_ON_STARTUP= "DBToLoadOnStartup";
    private static final String PREF_FILE = System.getProperty("user.dir") + "/upm.xml";

    private static java.util.prefs.Preferences pref = java.util.prefs.Preferences.userNodeForPackage(Preferences.class);

    
    public static String getDBToOptionOnStartup() {
        return pref.get(DB_TO_LOAD_ON_STARTUP, null);
    }

    
    public static void setDBToOptionOnStartup(String dbToOptionOnStartup) {
        pref.put(DB_TO_LOAD_ON_STARTUP, dbToOptionOnStartup);
    }

    
    public static void save() throws FileNotFoundException, IOException, BackingStoreException {
        pref.exportNode(new FileOutputStream(PREF_FILE));
    }
    
}
