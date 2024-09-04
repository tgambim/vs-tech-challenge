package com.gambim.framework.routing.util;

import com.gambim.framework.annotation.Controller;
import com.gambim.framework.annotation.RequestMapping;
import com.gambim.framework.routing.RequestMethod;
import java.lang.reflect.Method;

public class RoutingUtils {
    public static String getBasePath(Object controller) {
        Controller controllerAnnotation = controller.getClass().getAnnotation(Controller.class);
        return controllerAnnotation.basePath();
    }

    public static String getRequestPath(Method method) {
        RequestMapping request = method.getAnnotation(RequestMapping.class);
        return request.path();
    }

    public static RequestMethod getRequestMethod(Method method) {
        RequestMapping request = method.getAnnotation(RequestMapping.class);
        return request.method();
    }

    public static String getFullPath(String basePath, Method method) {
        return String.format("%s%s", basePath, getRequestPath(method));
    }
}
