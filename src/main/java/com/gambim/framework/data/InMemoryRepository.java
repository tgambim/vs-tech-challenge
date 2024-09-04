package com.gambim.framework.data;

import com.gambim.framework.exception.InternalException;
import java.util.*;

public class InMemoryRepository<T extends Entity> implements Repository<T> {
    private final HashMap<Integer, T> storedEntities = new HashMap<>();
    private Integer lastUsedId = 0;

    @Override
    public Collection<T> list() {
        return storedEntities.values();
    }

    @Override
    public T save(T object) {
        object.setId(++lastUsedId);
        return saveWithPredefinedId(object);
    }

    public T saveWithPredefinedId(T object) {
        if (storedEntities.containsKey(object.getId())) {
            throw new InternalException("Entity id already exists.");
        }
        if (object.getId() == null) {
            throw new InternalException("You have to inform de entity id.");
        }
        lastUsedId = Math.max(object.getId(), lastUsedId);
        storedEntities.put(object.getId(), object);
        return object;
    }

    @Override
    public Optional<T> find(Integer id) {
        return Optional.ofNullable(storedEntities.get(id));
    }
}
