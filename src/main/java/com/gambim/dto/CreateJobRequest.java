package com.gambim.dto;

public record CreateJobRequest(String description, Integer serviceCategoryId, Integer locationId) {}
