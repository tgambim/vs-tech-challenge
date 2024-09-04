package com.gambim.framework;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gambim.framework.annotation.Component;
import com.gambim.framework.annotation.Controller;
import com.gambim.framework.data.Seeder;
import com.gambim.framework.exception.InternalException;
import com.gambim.framework.container.ApplicationContainer;
import com.gambim.framework.routing.ApiRouter;
import com.gambim.framework.routing.RequestHandler;
import com.gambim.framework.util.ClassDiscoveryUtils;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

public class HttpApplication {
    private final ApplicationContainer container = new ApplicationContainer();
    private HttpServer server;

    public HttpApplication() {
        initApplication();
    }

    private void initApplication() {
        ObjectMapper objectMapper = new ObjectMapper()
                .findAndRegisterModules()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        container.registerInstance(objectMapper);

        container.registerAll(getClassesToCreate());

        container.getInstance(ApiRouter.class).registerRoutes(getControllers());

        container.getInstancesThatImplements(Seeder.class).forEach(Seeder::seed);
    }

    private Set<Class<?>> getClassesToCreate() {
        return ClassDiscoveryUtils.getClassesAnnotatedWith(Set.of(Controller.class, Component.class));
    }

    private List<Object> getControllers() {
        return container.getInstancesByAnnotation(Controller.class);
    }

    public ApplicationContainer getContainer() {
        return container;
    }

    public void startHttpServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/", container.getInstance(RequestHandler.class));
            server.start();
        } catch (IOException e) {
            throw new InternalException("Error creating the server");
        }
    }

    public void stopHttpServer() {
        server.stop(0);
    }
}
