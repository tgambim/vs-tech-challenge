package unit.framework;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gambim.framework.exception.*;
import com.gambim.framework.routing.ApiRouter;
import com.gambim.framework.routing.Response;
import com.gambim.framework.security.AuthenticationService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsExchange;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import util.InvalidTestController;
import util.testClasses.TestController;

class ApiRouterTest {
    static ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    static AuthenticationService mockedAuthenticationService = mock(AuthenticationService.class);
    static ApiRouter apiRouter = new ApiRouter(objectMapper, mockedAuthenticationService);
    HttpExchange httpExchange = mock(HttpsExchange.class);

    @BeforeAll
    static void beforeAll() {
        apiRouter.registerRoutes(List.of(new TestController()));
    }

    @Test
    void testGetRequestHandlerWithBasePath() throws URISyntaxException {
        when(httpExchange.getRequestMethod()).thenReturn("GET");
        when(httpExchange.getRequestURI()).thenReturn(new URI("http://localhost:8000/test"));

        Function<HttpExchange, Response> resultHandler =
                assertDoesNotThrow(() -> apiRouter.getRequestHandler(httpExchange));

        assertNotNull(resultHandler);
        Response response = assertDoesNotThrow(() -> resultHandler.apply(httpExchange));

        assertEquals("get response", response.content());
    }

    @Test
    void testGetRequestWithSuccessfulAuthentication() throws URISyntaxException {
        when(mockedAuthenticationService.isAuthenticated(httpExchange)).thenReturn(true);
        when(httpExchange.getRequestMethod()).thenReturn("GET");
        when(httpExchange.getRequestURI()).thenReturn(new URI("http://localhost:8000/test/authenticated"));

        Function<HttpExchange, Response> resultHandler =
                assertDoesNotThrow(() -> apiRouter.getRequestHandler(httpExchange));

        assertNotNull(resultHandler);
        Response response = assertDoesNotThrow(() -> resultHandler.apply(httpExchange));

        assertEquals("authenticated response", response.content());
    }

    @Test
    void testGetRequestWithAuthenticationFailure() throws URISyntaxException {
        when(mockedAuthenticationService.isAuthenticated(httpExchange)).thenReturn(false);
        when(httpExchange.getRequestMethod()).thenReturn("GET");
        when(httpExchange.getRequestURI()).thenReturn(new URI("http://localhost:8000/test/authenticated"));

        Function<HttpExchange, Response> resultHandler =
                assertDoesNotThrow(() -> apiRouter.getRequestHandler(httpExchange));

        assertNotNull(resultHandler);
        assertThrows(AuthenticationException.class, () -> resultHandler.apply(httpExchange));
    }

    @ParameterizedTest
    @CsvSource({
        "http://localhost:8000/test/invalid,GET",
        "http://localhost:8000/invalid,GET",
        "http://localhost:8000/test,PATCH"
    })
    void testGetRequestWithInvalidRoute(String url, String method) throws URISyntaxException {
        when(httpExchange.getRequestMethod()).thenReturn(method);
        when(httpExchange.getRequestURI()).thenReturn(new URI(url));

        assertThrows(NotFoundException.class, () -> apiRouter.getRequestHandler(httpExchange));
    }

    @Test
    void testGetRequestWithParameters() throws URISyntaxException {
        when(httpExchange.getRequestMethod()).thenReturn("GET");
        when(httpExchange.getRequestURI())
                .thenReturn(new URI("http://localhost:8000/test/parameters?param1=1&param2=2"));

        Function<HttpExchange, Response> resultHandler =
                assertDoesNotThrow(() -> apiRouter.getRequestHandler(httpExchange));

        assertNotNull(resultHandler);
        Response response = assertDoesNotThrow(() -> resultHandler.apply(httpExchange));

        assertEquals("get with params response 1 2", response.content());
    }

    @Test
    void testGetRequestWithoutOptionalParameter() throws URISyntaxException {
        when(httpExchange.getRequestMethod()).thenReturn("GET");
        when(httpExchange.getRequestURI()).thenReturn(new URI("http://localhost:8000/test/parameters?param1=1"));

        Function<HttpExchange, Response> resultHandler =
                assertDoesNotThrow(() -> apiRouter.getRequestHandler(httpExchange));

        assertNotNull(resultHandler);
        Response response = assertDoesNotThrow(() -> resultHandler.apply(httpExchange));

        assertEquals("get with params response 1 null", response.content());
    }

    @Test
    void testGetRequestWithoutRequiredParameter() throws URISyntaxException {
        when(httpExchange.getRequestMethod()).thenReturn("GET");
        when(httpExchange.getRequestURI()).thenReturn(new URI("http://localhost:8000/test/parameters"));

        Function<HttpExchange, Response> resultHandler =
                assertDoesNotThrow(() -> apiRouter.getRequestHandler(httpExchange));

        assertNotNull(resultHandler);
        InvalidRequestException resultException =
                assertThrows(InvalidRequestException.class, () -> resultHandler.apply(httpExchange));

        assertEquals("Parameter param1 is required.", resultException.getMessage());
    }

