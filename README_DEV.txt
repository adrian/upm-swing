Preparing Build Environment
===========================
1. Remote Database Location
   In order to test the HTTPTransport class you'll need a remote location
   configured that has the UPM upload.php and delete.php files. The
   easiest thing to do is,
      i) install Apache
     ii) install the Apache PHP module
    iii) configure a new directory that points to the 'server/http/'
         directory that comes with UPM, e.g.

            Alias /upm/ "/home/adrian/dev/UPM/server/http/"

            <Directory "/home/adrian/dev/UPM/server/http/">
                Order allow,deny
                Allow from all
                Options Indexes MultiViews
                AllowOverride All
            </Directory>

     iv) Configure the PHP upload setting in the PHP ini file. Mine is
         '/etc/php5/apache2/php.ini' and the setting to change is,
         'upload_tmp_dir = /home/adrian/dev/UPM/server/http'
      v) Ensure the APache user has write access to this directory.
         Something like this will do,
         'chmod a+w /home/adrian/dev/UPM/server/http'


Problem Running JUnit Ant Task Within Eclipse
==============================================
The Ant plugin that comes with Eclipse doesn't have the dependant jar file,
junit.jar.

To fix this I...
 1. copied junit.jar into C:\eclipse\plugins\org.apache.ant_1.6.5\lib
 2. Go to Windows -> Preferences -> Ant -> Runtime
 3. In the "Ant Home Entries" list hit "Add External Jar" and select the
    junit.jar file just referenced


Converting a Unicode Big Endian File to an Acceptable ResourceBundle Format
===========================================================================
Resource bundles must represent unicode characters using the \uxxxx format.

If you have a big endian unicode file you can convert it to this format using the command...
native2ascii -encoding UTF-16BE upm_cs.properties.biged > upm_cs.properties

native2ascii is a tool that comes with the Java JDK.
