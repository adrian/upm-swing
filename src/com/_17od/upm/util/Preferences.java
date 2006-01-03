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

        public static final String PROXY_HOST="http.proxy.host";
        public static final String PROXY_PORT="http.proxy.port";
        public static final String UPLOAD_URL="http.upload.url";
    }

    public class DatabaseOptions {
    }

    
    private static final String PREF_FILE = System.getProperty("user.dir") + "\\upm.properties";
    private static Properties preferences; 


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
        if (log.isInfoEnabled()) {
            log.info("Loading the properties file [" + PREF_FILE + "]");
        }
        preferences = new Properties();
        
        try {
            preferences.load(new FileInputStream(PREF_FILE));
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
        preferences.store(new FileOutputStream(PREF_FILE), "Universal Password Manager Preferences");
    }
    
}
