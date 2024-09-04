package com.gambim.converter;

import com.gambim.domain.ServiceCategory;
import com.gambim.dto.ServiceCategoryResponse;
import com.gambim.dto.VendorServiceCategoryResponse;
import com.gambim.framework.annotation.Component;
import java.util.List;
import java.util.Map;

@Component
public class ServiceCategoryConverter {

    public ServiceCategoryResponse toServiceCategoryResponse(ServiceCategory serviceCategory) {
        return new ServiceCategoryResponse(serviceCategory.getId(), serviceCategory.getName());
    }

    public List<VendorServiceCategoryResponse> toVendorServiceCategoryResponse(
            Map<ServiceCategory, Boolean> serviceCategories) {
        return serviceCategories.entrySet().stream()
                .map(entry -> toVendorServiceCategoryResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    private VendorServiceCategoryResponse toVendorServiceCategoryResponse(
            ServiceCategory serviceCategory, Boolean compliant) {
        return new VendorServiceCategoryResponse(serviceCategory.getId(), serviceCategory.getName(), compliant);
    }
}
