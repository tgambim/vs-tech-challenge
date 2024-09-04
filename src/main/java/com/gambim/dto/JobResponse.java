package com.gambim.dto;

public record JobResponse(
        Integer id, String description, LocationResponse location, ServiceCategoryResponse serviceCategory) {}
