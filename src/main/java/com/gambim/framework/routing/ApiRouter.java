package com.gambim.framework.routing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gambim.framework.annotation.*;
import com.gambim.framework.exception.*;
import com.gambim.framework.routing.util.ParameterUtils;
import com.gambim.framework.routing.util.RoutingUtils;
import com.gambim.framework.routing.util.URIUtils;
import com.gambim.framework.security.AuthenticationService;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class ApiRouter {

    private final LinkedList<Request> routeList = new LinkedList<>();
    private final ObjectMapper objectMapper;
    private final AuthenticationService authenticationService;

    public ApiRouter(ObjectMapper objectMapper, AuthenticationService authenticationService) {
        this.objectMapper = objectMapper;
        this.authenticationService = authenticationService;
    }

    public void registerRoutes(List<Object> controllers) {
        controllers.forEach(this::registerRoutes);
    }

    private void registerRoutes(Object controller) {
        String basePath = RoutingUtils.getBasePath(controller);

        Arrays.stream(controller.getClass().getMethods())
                .filter(method -> method.getAnnotation(RequestMapping.class) != null)
                .forEach(method -> registerRoutes(controller, basePath, method));
    }

    private void registerRoutes(Object controller, String basePath, Method method) {
        validateAuthentication(method);
        validateArgs(method);
        Request request = new Request(
                RoutingUtils.getRequestMethod(method),
                RoutingUtils.getFullPath(basePath, method),
                createHandler(controller, method));

        routeList.push(request);
    }

    private void validateAuthentication(Method method) {
        if (method.isAnnotationPresent(Authenticated.class) && authenticationService == null) {
            throw new InternalException(
                    "An AuthenticationService must be provided when using authenticated endpoints.");
        }
    }

    private void validateArgs(Method method) {
        for (Parameter parameter : method.getParameters()) {
            if (parameter.getAnnotation(Body.class) == null && parameter.getAnnotation(QueryParam.class) == null) {
                throw new InternalException(
                        "Request method parameters should always hava a parameter type annotation.");
            }
        }
    }

    private Function<HttpExchange, Response> createHandler(Object controller, Method method) {
        return (HttpExchange httpRequest) -> {
            try {
                validateAuthentication(method, httpRequest);
                Object content = method.invoke(controller, getRequestArgs(httpRequest, method));
                return new Response(content, getStatusCode(method));
            } catch (IllegalAccessException e) {
                throw new InternalException("Error handling request.");
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof ApiException apiException) {
                    throw apiException;
                }
                throw new InternalException("Error handling request.");
            }
        };
    }

    private void validateAuthentication(Method method, HttpExchange httpRequest) {
        if (method.isAnnotationPresent(Authenticated.class) && !authenticationService.isAuthenticated(httpRequest)) {
            throw new AuthenticationException();
        }
    }

    private Integer getStatusCode(Method method) {
        ResponseStatus responseStatus = method.getAnnotation(ResponseStatus.class);
        if (responseStatus != null) {
            return responseStatus.value();
        }
        return 200;
    }

    private Object[] getRequestArgs(HttpExchange httpRequest, Method method) {
        Map<String, String> queryParams = URIUtils.getURIQueryParameter(httpRequest.getRequestURI());
        return Arrays.stream(method.getParameters())
                .map(parameter -> {
                    if (parameter.getAnnotation(Body.class) != null) {
                        return parseBodyArg(httpRequest.getRequestBody(), parameter.getType());
                    }
                    QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
                    return ParameterUtils.getQueryParameter(
                            queryParams, parameter.getType(), queryParam.name(), queryParam.optional());
                })
                .toArray();
    }

    private Object parseBodyArg(InputStream requestBody, Class<?> type) {
        try {
            String bodyJson = new String(requestBody.readAllBytes());
            return objectMapper.readValue(bodyJson, type);
        } catch (IOException e) {
            throw new InvalidRequestException("Error parsing request body.");
        }
    }

    public Function<HttpExchange, Response> getRequestHandler(HttpExchange httpExchange) {
        return routeList.stream()
                .filter(request -> matchRequest(request, httpExchange))
                .map(Request::handler)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Not found."));
    }

    private boolean matchRequest(Request request, HttpExchange httpExchange) {
        return request.method().name().equals(httpExchange.getRequestMethod())
                && request.path().equals(httpExchange.getRequestURI().getPath());
    }
}
