<?php
require 'HTTP/Upload.php';

$upload = new HTTP_Upload('en');       // Language for error messages
$file = $upload->getFiles('userfile'); // return a file object or error
if ($file->isError()) {
    die ($file->getMessage());
}

// Check if the file is a valid upload
if ($file->isValid()) {
    // this method will return the name of the file you moved,
    // useful for example to save the name in a database
    if (file_exists('../upload/'.$file->getProp('name'))) {
        die ("File already exists");
    }
    $file_name = $file->moveTo('../upload/');
    if (PEAR::isError($file_name)) {
        die ($file_name->getMessage());
    }
}
?>

