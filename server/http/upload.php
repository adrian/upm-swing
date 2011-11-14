<?php
/*
 * Universal Password Manager
 * Copyright (C) 2005-2011 Adrian Smith
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

    if ($_SERVER['REQUEST_METHOD'] == 'POST') {

        // Some error constants
        define("FILE_ALREADY_EXISTS", "FILE_ALREADY_EXISTS");
        define("FILE_WASNT_MOVED", "FILE_WASNT_MOVED");
        define("FILE_WASNT_UPLOADED", "FILE_WASNT_UPLOADED");
        define("OK", "OK");

        // First of all check that the file was uploaded successfully
        if (is_uploaded_file($_FILES['userfile']['tmp_name'])) {

            // Get the name of the file that was uploaded
            $uploadedFileName = basename($_FILES['userfile']['name']);

            // If the file already exists throw an error
            if (file_exists('./'.$uploadedFileName)) {
                die(FILE_ALREADY_EXISTS);
            } else {
                move_uploaded_file($_FILES['userfile']['tmp_name'], "./$uploadedFileName");
                
                // Check to ensure the file was uploaded
                if (!file_exists('./'.$uploadedFileName)) {
                    die(FILE_WASNT_MOVED);
                }
                
                // Set the correct permissions on the file
                chmod('./'.$uploadedFileName, 0600);

                // Looks like the upload was successful so return a success message
                echo OK;
            }

        } else {
            die(FILE_WASNT_UPLOADED);
        }

    } else {

?>

    <html>
        <head><title>Upload File</title></head>
        <body>
            <form enctype="multipart/form-data" action="<?php echo $_SERVER['PHP_SELF'] ?>" method="POST">
                <input type="file" name="userfile"/>
                <input type="submit" name="Upload"/>
            </form>
        </body>
    </html>

<?php
    }
?>
