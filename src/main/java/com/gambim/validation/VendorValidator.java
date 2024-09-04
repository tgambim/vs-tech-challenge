package com.gambim.validation;

import com.gambim.dto.CreateVendorRequest;
import com.gambim.framework.annotation.Component;
import com.gambim.framework.exception.NotFoundException;
import com.gambim.framework.exception.ValidationException;
import com.gambim.service.LocationService;
import com.gambim.service.ServiceCategoryService;
import java.util.HashMap;
import java.util.Map;

@Component
public class VendorValidator {
    private final LocationService locationService;
    private final ServiceCategoryService serviceCategoryService;

    public VendorValidator(LocationService locationService, ServiceCategoryService serviceCategoryService) {
        this.locationService = locationService;
        this.serviceCategoryService = serviceCategoryService;
    }

    public void validateCreation(CreateVendorRequest request) {
        Map<String, String> errors = new HashMap<>();

        try {
            locationService.findById(request.locationId());
        } catch (NotFoundException e) {
            errors.put("locationId", e.getMessage());
        }

        try {
            request.services().keySet().forEach(serviceCategoryService::findById);
        } catch (NotFoundException e) {
            errors.put("serviceCategoryId", e.getMessage());
        }

        if (request.name() == null || request.name().isEmpty()) {
            errors.put("name", "Name cannot be blank.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("The vendor contains errors and cannot not be created.", errors);
        }
    }
}
