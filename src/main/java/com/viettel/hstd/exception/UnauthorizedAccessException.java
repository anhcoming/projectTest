package com.viettel.hstd.exception;

public class UnauthorizedAccessException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 2L;

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
