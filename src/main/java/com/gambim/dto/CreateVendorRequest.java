package com.gambim.dto;

import java.util.Map;

public record CreateVendorRequest(String name, Integer locationId, Map<Integer, Boolean> services) {}
