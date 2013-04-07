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
package com._17od.upm.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseOptions extends FlatPackObject {

    private String remoteLocation;
    private String authDBEntry;

    
    public DatabaseOptions() {
        remoteLocation = "";
        authDBEntry = "";
    }
    
    
    public DatabaseOptions(InputStream is) throws IOException, ProblemReadingDatabaseFile {
        remoteLocation = getString(is);
        authDBEntry = getString(is);
    }
    
    
    public void setRemoteLocation(String remoteLocation) {
        if (remoteLocation == null) {
            remoteLocation = "";
        }
        this.remoteLocation = remoteLocation;
    }
    
    
    public String getRemoteLocation() {
        return remoteLocation;
    }
    
    
    public void flatPack(OutputStream os) throws IOException {
        os.write(flatPack(remoteLocation));
        os.write(flatPack(authDBEntry));
    }


    public String getAuthDBEntry() {
        return authDBEntry;
    }


    public void setAuthDBEntry(String authDBEntry) {
        if (authDBEntry == null) {
            authDBEntry = "";
        }
        this.authDBEntry = authDBEntry;
    }

}
