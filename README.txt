Universal Password Manager
--------------------------
http://www.17od.com/upm

Copyright (C) 2005-2006 Adrian Smith

Universal Password Manager is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

Universal Password Manager is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Universal Password Manager; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA


Contents
--------
1. Overview
2. Features
3. Internationalization
4. Roadmap
5. History

1. Overview
   --------
   Universal Password Manager (UPM) allows you to store usernames, passwords, URLs,
   and generic notes in an encrypted database protected by one master password.

   There are several open source password managers available so what makes UPM 
   different? It's three strongest features are...

    * it's simplicity - it provides a small number of very strong features with no
      clutter

    * cross platform - UPM is written in Java so it can run on Windows, Mac OS X &
      Linux (among others). Both Windows and Mac OS X native feeling version are
      available.

    * database sharing - This feature is particularly useful. Rather than having
      lots of seperate databases (at home and at work for example) this feature
      allows you store your database at a remote location (password protected HTTP
      URL for example) and then have UPM automatically keep your local database in
      sync with the remote database.

2. Features
   --------
   .Small, fast and lean
   .Uses AES for database encryption (PBEWithSHA256And256BitAES-CBC-BC)
   .Shared password database
   .Written in Java/SWING
   .Windows and Mac OS X native feeling versions available
   .Fast account searching
   .Streamlined for those who are more comfortable using the keyboard only

3. Database Sharing
   ----------------
   The database sharing feature was introduced in version 1.1. The reason it came
   about was because I was using UPM at home and at work and was finding it a real
   pain trying to keep the databases on both machines in sync. With database
   sharing UPM uses a remote copy of your database to keep all the local copies in
   sync. It does this by downloading the remote copy before you make local changes
   and by uploading your local copy after you've made changes.

      Server Side Steps
      ~~~~~~~~~~~~~~~~~
      To use database sharing you need to specify where you want your remote database
      stored. At present UPM only supports HTTP locations (both password protected by
      basic authentication and non password protected).

      To upload your database UPM comes with a PHP script (upload.php) that needs to
      be placed in the directory where you want to store the database. You'll find
      this script in the .\server\http directory of your UPM installation.

      Database Configuration
      ~~~~~~~~~~~~~~~~~~~~~~
      To configure your database with the remote location open your database and go
      to the Database -> Database Properties menu item. In the URL field enter the
      URL of your remote location. If you put the upload.php file at
      http://www.mydomain.com/securedir/upload.php then enter
      http://www.mydomain.com/securedir in the URL field. If this URL is password
      protected then select the account in your database that has the relevant
      username and password. When you click OK UPM will upload your database to this
      URL.

      Application Prompts
      ~~~~~~~~~~~~~~~~~~~
      When the text "Revision x - Unsynchronised" appears in red in the status bar
      this means that your local database and the remote database may by out of sync.
      To synchronise them press the Synchronise button or the menu item 
      Database -> Sync with Remote Database</i>. When the databases are in sync the
      status bar will read "Revision x - Synchronised".

   Once you've configured your database to use a remote copy UPM will download the
   remote database before you make any changes to your local copy. This ensures
   that any changes you made on another machine will be downloaded to your current
   machine so that they're not lost. When you exit UPM (via the 
   Database -> Exit menu item) UPM will upload your database if you've made any 
   local changes. Be aware that UPM will not upload your database if you exit the
   application any other way.


4. Internationalization
   --------------------
   As of version 1.4 UPM supports multiple languages. For each language a seperate
   resource bundle file is included. UPM 1.4 includes resource bundles for both 
   English and French. My apologies to all French speakers but you might find that
   the French messages are a bit weird. I used Google's translator tool and I have
   a feeling it didn't really work out too well. I'd really appreciate help from
   anyone willing to provide translations for UPM's messages. You can find the
   English resource bundle (upm.properties) in the upm.jar file that ships with
   UPM (Winzip, 7-Zip, or some other zip tool can be used to uncompress the JAR file).

5. Roadmap
   -------
   .Allow multipe edits on the local database between synchronizations
   .Native Linux distributions (RPM, DEB)
   .Password generator
   .Commandline interface


6. History
   -------
   31-Dec-2006 : Version 1.5
      - Added Czech and German translations (courtesy of Petr Ustohal)
      - Added username and password fields to the HTTP proxy options
      - The HTTP Proxy can be enabled/disabled using a checkbox while still
        retaining the settings for later use
      - In the account information dialog the password is masked by default.
        To view it you untick a checkbox. Thanks to Jelle De Pot for suggesting 
        this feature
      - A few small bugfixes here and there


   27-Nov-2006 : Version 1.4
      - Added support for internationalisation. English and French language bundles
        are included.
      - Added support for editing the account name
      - Double clicking or hitting enter on an account opens the account in 
        read-only view mode. Edit it still available on the toolbar, menu bar and
        as a keyboard shortcut.


   23-Sep-2006 : Version 1.3
      The primary purpose of this release is the introduction of AES database encryption.

      Changes
      - Use the BouncyCastle AES provider to encrypt the database (PBEWithSHA256And256BitAES-CBC-BC)
      - Moved the database structural version out of the encrypted portion of the database.
        This means that UPM can use the database version to decide how to decrypt the database.

   
   09-Sep-2006 : Version 1.2
      This is primarily a bug fix release.

      Bugs Fixed
      [1551461] - Database sync problem on Mac

      Other Changes
      - Tidy up the HTTP proxy code in the HTTPTransport class
      - Don't prompt the user for the password on each database sync
      - Don't bother showing a "success" dialog on each database sync
      - Don't bother asking the user to sync database on application exit
      - Change the status bar message for local databases
      - When user edits and account check if it has changed before marking the database as dirty


   22-Sep-2006 : Version 1.1
      This release has one major new feature, database sharing. This feature allows you to have one database that can be accessed from many machines.


   21-Dec-2005 : Version 1.0
      This is first stable release of UPM. It's a bugfix release only and has no functional differences to version 1.0b1. Databases created with 1.0b1 are fully compatible with 1.0.

      Bugs Fixed
      [1375407] - Possible to change account name to existing account name
      [1375397] - Possible to add duplicate account when account name filtered
      [1375390] - Incorrect items listed after an add when list is filtered
      [1375385] - Error deleting last account in the listview
      [1374280] - Wrong URL on About page


   5-Dec-2005 : Version 1.0b1
      First release.

$Id$

