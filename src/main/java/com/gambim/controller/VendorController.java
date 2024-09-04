package com.gambim.controller;

import com.gambim.dto.CreateVendorRequest;
import com.gambim.dto.VendorResponse;
import com.gambim.dto.VendorsCountResponse;
import com.gambim.framework.annotation.*;
import com.gambim.framework.routing.RequestMethod;
import com.gambim.service.VendorService;
import java.util.Collection;

@Controller(basePath = "/api/vendors")
public class VendorController {
    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Authenticated
    public Collection<VendorResponse> listVendors(@QueryParam(name = "jobId") Integer jobId) {
        return vendorService.list(jobId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/count")
    public VendorsCountResponse getVendorsCount(
            @QueryParam(name = "locationId") Integer locationId,
            @QueryParam(name = "serviceCategoryId") Integer serviceCategoryId) {
        return vendorService.getCount(locationId, serviceCategoryId);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Authenticated
    @ResponseStatus(201)
    public VendorResponse createVendor(@Body CreateVendorRequest request) {
        return vendorService.create(request);
    }
}
