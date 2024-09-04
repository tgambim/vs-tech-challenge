package com.gambim.framework.routing;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(String message, LocalDateTime timestamp, Map<String, String> validationErrors) {
    public ErrorResponse(String message) {
        this(message, LocalDateTime.now(), Map.of());
    }

    public ErrorResponse(String message, Map<String, String> validationErrors) {
        this(message, LocalDateTime.now(), validationErrors);
    }
}
