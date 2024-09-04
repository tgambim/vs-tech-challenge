package com.gambim.framework.exception;

public class AuthenticationException extends ApiException {

    public AuthenticationException() {
        super("Unauthenticated.", 401);
    }
}
