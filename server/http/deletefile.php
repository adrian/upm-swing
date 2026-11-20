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
 * During sync the client calls deletefile.php to remove the remote database
 * and then upload.php to store the new one. If the upload fails in between,
 * the remote database is lost; this version therefore saves a timestamped
 * backup before deleting, and keeps only the 10 most recent ones.
 *
 * Changes vs. original:
 *  - accepts POST only (the HTML form was an XSS/attack surface, including
 *    reflected XSS through $_SERVER['PHP_SELF'])
 *  - strict whitelist: ONLY *.db / *.upm files can be deleted. The original
 *    allowed deleting any other file in the folder (.htaccess, .htpasswd,
 *    backups, ...) which is the classic way to strip the protection and
 *    then upload a PHP web shell
 *  - automatic timestamped backup (name does not match the whitelist, so
 *    it cannot be deleted or overwritten through these scripts)
 */

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    die('METHOD_NOT_ALLOWED');
}

// Some error constants (client-visible plain text)
define("FILE_DOESNT_EXIST", "FILE_DOESNT_EXIST");
define("FILE_WASNT_DELETED", "FILE_WASNT_DELETED");
define("INVALID_FILENAME", "INVALID_FILENAME");
define("OK", "OK");

// --- configuration ----------------------------------------------------
$AllowedExt = array('db', 'upm'); // must match upload.php
$KeepBackups = 10;
// ----------------------------------------------------------------------

// Get the name of the file to delete
if (!isset($_POST['fileToDelete'])) {
    die(FILE_DOESNT_EXIST);
}

// Just take the filename so that someone can't put in a relative path
$fileToDeleteName = basename($_POST['fileToDelete']);

// Whitelist: only a database file can be deleted. This also makes the old
// "protect this script and upload.php" check unnecessary: .php, .ht*,
// backup files and everything else can never be deleted here.
$pattern = '/\A[A-Za-z0-9][A-Za-z0-9._-]*\.(' . implode('|', $AllowedExt) . ')\z/';
if ($fileToDeleteName === '' || !preg_match($pattern, $fileToDeleteName)) {
    die(INVALID_FILENAME);
}

$target = __DIR__ . '/' . $fileToDeleteName;

// Ensure the file exists
if (!file_exists($target)) {
    die(FILE_DOESNT_EXIST);
}

// Safety backup before deletion (sync deletes the remote db before
// uploading the new one; if the upload fails the old db would be gone)
$backup = $target . '.bak-' . date('Ymd-His');
if (!copy($target, $backup)) {
    // Refuse to delete if the backup could not be created
    die(FILE_WASNT_DELETED);
}

// Rotate: keep only the newest $KeepBackups backups for this database
$backups = glob($target . '.bak-*');
if (is_array($backups) && count($backups) > $KeepBackups) {
    sort($backups); // timestamp-based names sort chronologically
    foreach (array_slice($backups, 0, count($backups) - $KeepBackups) as $oldBackup) {
        @unlink($oldBackup);
    }
}

// Delete the file
if (unlink($target)) {
    echo OK;
} else {
    die(FILE_WASNT_DELETED);
}
