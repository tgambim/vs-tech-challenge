package com.gambim.framework.routing.util;

import static java.util.Optional.ofNullable;

import java.net.URI;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class URIUtils {

    public static Map<String, String> getURIQueryParameter(URI uri) {
        return ofNullable(uri.getQuery())
                .map(query -> Arrays.stream(query.split("&"))
                        .map(URIUtils::parseParameter)
                        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)))
                .orElse(Map.of());
    }

    private static AbstractMap.SimpleEntry<String, String> parseParameter(String parameter) {
        String[] parameterParts = parameter.split("=");
        String parameterName = parameterParts[0];
        String parameterValue = "";
        if (parameterParts.length > 1) {
            parameterValue = parameterParts[1];
        }

        return new AbstractMap.SimpleEntry<>(parameterName, parameterValue);
    }
}
