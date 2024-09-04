package com.gambim.service;

import static java.util.Optional.ofNullable;

import com.gambim.framework.annotation.Component;
import com.gambim.framework.security.AuthenticationService;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.util.Base64;

@Component
public class BasicAuthenticationService implements AuthenticationService {
    private static final String USERNAME = "vs_tech_challenge";
    private static final String PASSWORD = "SuperSecurePassword123@";

    @Override
    public boolean isAuthenticated(HttpExchange httpRequest) {
        String expectedToken = createBasicToken(USERNAME, PASSWORD);
        String actualToken = ofNullable(httpRequest.getRequestHeaders())
                .map(this::getTokenFromHeader)
                .orElse("");
        return expectedToken.equals(actualToken);
    }

    private String getTokenFromHeader(Headers requestHeaders) {
        return ofNullable(requestHeaders.get("Authorization"))
                .flatMap(headers -> headers.stream().findFirst())
                .map(header -> header.substring(6))
                .orElse("");
    }

    private String createBasicToken(String username, String password) {
        String rawToken = String.format("%s:%s", username, password);
        return Base64.getEncoder().encodeToString(rawToken.getBytes());
    }
}
