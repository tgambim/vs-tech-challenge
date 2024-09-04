package com.gambim.framework.routing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gambim.framework.annotation.Component;
import com.gambim.framework.exception.ApiException;
import com.gambim.framework.exception.InternalException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class RequestHandler implements HttpHandler {
    private final ApiRouter apiRouter;
    private final ObjectMapper objectMapper;
    private static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(ApiRouter apiRouter, ObjectMapper objectMapper) {
        this.apiRouter = apiRouter;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Response response = apiRouter.getRequestHandler(exchange).apply(exchange);
            sendResponse(exchange, response.content(), response.statusCode());
        } catch (ApiException e) {
            sendErrorResponse(exchange, e);
        } catch (InternalException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            sendErrorResponse(exchange);
        }
    }

    private void sendErrorResponse(HttpExchange exchange) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse("Internal server error.");
        sendResponse(exchange, errorResponse, 500);
    }

    private void sendErrorResponse(HttpExchange exchange, ApiException e) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getValidationErrors());
        sendResponse(exchange, errorResponse, e.getStatusCode());
    }

    private void sendResponse(HttpExchange exchange, Object response, Integer status) throws IOException {
        String responseJson = "";
        if (response != null) {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            responseJson = objectMapper.writeValueAsString(response);
        }
        exchange.sendResponseHeaders(status, responseJson.length());
        exchange.getResponseBody().write(responseJson.getBytes());
        exchange.getResponseBody().close();
    }
}
