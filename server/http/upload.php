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

/*
 * Hardened version - protocol compatible with the unmodified UPM Java client.
 *
 * The client compares the response body to fixed strings, so the only
 * accepted success answer is exactly "OK" (no extra whitespace/newlines).
 * Any other plain-text body is shown to the user as a sync error, so the
 * extra messages below (INVALID_FILENAME, FILE_TOO_LARGE) are safe.
 *
 * Changes vs. original:
 *  - accepts POST only (the HTML form was an XSS/attack surface)
 *  - strict whitelist of uploaded filenames: only *.db / *.upm, no path
 *    traversal, no ".php" web-shell upload
 *  - full $_FILES error validation and size limit
 *  - chmod 0640 instead of world-readable 0644
 *  - no closing ?> tag: avoids accidental trailing output that would
 *    break the client's "OK" comparison
 */

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    die('METHOD_NOT_ALLOWED');
}

// Some error constants (client-visible plain text)
define("FILE_ALREADY_EXISTS", "FILE_ALREADY_EXISTS");
define("FILE_WASNT_MOVED", "FILE_WASNT_MOVED");
define("FILE_WASNT_UPLOADED", "FILE_WASNT_UPLOADED");
define("FILE_TOO_LARGE", "FILE_TOO_LARGE");
define("INVALID_FILENAME", "INVALID_FILENAME");
define("OK", "OK");

// --- configuration ----------------------------------------------------
$MaxUploadBytes = 2 * 1024 * 1024;   // 2 MB: plenty for a UPM database
$AllowedExt     = array('db', 'upm'); // adjust if your database uses another extension
// ----------------------------------------------------------------------

// The client sends the database as multipart field "userfile"
if (!isset($_FILES['userfile']) || !isset($_FILES['userfile']['error'])) {
    die(FILE_WASNT_UPLOADED);
}
if ($_FILES['userfile']['error'] !== UPLOAD_ERR_OK) {
    die(FILE_WASNT_UPLOADED);
}

// Get the name of the file that was uploaded
$uploadedFileName = basename($_FILES['userfile']['name']);

// Whitelist: a plain database name only. This blocks uploads of shell.php,
// .htaccess, .htpasswd, hidden files, traversal attempts, etc.
$pattern = '/\A[A-Za-z0-9][A-Za-z0-9._-]*\.(' . implode('|', $AllowedExt) . ')\z/';
if ($uploadedFileName === '' || !preg_match($pattern, $uploadedFileName)) {
    die(INVALID_FILENAME);
}

// Refuse empty uploads and oversized ones (disk-DoS)
if (!isset($_FILES['userfile']['size']) ||
    $_FILES['userfile']['size'] <= 0 ||
    $_FILES['userfile']['size'] > $MaxUploadBytes) {
    die(FILE_TOO_LARGE);
}

// The file must be a real HTTP upload, not something already on disk
if (!is_uploaded_file($_FILES['userfile']['tmp_name'])) {
    die(FILE_WASNT_UPLOADED);
}

$target = __DIR__ . '/' . $uploadedFileName;

// If the file already exists throw an error
if (file_exists($target)) {
    die(FILE_ALREADY_EXISTS);
}

if (!move_uploaded_file($_FILES['userfile']['tmp_name'], $target)) {
    // The client automatically retries once on this exact message
    die(FILE_WASNT_MOVED);
}

// Check to ensure the file was uploaded
if (!file_exists($target)) {
    die(FILE_WASNT_MOVED);
}

// Owner read/write, group read; not world-readable
@chmod($target, 0640);

// Looks like the upload was successful so return a success message
echo OK;
