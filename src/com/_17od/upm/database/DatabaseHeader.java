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


public class DatabaseHeader extends FlatPackObject {

    private int majorVersion;
    private int minorVersion;
    private int patchVersion;
    
    
    public DatabaseHeader(InputStream is) throws IOException, ProblemReadingDatabaseFile {
        assemble(is);
    }
    
    
    public DatabaseHeader(int majorVersion, int minorVersion, int patchVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchVersion = patchVersion;
    }
    
    
    public void flatPack(OutputStream os) throws IOException {
        os.write(flatPack(String.valueOf(majorVersion)));
        os.write(flatPack(String.valueOf(minorVersion)));
        os.write(flatPack(String.valueOf(patchVersion)));
    }

    
    private void assemble(InputStream is) throws IOException, ProblemReadingDatabaseFile {
        majorVersion = getInt(is);
        minorVersion = getInt(is);
        patchVersion = getInt(is);
    }    

    public String getVersion() {
        StringBuffer buf = new StringBuffer();
        buf.append(majorVersion);
        buf.append('.');
        buf.append(minorVersion);
        buf.append('.');
        buf.append(patchVersion);
        return buf.toString();
    }


    public int getMajorVersion() {
        return majorVersion;
    }


    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }


    public int getMinorVersion() {
        return minorVersion;
    }


    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }


    public int getPatchVersion() {
        return patchVersion;
    }


    public void setPatchVersion(int patchVersion) {
        this.patchVersion = patchVersion;
    }

}
