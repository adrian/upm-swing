package com._17od.upm.invest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class EncryptionTest {

    public static void main(String[] args) throws Exception {

        String algorithm = "PBEWithMD5AndDES";
        String fileName = "c:\\temp\\fos";
        
        //This first section creates and initialises the Cipher
        char[] password = {'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};
        byte[] cleartext = "this is the plain text".getBytes();
    
        PBEKeySpec pbeKeySpec;
        PBEParameterSpec pbeParamSpec;
        SecretKeyFactory keyFac;
        
        byte[] salt = {
                (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
                (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
        };

        int count = 20;

        pbeParamSpec = new PBEParameterSpec(salt, count);
        
        pbeKeySpec = new PBEKeySpec(password);
        keyFac = SecretKeyFactory.getInstance(algorithm);
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

        Cipher pbeCipher = Cipher.getInstance(algorithm);

        pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);

        //This next section uses the Cipher just initialised and a CipherOutputStream
        //to encrypt and write some plaintext to a file
        FileOutputStream fos = new FileOutputStream(fileName);
        CipherOutputStream cos = new CipherOutputStream(fos, pbeCipher);
        cos.write(cleartext);
        cos.close();
        fos.close();
        
        //Now we read back in the encryped text using the CipherInputStream
        pbeCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
        FileInputStream fis = new FileInputStream(fileName);
        CipherInputStream cis = new CipherInputStream(fis, pbeCipher);
        
        //Attempt to read in the number of bytes in the original cleartext in one go -- THIS WORKS
        /*byte[] unencryptedBytes = new byte[cleartext.length]; 
        int bytesRead = cis.read(unencryptedBytes);
        System.out.println("Total bytes read back in:" + bytesRead);*/
        
        //Read in the number of bytes in the original cleartext one by one
        byte[] unencryptedBytes = new byte[cleartext.length]; 
        int i; 
        for (i=0; i<cleartext.length; i++) {
            int j = cis.read();
            if (j != -1) {
                unencryptedBytes[i] = (byte)j; 
            }
        }
        System.out.println("Total bytes read back in:" + i);

        String before = new String(cleartext, "UTF-8");
        String after = new String(unencryptedBytes, "UTF-8");
        
        System.out.println("Old:" + before);
        System.out.println("New:" + after);
        
        cis.close();
        fis.close();
        
    }
    
}