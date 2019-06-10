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
package com._17od.upm.platformspecific.mac;

import com._17od.upm.gui.MainWindow;
import com._17od.upm.platformspecific.PlatformSpecificCode;
import java.awt.Desktop;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.PreferencesHandler;
import java.awt.desktop.PreferencesEvent;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitResponse;


public class DesktopModuleCode extends PlatformSpecificCode {

    public void initialiseApplication(final MainWindow mainWindow) {
        Desktop desktop = Desktop.getDesktop();
        desktop.setAboutHandler(new AboutHandler() {
            public void handleAbout(AboutEvent e) {
                mainWindow.getAboutMenuItem().doClick();
            }
        });
        desktop.setPreferencesHandler(new PreferencesHandler() {
            public void handlePreferences(PreferencesEvent e) {
                mainWindow.getOptionsButton().doClick();
            }
        });
        desktop.setQuitHandler(new QuitHandler() {
            public void handleQuitRequestWith(QuitEvent e, QuitResponse response) {
                response.performQuit();
                mainWindow.getExitMenuItem().doClick();
            }
        });
    }

}
