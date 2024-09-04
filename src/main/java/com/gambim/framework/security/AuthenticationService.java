package com.gambim.framework.security;

import com.sun.net.httpserver.HttpExchange;

public interface AuthenticationService {

    boolean isAuthenticated(HttpExchange httpRequest);
}
