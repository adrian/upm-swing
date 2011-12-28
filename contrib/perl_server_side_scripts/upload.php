#!/usr/bin/perl

use CGI;
use File::Basename;
use Storable;

$userfile_param_name = 'userfile';
# Some error constants
$msg{'FILE_ALREADY_EXISTS'} = "FILE_ALREADY_EXISTS";
$msg{'FILE_WASNT_UPLOADED'} = "FILE_WASNT_UPLOADED";
$msg{'OK'} = "OK";

$q = CGI->new;

#if (defined $q) {
#    store \$q, 'cgiobj.txt';
#} else {
#    print "Content-type: text/plain\n\n";
#    print "UNDEFINED_OBJECT\n";
#    exit;
    #`echo 'undefined object' > cgiobj.txt`;
#}

if ($ENV{'REQUEST_METHOD'} eq 'POST') {
	# First of all check that the file was uploaded successfully
	$fh = $q->upload($userfile_param_name);
	if (defined $fh) {
		# Get the name of the file that was uploaded
		my $uploadedFileName = basename($q->param($userfile_param_name));

		# If the file already exists throw an error
		if (-e $uploadedFileName) {
			advise($msg{'FILE_ALREADY_EXISTS'});
		} else {
			open (UFILE, ">$uploadedFileName")
				|| advise("Cannot open file \"$q->param($userfile_param_name)\" for writing!\n");
			binmode UFILE;
			print UFILE while <$fh>;
			close UFILE;
			# Set the correct permissions on the file
			chmod 0644, $uploadedFileName;
			advise($msg{'OK'});
		}
	} else {
		advise($msg{'FILE_WASNT_UPLOADED'});
	}
} else {
	print $q->header(),
         $q->start_html('Upload File'),
			$q->start_form(-method=>'POST',
		    -action=>$ENV{'SCRIPT_NAME'},
		    -enctype=>'multipart/form-data'),
			$q->filefield(-name=>'userfile'),
			$q->submit(-name=>'Upload'),
			$q->end_form(),
         $q->end_html();
}

sub advise {
	print "Content-type: text/plain\n\n";
	print @_;
	exit;
#	print $q->header(),
#			$q->start_html(@_),
#			@_,
#			$q->end_html();
#	exit;
}

