--------------------------------------------------------------------------------
-- Packaging and uploading to Launchpad PPA
--------------------------------------------------------------------------------

These instructions assume the upstream source tarball has been created (ant package-src -Dversion=[version])

Required packages,
 * javahelper

1. Create a working directory,

    $ mkdir /tmp/upm-swing-deb-package

2. Copy the upstream source tarball into the working directory,

    $ cp upm-src-<version>.tar.gz /tmp/upm-swing-deb-package

3. Use jh_repack to clean up the upstream tarball (remove jars),

    $ jh_repack --upstream-version <version> upm-src-<version>.tar.gz

4. Rename the tarball to what what the Debian packaging tools expect,

    $ mv upm-src-<version>.tar.gz upm_<version>.orig.tar.gz

5. Unpack the upstream tarball,

    $ tar -xzvf upm_<version>.orig.tar.gz

6. cd into the source directory,

    $ cd upm-src-<version>

7. Update the changelog (using dch)

    $ export DEBFULLNAME="Adrian Smith"
    $ export DEBEMAIL="adrian@17od.com"
    $ dch -v <version>  # e.g. 1.12-1~ppa1
   
8. Build the source tarball and sign,

    $ debuild -S -sa -k<key ID>

    Use gpg --list-secret-keys to get the key ID. Look for a line like "sec 12345/12ABCDEF"; the part after the slash is the key ID.

9. Upload the PPA to Launchpad,

    $ dput ppa:adriansmith/upm ../upm_<version>_source.changes


--------------------------------------------------------------------------------
-- Creating Patches for Debian build
--------------------------------------------------------------------------------

1. quilt new <name>.patch

2. quilt add <file to modify>

3. (make changes to files)

4. quilt refresh

5. quilt header -e


--------------------------------------------------------------------------------
-- Preparing Build Environment
--------------------------------------------------------------------------------

In order to test the HTTPTransport class you'll need a remote location 
configured that has the UPM upload.php and delete.php files. The easiest thing
to do is,

  i) install Apache

 ii) install the Apache PHP module

iii) configure a new directory that points to the 'server/http/' directory that
     comes with UPM, e.g.

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

  v) Ensure the Apache user has write access to this directory.
     Something like this will do,

         chmod a+w /home/adrian/dev/UPM/server/http


--------------------------------------------------------------------------------
-- Problem Running JUnit Ant Task Within Eclipse ==
--------------------------------------------------------------------------------
The Ant plugin that comes with Eclipse doesn't have the dependant jar file,
junit.jar.

To fix this I...
 1. copied junit.jar into C:\eclipse\plugins\org.apache.ant_1.6.5\lib
 2. Go to Windows -> Preferences -> Ant -> Runtime
 3. In the "Ant Home Entries" list hit "Add External Jar" and select the
    junit.jar file just referenced


--------------------------------------------------------------------------------
-- Converting a Unicode Big Endian File to an Acceptable ResourceBundle Format
--------------------------------------------------------------------------------
Resource bundles must represent unicode characters using the \uxxxx format.

If you have a big endian unicode file you can convert it to this format using the command...
native2ascii -encoding UTF-16BE upm_cs.properties.biged > upm_cs.properties

native2ascii is a tool that comes with the Java JDK.
