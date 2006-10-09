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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class provides the configuration services to the rest of the application
 */
public class Preferences {

    private static Log log = LogFactory.getLog(Preferences.class); 
    
    public class ApplicationOptions {
        public static final String DB_TO_LOAD_ON_STARTUP= "DBToLoadOnStartup";

        public static final String HTTP_PROXY_HOST="http.proxy.host";
        public static final String HTTP_PROXY_PORT="http.proxy.port";
    }

    public class DatabaseOptions {
    }


    private static final String PREF_FILE = System.getProperty("user.home") + System.getProperty("file.separator") + "upm.properties";
    private static final String PREF_FILE_SYS_PROP = "upm.properties";
    private static Properties preferences; 
    private static String propertiesFile;

    public static String get(String name) {
        String retVal = preferences.getProperty(name); 
        if (log.isDebugEnabled()) {
            log.debug("Returning the property, name=" + name + ", value=" + retVal);
        }
        return retVal;
    }

    
    public static void set(String name, String value) {
        if (log.isDebugEnabled()) {
            log.debug("Setting the property, name=" + name + ", value=" + value);
        }
        preferences.setProperty(name, value);
    }

    
    public static void load() throws FileNotFoundException, IOException {

        //Check for the system property PREF_FILE_SYS_PROP. If supplied it will give the name
        //of the properties file to use. If it's not given then use the properties file in the
        //user's home directory (which may or may not exist)
        propertiesFile = System.getProperty(PREF_FILE_SYS_PROP);
        if (propertiesFile == null || propertiesFile.trim().equals("")) {
            propertiesFile = PREF_FILE;
        }

        //Attempt to load the properties
        try {
            if (log.isInfoEnabled()) {
                log.info("Loading the properties file [" + propertiesFile + "]");
            }
            preferences = new Properties();
            preferences.load(new FileInputStream(propertiesFile));
        } catch (FileNotFoundException e) {
            if (log.isDebugEnabled()) {
                log.debug("Property file not found. Will be created the next time the properties are saved.");
            }
        }
    }


    public static void save() throws IOException  {
        if (log.isDebugEnabled()) {
            log.debug("Saving properties to the file [" + PREF_FILE + "]");
        }
        preferences.store(new FileOutputStream(propertiesFile), "Universal Password Manager Preferences");
    }
    
}
