package com.viettel.hstd.exception;

public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 11L;

    public BadRequestException(String message) {
        super(message);
    }
}
