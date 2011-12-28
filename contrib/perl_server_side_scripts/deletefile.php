#!/usr/bin/perl

use CGI;
use File::Basename;

$userfile_param_name = 'fileToDelete';
# Some error constants
$msg{'FILE_DOESNT_EXIST'} = "FILE_DOESNT_EXIST";
$msg{'FILE_WASNT_DELETED'} = "FILE_WASNT_DELETED";
$msg{'OK'} = "OK";

$q = CGI->new;

if ($ENV{'REQUEST_METHOD'} eq 'POST') {
	# Just take the filename so that someone can't put in a relative path
	$fileToDeleteName = basename($q->param($userfile_param_name));

	# Ensure we don't delete this file or the upload file
	if ($fileToDeleteName ne basename($ENV{'SCRIPT_NAME'}) &&
	   $fileToDeleteName ne 'upload.php') {

	   # Ensure the file exists
	   if (! -e $fileToDeleteName) {
	       advise($msg{'FILE_DOESNT_EXIST'});
	   }

	   # Delete the file
	   if (unlink($fileToDeleteName)) {
	       advise($msg{'OK'});
	   } else {
	       advise($msg{'FILE_WASNT_DELETED'});
	   }
	} else {
	   advise($msg{'OK'});
	}
} else {
	print $q->header(),
         $q->start_html('Delete File'),
			$q->start_form(-method=>'POST',
		    -action=>$ENV{'SCRIPT_NAME'}),
			$q->textfield(-name=>'fileToDelete'),
			$q->submit(-name=>'Delete'),
			$q->end_form(),
         $q->end_html();
}

sub advise {
	print "Content-type: text/plain\n\n";
	print @_;
	exit;
}
