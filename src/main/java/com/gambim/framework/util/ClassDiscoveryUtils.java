package com.gambim.framework.util;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;
import org.reflections.Reflections;

public class ClassDiscoveryUtils {
    private static final Reflections reflections = new Reflections("com.gambim");

    public static Set<Class<?>> getClassesAnnotatedWith(Set<Class<? extends Annotation>> annotations) {
        return annotations.stream()
                .flatMap(annotation -> reflections.getTypesAnnotatedWith(annotation).stream())
                .collect(Collectors.toSet());
    }
}
