package com.viettel.hstd.exception;

public class NotFoundException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 3L;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, String detail) {
        super(message+ "&&&" + detail);
    }
}
