package com.gambim.framework.data;

import java.util.Collection;
import java.util.Optional;

public interface Repository<T extends Entity> {
    Collection<T> list();

    T save(T object);

    T saveWithPredefinedId(T object);

    Optional<T> find(Integer id);
}
