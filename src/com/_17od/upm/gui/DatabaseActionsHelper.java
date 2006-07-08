/*
 * $Id: DatabaseActions.java 107 2006-05-25 19:17:14Z adrianshome $
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
package com._17od.upm.gui;

import java.io.File;
import com._17od.upm.database.PasswordDatabase;


public class DatabaseActionsHelper {

    public static void replaceDatabase(PasswordDatabase existingDatabase, PasswordDatabase newDatabase) throws PasswordDatabaseException {
		// Delete the existing database and then copy the downloaded db into it's place
		String dbFileName = existingDatabase.getDatabaseFile().getAbsolutePath();
		boolean successful = existingDatabase.getDatabaseFile().delete();
		if (successful) {
			successful = newDatabase.getDatabaseFile().renameTo(new File(dbFileName));
			if (!successful) {
				throw new PasswordDatabaseException("Couldn't rename existing password database (to make way for the downloaded database) [" + existingDatabase.getDatabaseFile().getAbsolutePath() + "] to [" + existingDatabase.getDatabaseFile().getName() + ".tmp]");
			}
		} else {
			throw new PasswordDatabaseException("Couldn't delete the existing password database");
		}
    }
    
}
