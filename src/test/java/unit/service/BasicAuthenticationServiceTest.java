package unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gambim.service.BasicAuthenticationService;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.util.List;
import org.junit.jupiter.api.Test;

class BasicAuthenticationServiceTest {

    HttpExchange mockedHttpExchange = mock(HttpExchange.class);
    BasicAuthenticationService basicAuthenticationService = new BasicAuthenticationService();

    @Test
    void testAuthenticate() {
        Headers headers = new Headers();
        headers.put("Authorization", List.of("Basic dnNfdGVjaF9jaGFsbGVuZ2U6U3VwZXJTZWN1cmVQYXNzd29yZDEyM0A="));
        when(mockedHttpExchange.getRequestHeaders()).thenReturn(headers);

        boolean authenticateResult =
                assertDoesNotThrow(() -> basicAuthenticationService.isAuthenticated(mockedHttpExchange));

        assertTrue(authenticateResult);
    }

    @Test
    void testAuthenticateWithInvalidToken() {
        Headers headers = new Headers();
        headers.put("Authorization", List.of("Basic invalid"));
        when(mockedHttpExchange.getRequestHeaders()).thenReturn(headers);

        boolean authenticateResult =
                assertDoesNotThrow(() -> basicAuthenticationService.isAuthenticated(mockedHttpExchange));

        assertFalse(authenticateResult);
    }

    @Test
    void testAuthenticateWithoutAuthenticationHeader() {
        Headers headers = new Headers();
        headers.put("RandomHeader", List.of(""));
        when(mockedHttpExchange.getRequestHeaders()).thenReturn(headers);

        boolean authenticateResult =
                assertDoesNotThrow(() -> basicAuthenticationService.isAuthenticated(mockedHttpExchange));

        assertFalse(authenticateResult);
    }

    @Test
    void testAuthenticateWithoutHeaders() {
        when(mockedHttpExchange.getRequestHeaders()).thenReturn(null);

        boolean authenticateResult =
                assertDoesNotThrow(() -> basicAuthenticationService.isAuthenticated(mockedHttpExchange));

        assertFalse(authenticateResult);
    }
}
