package com.gambim.framework.container;

import static java.util.Optional.ofNullable;

import com.gambim.framework.exception.InternalException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContainer {
    private final ConcurrentHashMap<Class<?>, Object> instancesContainer = new ConcurrentHashMap<>();

    public <T> T getInstance(Class<T> clazz) {
        return ofNullable((T) instancesContainer.get(clazz))
                .or(() -> getInstancesThatImplements(clazz).stream().findFirst())
                .orElse(null);
    }

    public void registerInstance(Object object) {
        instancesContainer.putIfAbsent(object.getClass(), object);
    }

    private Object registerInstance(Class<?> clazz, Set<Class<?>> classesToCreate, Set<Class<?>> dependencySet) {
        Object existingInstance = instancesContainer.get(clazz);
        if (existingInstance != null) {
            return existingInstance;
        }
        Class<?> classToCreate = getClassToCreate(classesToCreate, clazz);

        checkForCircularDependencies(classToCreate, dependencySet);

        try {
            Constructor<?> constructor = findConstructor(classToCreate);
            Object[] args = resolveConstructorArgs(constructor, classesToCreate, dependencySet);
            Object instance = constructor.newInstance(args);
            instancesContainer.put(classToCreate, instance);
            return instance;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new InternalException(e);
        }
    }

    private void checkForCircularDependencies(Class<?> clazz, Set<Class<?>> dependencySet) {
        if (dependencySet.contains(clazz)) {
            throw new InternalException("You may have a circular dependency.");
        }
        dependencySet.add(clazz);
    }

    private Class<?> getClassToCreate(Set<Class<?>> classesToCreate, Class<?> clazz) {
        if (classesToCreate.contains(clazz)) {
            return clazz;
        }
        return classesToCreate.stream()
                .filter(clazz::isAssignableFrom)
                .findFirst()
                .orElseThrow(() -> new InternalException(
                        String.format("Unsatisfied dependency: could not find an instance of %s", clazz.getName())));
    }

    private Constructor<?> findConstructor(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .filter(constructor -> Modifier.isPublic(constructor.getModifiers()))
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("Injected classes should have at least one public constructor"));
    }

    private Object[] resolveConstructorArgs(
            Constructor<?> constructor, Set<Class<?>> classesToCreate, Set<Class<?>> dependencySet) {
        return Arrays.stream(constructor.getParameters())
                .map(Parameter::getType)
                .map(parameterClass -> ofNullable((Object) getInstance(parameterClass))
                        .orElseGet(() -> registerInstance(parameterClass, classesToCreate, dependencySet)))
                .toArray();
    }

    public <T> List<T> getInstancesThatImplements(Class<T> clazz) {
        return (List<T>) instancesContainer.values().stream()
                .filter(object -> clazz.isAssignableFrom(object.getClass()))
                .toList();
    }

    public <T extends Annotation> List<Object> getInstancesByAnnotation(Class<T> clazz) {
        return instancesContainer.values().stream()
                .filter(object -> object.getClass().isAnnotationPresent(clazz))
                .toList();
    }

    public void registerAll(Set<Class<?>> classesToCreate) {
        for (Class<?> clazz : classesToCreate) {
            registerInstance(clazz, classesToCreate, new HashSet<>());
        }
    }
}
