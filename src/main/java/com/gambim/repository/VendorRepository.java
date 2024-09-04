package com.gambim.repository;

import com.gambim.domain.Vendor;
import com.gambim.framework.annotation.Component;
import com.gambim.framework.data.InMemoryRepository;

@Component
public class VendorRepository extends InMemoryRepository<Vendor> {}
