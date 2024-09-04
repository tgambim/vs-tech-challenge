package com.gambim.domain;

import com.gambim.framework.data.Entity;
import java.util.Map;

public class Vendor extends Entity {
    private String name;
    private Integer locationId;
    private Map<Integer, Boolean> servicesMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Map<Integer, Boolean> getServicesMap() {
        return servicesMap;
    }

    public void setServicesMap(Map<Integer, Boolean> servicesMap) {
        this.servicesMap = servicesMap;
    }
}
