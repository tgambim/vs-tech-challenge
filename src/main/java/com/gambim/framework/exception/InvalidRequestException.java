package com.gambim.framework.exception;

public class InvalidRequestException extends ApiException {

    public InvalidRequestException(String message) {
        super(message, 400);
    }
}
