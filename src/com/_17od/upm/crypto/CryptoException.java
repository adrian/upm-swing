package com._17od.upm.crypto;

/**
 * A general purpose wrapper exception to use for all crypto related exceptions  
 */
public class CryptoException extends Exception {

    public CryptoException(Exception e) {
        super(e);
    }

}
