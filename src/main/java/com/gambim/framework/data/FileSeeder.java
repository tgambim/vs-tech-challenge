package com.gambim.framework.data;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gambim.framework.data.Entity;
import com.gambim.framework.data.Repository;
import com.gambim.framework.data.Seeder;
import java.io.IOException;
import java.util.List;

public abstract class FileSeeder<T extends Entity> implements Seeder {
    protected final List<T> seedData;
    private final Class<T> clazz;
    private final ObjectMapper objectMapper;
    private final Repository<T> repository;

    protected FileSeeder(String resourceFile, Class<T> clazz, ObjectMapper objectMapper, Repository<T> repository) throws IOException {
        this.clazz = clazz;
        this.objectMapper = objectMapper;
        seedData = parseDataFromFile(resourceFile);
        this.repository = repository;
    }

    private List<T> parseDataFromFile(String resourceFile) throws IOException {
        String jsonContent = new String(
                getClass().getClassLoader().getResourceAsStream(resourceFile).readAllBytes());

        JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
        return objectMapper.readValue(jsonContent, type);
    }

    @Override
    public void seed() {
        seedData.forEach(repository::saveWithPredefinedId);
    }
}
