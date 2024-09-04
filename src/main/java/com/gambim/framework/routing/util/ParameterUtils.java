package com.gambim.framework.routing.util;

import com.gambim.framework.exception.InvalidRequestException;
import java.util.Map;

public class ParameterUtils {
    private ParameterUtils() {}

    public static Object getQueryParameter(
            Map<String, String> queryParams, Class<?> type, String name, Boolean optional) {
        if (queryParams.containsKey(name)) {
            try {
                return parseParameter(type, queryParams.get(name));
            } catch (Exception e) {
                throw new InvalidRequestException(
                        String.format("Error parsing parameters: parameter %s should be of type %s.", name, type));
            }
        } else if (optional) {
            return null;
        }
        throw new InvalidRequestException(String.format("Parameter %s is required.", name));
    }

    private static Object parseParameter(Class<?> clazz, String value) {
        if (clazz == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        if (clazz == Byte.class) {
            return Byte.parseByte(value);
        }
        if (clazz == Short.class) {
            return Short.parseShort(value);
        }
        if (clazz == Integer.class) {
            return Integer.parseInt(value);
        }
        if (clazz == Long.class) {
            return Long.parseLong(value);
        }
        if (clazz == Float.class) {
            return Float.parseFloat(value);
        }
        if (clazz == Double.class) {
            return Double.parseDouble(value);
        }
        return value;
    }
}
