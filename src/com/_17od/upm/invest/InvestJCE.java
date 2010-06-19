package com._17od.upm.invest;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.SecretKeyFactory;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import javax.crypto.CipherOutputStream;
import javax.crypto.CipherInputStream;
import javax.crypto.Mac;
import java.nio.charset.Charset;
import java.util.SortedMap;
import java.util.Iterator;


public class InvestJCE {

    public static void main(String[] args) throws GeneralSecurityException, IOException, Exception {
        /*String algorithm = "Blowfish";
        KeyGenerator kg = KeyGenerator.getInstance(algorithm);
        SecretKey skey = kg.generateKey();
        byte[] raw = skey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, algorithm);
        System.out.println(">" + new String(skey.getEncoded()) + "<");
        System.out.println(">" + new String(skey.getFormat()) + "<");*/

        streaming();
        //pbe();
        //mac();
    }


    public static void padding() throws Exception {
    
        char[] password = {
                't', 'h', 'i', 's', ' ', 'i', 's', ' ', 'a',
                ' ', 's', 'i', 'm', 'p', 'l', 'e', ' ', 'p',
                'a', 's', 's', 'w', 'o', 'r', 'd'};
        byte[] cleartext = "abcdefg".getBytes();
        
        String algorithm = "PBEWithMD5AndDES";
        
        PBEKeySpec pbeKeySpec;
        PBEParameterSpec pbeParamSpec;
        SecretKeyFactory keyFac;
        
        // Salt
        byte[] salt = {
            (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
            (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
        };

        // Iteration count
        int count = 20;

        // Create PBE parameter set
        pbeParamSpec = new PBEParameterSpec(salt, count);
        
        pbeKeySpec = new PBEKeySpec(password);
        keyFac = SecretKeyFactory.getInstance(algorithm);
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

        // Create PBE Cipher
        Cipher pbeCipher = Cipher.getInstance(algorithm);

        // Initialize PBE Cipher with key and parameters
        pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);

        //Now encrypt the data and write it to a file (all in one go) 
        FileOutputStream fos;
        CipherOutputStream cos;
        fos = new FileOutputStream("e:\\temp\\CipherText.txt");
        cos = new CipherOutputStream(fos, pbeCipher);
        cos.write(cleartext);
        cos.flush();
        cos.close();
        fos.flush();
        fos.close();
        
        //Now read the data back in
        pbeCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
        CipherInputStream cis;
        FileInputStream fis;
        fis = new FileInputStream("e:\\temp\\CipherText.txt");
        cis = new CipherInputStream(fis, pbeCipher);
        int i = 0;
        while (i != -1) {
            i = cis.read();
            if (i != -1) {
                System.out.print((char) i);
            }
        }
        //byte[] cleartext2 = new byte[cleartext.length]; 
        //cis.read(cleartext2);
        cis.close();
        fis.close();
        
    }
    
    
    public static void charSets() throws Exception {

        //This code loops through each available character set
        //and prints the number of bytes used to represent a
        //character using that characterset
        String s = new String("x");
        SortedMap charSets = Charset.availableCharsets();
        Iterator charSetNames = charSets.keySet().iterator();
        while (charSetNames.hasNext()) {
            String charSetName = (String) charSetNames.next();
            if (s.getBytes(charSetName).length > 1 ) {
                System.out.print("* ");
            }
            System.out.println(charSetName + ":" + s.getBytes(charSetName).length);
        }

    }
    
    
    public static void streaming() throws GeneralSecurityException, IOException {

        //This first section creates and initialises the Cipher
        char[] password = {
                't', 'h', 'i', 's', ' ', 'i', 's', ' ', 'a',
                ' ', 's', 'i', 'm', 'p', 'l', 'e', ' ', 'p',
                'a', 's', 's', 'w', 'o', 'r', 'd'};
        byte[] cleartext = "this is the plain text".getBytes();
        
        String algorithm = "PBEWithMD5AndDES";
        
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
        FileOutputStream fos = new FileOutputStream("c:\\temp\\fos");
        CipherOutputStream cos= new CipherOutputStream(fos, pbeCipher);
        cos.write(cleartext);
        cos.flush();
        cos.close();
        fos.flush();
        fos.close();
        
        //Now we read back in the encryped text using the CipherInputStream
        pbeCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
        FileInputStream fis = new FileInputStream("c:\\temp\\fos");
        CipherInputStream cis = new CipherInputStream(fis, pbeCipher);
        
        int padding = 8 - (cleartext.length % 8);
        byte[] cleartext2 = new byte[cleartext.length + padding]; 
        int j = cis.read(cleartext2);
        System.out.println("Total bytes read back in:" + j);

        /*byte[] cleartext2 = new byte[cleartext.length];
        int i; 
        for (i=0; i<cleartext.length; i++) {
            int j = cis.read();
            if (j != -1) {
                cleartext2[i] = (byte)j; 
            }
        }
        System.out.println("Total bytes read back in:" + i);*/
        
        /*int i = 0;
        int j = 0;
        while (i != -1) {
            i = cis.read();
            if (i != -1) {
                j++;
                System.out.print((char) i);
            }
        }
        System.out.println("\nTotal bytes:" + j);*/
        
        
        String before = new String(cleartext, "UTF-8");
        String after = new String(cleartext2, "UTF-8");
        
        if (!before.equals(after)) {
            System.out.println("Old:" + before);
            System.out.println("New:" + after);
        }

        cis.close();
        fis.close();
        
    }

    
    public static void pbe() throws GeneralSecurityException {

        char[] password = {
                't', 'h', 'i', 's', ' ', 'i', 's', ' ', 'a',
                ' ', 's', 'i', 'm', 'p', 'l', 'e', ' ', 'p',
                'a', 's', 's', 'w', 'o', 'r', 'd'};
        byte[] cleartext = "this is the text to encrypt".getBytes();
        
        String algorithm = "PBEWithMD5AndDES";
        
        PBEKeySpec pbeKeySpec;
        PBEParameterSpec pbeParamSpec;
        SecretKeyFactory keyFac;
        
        // Salt
        byte[] salt = {
            (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
            (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
        };

        // Iteration count
        int count = 20;

        // Create PBE parameter set
        pbeParamSpec = new PBEParameterSpec(salt, count);
        
        pbeKeySpec = new PBEKeySpec(password);
        keyFac = SecretKeyFactory.getInstance(algorithm);
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

        // Create PBE Cipher
        Cipher pbeCipher = Cipher.getInstance(algorithm);

        // Initialize PBE Cipher with key and parameters
        pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);

        // Encrypt the cleartext
        byte[] cipherText = pbeCipher.doFinal(cleartext);
     
        System.out.println(new String(cipherText));
    }
    
    public static void simple() throws GeneralSecurityException {
        
        String algorithm = "AES";
        
        //The KeyGenerator generates a key. Don't need this if I have a password since
        //the password is the key
        KeyGenerator kg = KeyGenerator.getInstance(algorithm);
        SecretKey skey = kg.generateKey();
        byte[] raw = skey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, algorithm);
        
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal("This is just an example".getBytes());
        System.out.println(new String(encrypted));

        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        System.out.println(new String(decrypted));

    }

    
    public static void mac() throws GeneralSecurityException {

        // Generate secret key for HMAC-MD5
        KeyGenerator kg = KeyGenerator.getInstance("HmacMD5");
        SecretKey sk = kg.generateKey();

        // Get instance of Mac object implementing HMAC-MD5, and 
        // initialize it with the above secret key
        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(sk);
        byte[] result = mac.doFinal("Hi There mr smith".getBytes());
        System.out.println(mac.getProvider());
        System.out.println(mac.getMacLength());
        System.out.println(new String(result));
        
    }    
}
