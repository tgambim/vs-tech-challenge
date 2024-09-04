package com.gambim.converter;

import com.gambim.domain.Job;
import com.gambim.domain.Location;
import com.gambim.domain.ServiceCategory;
import com.gambim.dto.CreateJobRequest;
import com.gambim.dto.JobResponse;
import com.gambim.framework.annotation.Component;

@Component
public class JobConverter {
    private final ServiceCategoryConverter serviceCategoryConverter;
    private final LocationConverter locationConverter;

    public JobConverter(ServiceCategoryConverter serviceCategoryConverter, LocationConverter locationConverter) {
        this.serviceCategoryConverter = serviceCategoryConverter;
        this.locationConverter = locationConverter;
    }

    public Job fromCreateJobRequest(CreateJobRequest request) {
        Job job = new Job();
        job.setDescription(request.description());
        job.setLocationId(request.locationId());
        job.setServiceCategoryId(request.serviceCategoryId());
        return job;
    }

    public JobResponse toJobResponse(Job job, Location location, ServiceCategory serviceCategory) {
        return new JobResponse(
                job.getId(),
                job.getDescription(),
                locationConverter.toLocationResponse(location),
                serviceCategoryConverter.toServiceCategoryResponse(serviceCategory));
    }
}
