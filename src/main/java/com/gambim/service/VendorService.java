package com.gambim.service;

import static java.util.Optional.ofNullable;

import com.gambim.converter.VendorConverter;
import com.gambim.domain.Job;
import com.gambim.domain.Location;
import com.gambim.domain.ServiceCategory;
import com.gambim.domain.Vendor;
import com.gambim.dto.CreateVendorRequest;
import com.gambim.dto.VendorResponse;
import com.gambim.dto.VendorsCountResponse;
import com.gambim.framework.annotation.Component;
import com.gambim.repository.VendorRepository;
import com.gambim.validation.VendorValidator;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class VendorService {
    private final VendorRepository vendorRepository;
    private final VendorConverter vendorConverter;
    private final VendorValidator vendorValidator;
    private final JobService jobService;
    private final LocationService locationService;
    private final ServiceCategoryService serviceCategoryService;

    public VendorService(
            VendorRepository vendorRepository,
            VendorConverter vendorConverter,
            VendorValidator vendorValidator,
            JobService jobService,
            LocationService locationService,
            ServiceCategoryService serviceCategoryService) {
        this.vendorRepository = vendorRepository;
        this.vendorConverter = vendorConverter;
        this.vendorValidator = vendorValidator;
        this.jobService = jobService;
        this.locationService = locationService;
        this.serviceCategoryService = serviceCategoryService;
    }

    public Collection<VendorResponse> list(Integer jobId) {
        Job job = jobService.findById(jobId);
        Integer serviceCategoryId = job.getServiceCategoryId();
        return vendorRepository.list().stream()
                .filter(vendor -> vendor.getLocationId().equals(job.getLocationId())
                        && vendor.getServicesMap().containsKey(serviceCategoryId))
                .sorted(Comparator.comparing(
                                (Vendor vendor) -> vendor.getServicesMap().get(serviceCategoryId))
                        .reversed()
                        .thenComparing(Vendor::getId))
                .map(vendor -> toVendorResponse(
                        vendor,
                        Map.of(serviceCategoryId, vendor.getServicesMap().get(serviceCategoryId))))
                .toList();
    }

    public VendorResponse create(CreateVendorRequest request) {
        vendorValidator.validateCreation(request);
        Vendor vendor = vendorRepository.save(vendorConverter.fromCreateVendorRequest(request));
        return toVendorResponse(vendor, vendor.getServicesMap());
    }

    public VendorsCountResponse getCount(Integer locationId, Integer serviceCategoryId) {
        Map<Boolean, Long> vendorsCountByCompliant = vendorRepository.list().stream()
                .filter(vendor -> vendor.getLocationId().equals(locationId)
                        && vendor.getServicesMap().containsKey(serviceCategoryId))
                .collect(Collectors.groupingBy(
                        vendor -> vendor.getServicesMap().get(serviceCategoryId), Collectors.counting()));

        Long compliantCount = ofNullable(vendorsCountByCompliant.get(true)).orElse(0L);
        Long notCompliantCount = ofNullable(vendorsCountByCompliant.get(false)).orElse(0L);

        return vendorConverter.toVendorsCountResponse(compliantCount, notCompliantCount);
    }

    private VendorResponse toVendorResponse(Vendor vendor, Map<Integer, Boolean> serviceIdsMap) {
        Location location = locationService.findById(vendor.getLocationId());
        return vendorConverter.toVendorResponse(vendor, location, getServiceCategoriesMap(serviceIdsMap));
    }

    private Map<ServiceCategory, Boolean> getServiceCategoriesMap(Map<Integer, Boolean> serviceIdsMap) {
        Map<ServiceCategory, Boolean> servicesMap = new HashMap<>();
        serviceIdsMap.forEach(
                (serviceId, compliant) -> servicesMap.put(serviceCategoryService.findById(serviceId), compliant));
        return servicesMap;
    }
}
