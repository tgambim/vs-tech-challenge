package com.gambim.converter;

import com.gambim.domain.Location;
import com.gambim.domain.ServiceCategory;
import com.gambim.domain.Vendor;
import com.gambim.dto.CreateVendorRequest;
import com.gambim.dto.VendorResponse;
import com.gambim.dto.VendorsCountResponse;
import com.gambim.framework.annotation.Component;
import java.util.Map;

@Component
public class VendorConverter {
    private final ServiceCategoryConverter serviceCategoryConverter;
    private final LocationConverter locationConverter;

    public VendorConverter(ServiceCategoryConverter serviceCategoryConverter, LocationConverter locationConverter) {
        this.serviceCategoryConverter = serviceCategoryConverter;
        this.locationConverter = locationConverter;
    }

    public Vendor fromCreateVendorRequest(CreateVendorRequest request) {
        Vendor vendor = new Vendor();
        vendor.setName(request.name());
        vendor.setLocationId(request.locationId());
        vendor.setServicesMap(request.services());
        return vendor;
    }

    public VendorsCountResponse toVendorsCountResponse(Long compliantCount, Long notCompliantCount) {
        Long totalCount = compliantCount + notCompliantCount;

        return new VendorsCountResponse(totalCount, compliantCount, notCompliantCount);
    }

    public VendorResponse toVendorResponse(
            Vendor vendor, Location location, Map<ServiceCategory, Boolean> serviceCategories) {
        return new VendorResponse(
                vendor.getId(),
                vendor.getName(),
                locationConverter.toLocationResponse(location),
                serviceCategoryConverter.toVendorServiceCategoryResponse(serviceCategories));
    }
}
