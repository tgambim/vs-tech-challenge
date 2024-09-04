package com.gambim.validation;

import com.gambim.dto.CreateJobRequest;
import com.gambim.framework.annotation.Component;
import com.gambim.framework.exception.NotFoundException;
import com.gambim.framework.exception.ValidationException;
import com.gambim.service.LocationService;
import com.gambim.service.ServiceCategoryService;
import java.util.HashMap;
import java.util.Map;

@Component
public class JobValidator {
    private final LocationService locationService;
    private final ServiceCategoryService serviceCategoryService;

    public JobValidator(LocationService locationService, ServiceCategoryService serviceCategoryService) {
        this.locationService = locationService;
        this.serviceCategoryService = serviceCategoryService;
    }

    public void validateCreation(CreateJobRequest request) {
        Map<String, String> errors = new HashMap<>();

        try {
            locationService.findById(request.locationId());
        } catch (NotFoundException e) {
            errors.put("locationId", e.getMessage());
        }

        try {
            serviceCategoryService.findById(request.serviceCategoryId());
        } catch (NotFoundException e) {
            errors.put("serviceCategoryId", e.getMessage());
        }

        if (request.description() == null || request.description().isEmpty()) {
            errors.put("description", "Description cannot be blank.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("The job contains errors and cannot not be created.", errors);
        }
    }
}
