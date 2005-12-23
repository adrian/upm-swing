package com._17od.upm.invest;

import java.io.File;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;


public class UploadFile {

    public static void main(String[] args) throws Exception {

        HttpClient client = new HttpClient();
        client.getHostConfiguration().setProxy("www-proxy.msc.ie", 8080);

        PostMethod post = new PostMethod("http://www.17od.com/files.php");

        try {
            Part[] parts = {
                    new FilePart("userfile", new File("C:/Documents and Settings/smitha/My Documents/bookmarks.html")),
                    new StringPart("MAX_FILE_SIZE", "999999999")
            };
            post.setRequestEntity(
                    new MultipartRequestEntity(parts, post.getParams())
            );

            int status = client.executeMethod(post);
            if (status == HttpStatus.SC_OK) {
                String responseBody = post.getResponseBodyAsString();
                if (!responseBody.equals("")) {
                    System.err.println(responseBody);
                } else {
                    System.out.println("Happy Days");
                }
            } else {
                HttpStatus.getStatusText(status);
            }
        } finally {
            post.releaseConnection();
        }

    }
    
}
