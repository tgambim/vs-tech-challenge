package com.gambim.repository;

import com.gambim.domain.ServiceCategory;
import com.gambim.framework.annotation.Component;
import com.gambim.framework.data.InMemoryRepository;

@Component
public class ServiceCategoryRepository extends InMemoryRepository<ServiceCategory> {}
