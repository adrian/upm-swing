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
        define("FILE_DOESNT_EXIST", "FILE_DOESNT_EXIST");
        define("FILE_WASNT_DELETED", "FILE_WASNT_DELETED");
        define("OK", "OK");

        // Get the name of the file to delete
        $fileToDelete = $_POST['fileToDelete'];

        // Just take the filename so that someone can't put in a relative path
        $fileToDeleteName = basename($fileToDelete);
        
        // Ensure we don't delete this file or the upload file
        if ($fileToDeleteName != basename($_SERVER['PHP_SELF']) &&
            $fileToDeleteName != 'upload.php') {

            // Ensure the file exists
            if (!file_exists('./'.$fileToDeleteName)) {
                die(FILE_DOESNT_EXIST);
            }

            // Delete the file
            $result = unlink('./'.$fileToDeleteName);

            if ($result) {
                echo OK;
            } else {
                die(FILE_WASNT_DELETED);
            }
        } else {
            echo OK;
        }

    } else {

?>

    <html>
        <head><title>Delete File</title></head>
        <body>
            <form action="<?php echo $_SERVER['PHP_SELF'] ?>" method="POST">
                <input type="text" name="fileToDelete"/>
                <input type="submit" name="Delete"/>
            </form>
        </body>
    </html>
 
<?php
    }
?>
