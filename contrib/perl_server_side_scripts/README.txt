These Perl scripts were kindly donated by a UPM user. They can be used on the server side in place of the PHP scripts.

deletefile.php and upload.php are perl scripts. The reason they have a .php extension is because this is what UPM expects.

The author pointed out that,
"CGI.pm has a bug that prevents processing of multipart form submissions from Android Java machine. Apparently, Android Java uses LF as line separator in multipart submissions instead of CRLF. This LF-only chokes CGI.pm. I fixed the bug and notified the person who maintains CGI.pm at cpan.org. Hopefully, the next public release will incorporate the bug fix. In the interim, I am attaching the latest unmodified version of CGI.pm and the one with the bugfix so that you can compare the documents and see the modified lines of code if you are interested."

