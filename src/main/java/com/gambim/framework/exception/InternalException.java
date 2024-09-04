package com.gambim.framework.exception;

public class InternalException extends RuntimeException {

    public InternalException(String message) {
        super(message);
    }

    public InternalException(Exception e) {
        super(e);
    }
}
