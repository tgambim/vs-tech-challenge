package com.gambim.repository;

import com.gambim.domain.Location;
import com.gambim.framework.annotation.Component;
import com.gambim.framework.data.InMemoryRepository;

@Component
public class LocationRepository extends InMemoryRepository<Location> {}
