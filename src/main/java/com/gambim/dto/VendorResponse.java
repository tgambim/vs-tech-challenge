package com.gambim.dto;

import java.util.List;

public record VendorResponse(
        Integer id, String name, LocationResponse location, List<VendorServiceCategoryResponse> serviceCategories) {}
