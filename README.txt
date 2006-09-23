--------------------------
Universal Password Manager
--------------------------
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


--------
Features
--------
.Small, fast and lean
.Uses AES for database encryption (PBEWithSHA256And256BitAES-CBC-BC)
.Shared password database
.Written in Java/SWING
.Windows and Mac OS X native feeling versions available
.Fast account searching
.Streamlined for those who are more comfortable using the keyboard only


----------------
Database Sharing
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


-------
Roadmap
-------
.Password generator
.Native Linux distributions (RPM, DEB)
.Internationalisation
.Commandline interface


-------
History
-------
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

