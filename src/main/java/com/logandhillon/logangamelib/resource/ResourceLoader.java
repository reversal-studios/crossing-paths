package com.logandhillon.logangamelib.resource;

/**
 * Represents an operation that loads a resource from IO provided a path.
 *
 * @param <T> type of resource to load
 *
 * @author Logan Dhillon
 */
@FunctionalInterface
public interface ResourceLoader<T> {
    /**
     * Finds the associated resource for the path and loads it as the generic type.
     *
     * @param path path to resource
     *
     * @return resource of type {@code <T>}
     */
    T load(String path);
}
