package com.gambim.seeder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gambim.domain.Location;
import com.gambim.framework.annotation.Component;
import com.gambim.framework.data.FileSeeder;
import com.gambim.framework.data.Seeder;
import com.gambim.repository.LocationRepository;
import java.io.IOException;

@Component
public class LocationSeeder extends FileSeeder<Location> implements Seeder {
    public LocationSeeder(LocationRepository locationRepository, ObjectMapper objectMapper) throws IOException {
        super("seed/locations.json", Location.class, objectMapper, locationRepository);
    }
}
