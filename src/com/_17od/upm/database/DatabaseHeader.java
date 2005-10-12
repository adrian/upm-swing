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
package com._17od.upm.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DatabaseHeader extends FlatPackObject {

	private String majorVersion;
	private String minorVersion;
	private String patchVersion;
	
	
	public DatabaseHeader(InputStream is) throws IOException, ProblemReadingDatabaseFile {
		assemble(is);
	}
	
	
	public DatabaseHeader(String majorVersion, String minorVersion, String patchVersion) {
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.patchVersion = patchVersion;
	}
	
	
	public void flatPack(OutputStream os) throws IOException {
		os.write(flatPack(majorVersion));
		os.write(flatPack(minorVersion));
		os.write(flatPack(patchVersion));
	}

	
	private void assemble(InputStream is) throws IOException, ProblemReadingDatabaseFile {
		majorVersion = getString(is);
		minorVersion = getString(is);
		patchVersion = getString(is);
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


	public String getMajorVersion() {
		return majorVersion;
	}


	public void setMajorVersion(String majorVersion) {
		this.majorVersion = majorVersion;
	}


	public String getMinorVersion() {
		return minorVersion;
	}


	public void setMinorVersion(String minorVersion) {
		this.minorVersion = minorVersion;
	}


	public String getPatchVersion() {
		return patchVersion;
	}


	public void setPatchVersion(String patchVersion) {
		this.patchVersion = patchVersion;
	}

}
