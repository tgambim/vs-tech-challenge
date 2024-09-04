package com.gambim.converter;

import com.gambim.domain.Location;
import com.gambim.dto.LocationResponse;
import com.gambim.framework.annotation.Component;

@Component
public class LocationConverter {
    public LocationResponse toLocationResponse(Location location) {
        return new LocationResponse(location.getId(), location.getName(), location.getState());
    }
}
