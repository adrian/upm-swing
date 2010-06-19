package com._17od.upm.database;

public class ExportException extends Exception {

    public ExportException(String message) {
        super(message);
    }

    public ExportException(Exception e) {
        super(e);
    }

}
