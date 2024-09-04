package com.gambim.framework.exception;

import java.util.Map;

public class ApiException extends RuntimeException {

    private final Integer statusCode;
    private final Map<String, String> validationErrors;

    protected ApiException(String message, Integer statusCode, Map<String, String> validationErrors) {
        super(message);
        this.statusCode = statusCode;
        this.validationErrors = validationErrors;
    }

    protected ApiException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.validationErrors = Map.of();
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }
}
