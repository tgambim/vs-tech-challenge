package unit.framework;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gambim.framework.exception.*;
import com.gambim.framework.routing.ApiRouter;
import com.gambim.framework.routing.ErrorResponse;
import com.gambim.framework.routing.RequestHandler;
import com.gambim.framework.routing.Response;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class RequestHandlerTest {

    ApiRouter mockedApiRouter = mock(ApiRouter.class);
    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    HttpExchange mockedHttpExchange = mock(HttpExchange.class);
    RequestHandler requestHandler = new RequestHandler(mockedApiRouter, objectMapper);

    @ParameterizedTest
    @CsvSource({"{},\"{}\"", ",''"})
    void testHandle(String routerReturn, String expectedResponse) throws IOException {
        Headers headers = new Headers();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        when(mockedApiRouter.getRequestHandler(mockedHttpExchange))
                .thenReturn((HttpExchange httpExchange) -> new Response(routerReturn, 200));
        when(mockedHttpExchange.getResponseHeaders()).thenReturn(headers);
        when(mockedHttpExchange.getResponseBody()).thenReturn(byteArrayOutputStream);

        assertDoesNotThrow(() -> requestHandler.handle(mockedHttpExchange));

        String responseString = byteArrayOutputStream.toString();
        assertEquals(expectedResponse, responseString);
        if (routerReturn != null) {
            assertTrue(headers.containsKey("Content-Type"));
            assertTrue(headers.get("Content-Type").contains("application/json"));
        } else {
            assertFalse(headers.containsKey("Content-Type"));
        }
        verify(mockedHttpExchange, times(1)).sendResponseHeaders(200, responseString.length());
    }

    @ParameterizedTest
    @MethodSource("exceptionArguments")
    void testHandleWithApiException(ApiException exception) throws IOException {
        Headers headers = new Headers();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        when(mockedApiRouter.getRequestHandler(mockedHttpExchange)).thenThrow(exception);
        when(mockedHttpExchange.getResponseHeaders()).thenReturn(headers);
        when(mockedHttpExchange.getResponseBody()).thenReturn(byteArrayOutputStream);

        assertDoesNotThrow(() -> requestHandler.handle(mockedHttpExchange));

        String responseString = byteArrayOutputStream.toString();
        ErrorResponse resultErrorResponse =
                assertDoesNotThrow(() -> objectMapper.readValue(responseString, ErrorResponse.class));
        assertEquals(exception.getValidationErrors(), resultErrorResponse.validationErrors());
        assertEquals(exception.getMessage(), resultErrorResponse.message());
        assertTrue(headers.containsKey("Content-Type"));
        assertTrue(headers.get("Content-Type").contains("application/json"));
        verify(mockedHttpExchange, times(1)).sendResponseHeaders(exception.getStatusCode(), responseString.length());
    }

    static Stream<ApiException> exceptionArguments() {
        return Stream.of(
                new ValidationException("error message", Map.of("field", "error")),
                new InvalidRequestException("Error parsing request body"),
                new AuthenticationException(),
                new NotFoundException("Not found."));
    }

    @Test
    void testHandleWithApiException() throws IOException {
        Headers headers = new Headers();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        when(mockedApiRouter.getRequestHandler(mockedHttpExchange)).thenThrow(new InternalException("generic error"));
        when(mockedHttpExchange.getResponseHeaders()).thenReturn(headers);
        when(mockedHttpExchange.getResponseBody()).thenReturn(byteArrayOutputStream);

        assertDoesNotThrow(() -> requestHandler.handle(mockedHttpExchange));

        String responseString = byteArrayOutputStream.toString();
        ErrorResponse resultErrorResponse =
                assertDoesNotThrow(() -> objectMapper.readValue(responseString, ErrorResponse.class));
        assertTrue(resultErrorResponse.validationErrors().isEmpty());
        assertEquals("Internal server error.", resultErrorResponse.message());
        assertTrue(headers.containsKey("Content-Type"));
        assertTrue(headers.get("Content-Type").contains("application/json"));
        verify(mockedHttpExchange, times(1)).sendResponseHeaders(500, responseString.length());
    }

    @Test
    void testHandleWithIoException() throws IOException {
        Headers headers = new Headers();
        OutputStream mockedOutputStream = mock(OutputStream.class);
        doThrow(new IOException()).when(mockedOutputStream).write(any());
        when(mockedApiRouter.getRequestHandler(mockedHttpExchange))
                .thenReturn((HttpExchange httpExchange) -> new Response(null, 200));
        when(mockedHttpExchange.getResponseHeaders()).thenReturn(headers);
        when(mockedHttpExchange.getResponseBody()).thenReturn(mockedOutputStream);

        assertThrows(IOException.class, () -> requestHandler.handle(mockedHttpExchange));
    }
}
