package com.gambim.service;

import com.gambim.converter.JobConverter;
import com.gambim.domain.Job;
import com.gambim.domain.Location;
import com.gambim.domain.ServiceCategory;
import com.gambim.dto.CreateJobRequest;
import com.gambim.dto.JobResponse;
import com.gambim.framework.annotation.Component;
import com.gambim.framework.exception.NotFoundException;
import com.gambim.repository.JobRepository;
import com.gambim.validation.JobValidator;

@Component
public class JobService {
    private final JobRepository jobRepository;
    private final JobConverter jobConverter;
    private final JobValidator jobValidator;
    private final LocationService locationService;
    private final ServiceCategoryService serviceCategoryService;

    public JobService(
            JobRepository jobRepository,
            JobConverter jobConverter,
            JobValidator jobValidator,
            LocationService locationService,
            ServiceCategoryService serviceCategoryService) {
        this.jobRepository = jobRepository;
        this.jobConverter = jobConverter;
        this.jobValidator = jobValidator;
        this.locationService = locationService;
        this.serviceCategoryService = serviceCategoryService;
    }

    public JobResponse create(CreateJobRequest request) {
        jobValidator.validateCreation(request);
        Job job = jobConverter.fromCreateJobRequest(request);
        return toJobResponse(jobRepository.save(job));
    }

    private JobResponse toJobResponse(Job job) {
        Location location = locationService.findById(job.getLocationId());
        ServiceCategory serviceCategory = serviceCategoryService.findById(job.getServiceCategoryId());
        return jobConverter.toJobResponse(job, location, serviceCategory);
    }

    public Job findById(Integer jobId) {
        return jobRepository.find(jobId).orElseThrow(() -> new NotFoundException("Job not found."));
    }
}
