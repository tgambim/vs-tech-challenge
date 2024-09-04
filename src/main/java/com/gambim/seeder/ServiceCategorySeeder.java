package com.gambim.seeder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gambim.domain.ServiceCategory;
import com.gambim.framework.annotation.Component;
import com.gambim.framework.data.FileSeeder;
import com.gambim.repository.ServiceCategoryRepository;
import java.io.IOException;

@Component
public class ServiceCategorySeeder extends FileSeeder<ServiceCategory> {
    public ServiceCategorySeeder(ServiceCategoryRepository serviceCategoryRepository, ObjectMapper objectMapper)
            throws IOException {
        super("seed/service-categories.json", ServiceCategory.class, objectMapper, serviceCategoryRepository);
    }
}
