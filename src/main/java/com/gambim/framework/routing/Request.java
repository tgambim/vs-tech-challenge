package com.gambim.framework.routing;

import com.sun.net.httpserver.HttpExchange;
import java.util.function.Function;

public record Request(RequestMethod method, String path, Function<HttpExchange, Response> handler) {}
