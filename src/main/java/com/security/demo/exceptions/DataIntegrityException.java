package com.security.demo.exceptions;

public class DataIntegrityException extends Exception {
    private static final long serialVersionUID = 1L;

    public DataIntegrityException(String message) {
        super(message);
    }
}
