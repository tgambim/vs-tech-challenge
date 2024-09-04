package com.gambim.domain;

import com.gambim.framework.data.Entity;

public class Job extends Entity {
    private String description;
    private Integer locationId;
    private Integer serviceCategoryId;

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getServiceCategoryId() {
        return serviceCategoryId;
    }

    public void setServiceCategoryId(Integer serviceCategoryId) {
        this.serviceCategoryId = serviceCategoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
