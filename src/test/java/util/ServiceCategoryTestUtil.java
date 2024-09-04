package util;

import com.gambim.domain.ServiceCategory;

public class ServiceCategoryTestUtil {

    public static ServiceCategory createServiceCategory(Integer id) {
        return createServiceCategory(id, String.format("service category %s", id));
    }

    public static ServiceCategory createServiceCategory(Integer id, String name) {
        ServiceCategory serviceCategory = new ServiceCategory();
        serviceCategory.setId(id);
        serviceCategory.setName(name);
        return serviceCategory;
    }
}
