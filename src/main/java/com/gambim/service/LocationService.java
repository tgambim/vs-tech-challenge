package com.gambim.service;

import com.gambim.domain.Location;
import com.gambim.framework.annotation.Component;
import com.gambim.framework.exception.NotFoundException;
import com.gambim.repository.LocationRepository;

@Component
public class LocationService {
    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location findById(Integer locationId) {
        return locationRepository.find(locationId).orElseThrow(() -> new NotFoundException("Location not found."));
    }
}