    @Test
    void testGetRequestWithInvalidParameterType() throws URISyntaxException {
        when(httpExchange.getRequestMethod()).thenReturn("GET");
        when(httpExchange.getRequestURI()).thenReturn(new URI("http://localhost:8000/test/parameters?param1=test"));

        Function<HttpExchange, Response> resultHandler =
                assertDoesNotThrow(() -> apiRouter.getRequestHandler(httpExchange));

        assertNotNull(resultHandler);
        InvalidRequestException resultException =
                assertThrows(InvalidRequestException.class, () -> resultHandler.apply(httpExchange));

        assertEquals(
                "Error parsing parameters: parameter param1 should be of type class java.lang.Integer.",
                resultException.getMessage());
    }

    @Test
    void testPost() throws URISyntaxException {
        when(httpExchange.getRequestMethod()).thenReturn("POST");
        when(httpExchange.getRequestURI()).thenReturn(new URI("http://localhost:8000/test"));

        Function<HttpExchange, Response> resultHandler =
                assertDoesNotThrow(() -> apiRouter.getRequestHandler(httpExchange));

        assertNotNull(resultHandler);
        Response response = assertDoesNotThrow(() -> resultHandler.apply(httpExchange));

        assertEquals("post response", response.content());
    }

    @Test
    void testPostWithBody() throws URISyntaxException, JsonProcessingException {
        Map<String, String> body = Map.of("test", "body");
        when(httpExchange.getRequestMethod()).thenReturn("POST");
        when(httpExchange.getRequestURI()).thenReturn(new URI("http://localhost:8000/test/post-body"));
        when(httpExchange.getRequestBody()).thenReturn(new ByteArrayInputStream(objectMapper.writeValueAsBytes(body)));

        Function<HttpExchange, Response> resultHandler =
                assertDoesNotThrow(() -> apiRouter.getRequestHandler(httpExchange));

        assertNotNull(resultHandler);
        Response response = assertDoesNotThrow(() -> resultHandler.apply(httpExchange));

        assertEquals("post with body response {test=body}", response.content());
    }

    @Test
    void testWithInvalidBody() throws URISyntaxException {
        when(httpExchange.getRequestMethod()).thenReturn("POST");
        when(httpExchange.getRequestURI()).thenReturn(new URI("http://localhost:8000/test/post-body"));
        when(httpExchange.getRequestBody()).thenReturn(new ByteArrayInputStream("test".getBytes()));

        Function<HttpExchange, Response> resultHandler =
                assertDoesNotThrow(() -> apiRouter.getRequestHandler(httpExchange));

        assertNotNull(resultHandler);
        InvalidRequestException resultException =
                assertThrows(InvalidRequestException.class, () -> resultHandler.apply(httpExchange));

        assertEquals("Error parsing request body.", resultException.getMessage());
    }

    @Test
    void testGetThatThrowsApiException() throws URISyntaxException {
        when(httpExchange.getRequestMethod()).thenReturn("GET");
        when(httpExchange.getRequestURI()).thenReturn(new URI("http://localhost:8000/test/api-exception"));

        Function<HttpExchange, Response> resultHandler =
                assertDoesNotThrow(() -> apiRouter.getRequestHandler(httpExchange));

        assertNotNull(resultHandler);
        assertThrows(ApiException.class, () -> resultHandler.apply(httpExchange));
    }

    @Test
    void testGetThatThrowsGenericException() throws URISyntaxException {
        when(httpExchange.getRequestMethod()).thenReturn("GET");
        when(httpExchange.getRequestURI()).thenReturn(new URI("http://localhost:8000/test/generic-exception"));

        Function<HttpExchange, Response> resultHandler =
                assertDoesNotThrow(() -> apiRouter.getRequestHandler(httpExchange));

        assertNotNull(resultHandler);
        InternalException resultException =
                assertThrows(InternalException.class, () -> resultHandler.apply(httpExchange));

        assertEquals("Error handling request.", resultException.getMessage());
    }

    @Test
    void testRegisterRoutesWithAuthenticatedRoutesWithoutAuthenticationService() {
        ApiRouter customRouter = new ApiRouter(objectMapper, null);

        InternalException resultException =
                assertThrows(InternalException.class, () -> customRouter.registerRoutes(List.of(new TestController())));

        assertEquals(
                "An AuthenticationService must be provided when using authenticated endpoints.",
                resultException.getMessage());
    }

    @Test
    void testRegisterRoutesWithInvalidController() {
        ApiRouter customRouter = new ApiRouter(objectMapper, null);

        InternalException resultException = assertThrows(
                InternalException.class, () -> customRouter.registerRoutes(List.of(new InvalidTestController())));

        assertEquals(
                "Request method parameters should always hava a parameter type annotation.",
                resultException.getMessage());
    }
}
