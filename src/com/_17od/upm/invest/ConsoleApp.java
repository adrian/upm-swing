package com._17od.upm.invest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import sun.io.Converters;


public class ConsoleApp {

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        System.out.println(Converters.getDefaultEncodingName());
        String s = new String("Hello World");
        printBytesWithEncofing(s.getBytes(), "UTF-8");
    }

    
    private static void printBytes(byte[] bytes) {
        for (int i=0; i<bytes.length; i++) {
            System.out.println("Position " + i + ':' + (int) bytes[i]);
        }
    }
    
    
    private static void printBytesWithEncofing(byte[] bytes, String encoding) throws IOException {
        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(bytes), encoding);
        int nextChar;
        int i = 0;
        do {
            nextChar = isr.read();
            if (nextChar != -1) {
                System.out.println("Position " + i + ':' + nextChar);
            }
            i++;
        } while(nextChar != -1);
    }
}
