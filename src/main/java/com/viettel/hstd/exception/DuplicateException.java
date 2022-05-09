package com.viettel.hstd.exception;

public class DuplicateException extends RuntimeException {

    private static final long serialVersionUID = 19L;

    public DuplicateException(String message) {
        super(message);
    }
}
