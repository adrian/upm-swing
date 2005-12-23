<?php
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
require 'HTTP/Upload.php';

// Some error constants
define("GENERAL_ERROR", "GENERAL_ERROR");
define("FILE_ALREADY_EXISTS", "FILE_ALREADY_EXISTS");
define("FILE_WASNT_MOVED", "FILE_WASNT_MOVED");
define("FILE_WASNT_UPLOADED", "FILE_WASNT_UPLOADED");
define("OK", "OK");

// Get a reference to the uploaded file
$upload = new HTTP_Upload('en');
$file = $upload->getFiles('userfile');
if ($file->isError()) {
    die (GENERAL_ERROR . " - " . $file->getMessage());
}

// Check if the file is a valid upload
if ($file->isValid()) {

	// If the file already exists throw an error
    if (file_exists('../upload/'.$file->getProp('name'))) {
        die (FILE_ALREADY_EXISTS);
    }
    
    // Move the file
    $file_name = $file->moveTo('../upload/');
    
    // If there was an error or the uploaded file doesn't exist then throw an error
    if (PEAR::isError($file_name)) {
	    die (GENERAL_ERROR . " - " . $file->getMessage());
    }
    if (!file_exists('../upload/'.$file->getProp('name'))) {
        die (FILE_WASNT_MOVED);
    }

	// Looks like the upload was successful so return a success message
    echo OK;
    
} else {
	die (FILE_WASNT_UPLOADED);
}

?>
