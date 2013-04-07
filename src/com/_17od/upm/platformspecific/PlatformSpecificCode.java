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
package com._17od.upm.platformspecific;

import com._17od.upm.gui.MainWindow;


public abstract class PlatformSpecificCode {

    public abstract void initialiseApplication(final MainWindow mainWindow);


    public static PlatformSpecificCode getInstance() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        PlatformSpecificCode retVal = null;
        
        //Use reflection to create the platform specific code because the chances are it won't compile
        //on other platforms (causing this class to also fail compilation)
        if (isMAC()) {
            Class clazz = Class.forName("com._17od.upm.platformspecific.mac.MACOSXSpecificCode");
            retVal = (PlatformSpecificCode) clazz.newInstance();
        } else {
            retVal = new NonPlatformSpecificCode();
        }
        
        return retVal;
    }
    
    
    public static boolean isMAC() {
        return System.getProperty("os.name").equals("Mac OS X");
    }

	public static boolean isLinux() {
		return System.getProperty("os.name").equals("Linux");
	}

}
