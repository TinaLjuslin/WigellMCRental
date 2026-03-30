package com.ljuslin.wigellMCRental.exception;

public class DataConflictException extends RuntimeException {
    public DataConflictException(String txt) {
        super(txt);
    }
}
