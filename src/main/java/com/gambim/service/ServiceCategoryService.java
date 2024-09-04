package com.gambim.service;

import com.gambim.domain.ServiceCategory;
import com.gambim.framework.annotation.Component;
import com.gambim.framework.exception.NotFoundException;
import com.gambim.repository.ServiceCategoryRepository;

@Component
public class ServiceCategoryService {
    private final ServiceCategoryRepository serviceCategoryRepository;

    public ServiceCategoryService(ServiceCategoryRepository serviceCategoryRepository) {
        this.serviceCategoryRepository = serviceCategoryRepository;
    }

    public ServiceCategory findById(Integer serviceCategoryId) {
        return serviceCategoryRepository
                .find(serviceCategoryId)
                .orElseThrow(() -> new NotFoundException("Service category not found."));
    }
}
