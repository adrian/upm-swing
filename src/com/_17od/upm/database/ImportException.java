package com._17od.upm.database;

public class ImportException extends Exception {

    public ImportException(String message) {
        super(message);
    }

    public ImportException(Exception e) {
        super(e);
    }

}
