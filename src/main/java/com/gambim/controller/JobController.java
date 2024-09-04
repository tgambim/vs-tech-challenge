package com.gambim.controller;

import com.gambim.dto.CreateJobRequest;
import com.gambim.dto.JobResponse;
import com.gambim.framework.annotation.*;
import com.gambim.framework.routing.RequestMethod;
import com.gambim.service.JobService;

@Controller(basePath = "/api/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Authenticated
    @ResponseStatus(201)
    public JobResponse createJob(@Body CreateJobRequest request) {
        return jobService.create(request);
    }
}
