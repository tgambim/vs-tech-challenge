package com.gambim.framework.exception;

import java.util.Map;

public class ValidationException extends ApiException {

    public ValidationException(String message, Map<String, String> validationErrors) {
        super(message, 422, validationErrors);
    }
}
