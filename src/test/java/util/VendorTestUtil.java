package util;

import com.gambim.domain.Vendor;
import java.util.List;
import java.util.Map;

public class VendorTestUtil {
    public static Vendor createVendor(Integer id, Integer locationId, Map<Integer, Boolean> servicesMap) {
        Vendor vendor = new Vendor();
        vendor.setId(id);
        vendor.setName(String.format("test vendor %d", id));
        vendor.setServicesMap(servicesMap);
        vendor.setLocationId(locationId);
        return vendor;
    }

    public static Vendor createVendor(Integer id, boolean compliant, Integer locationId, Integer serviceCategoryId) {
        return createVendor(id, locationId, Map.of(serviceCategoryId, compliant));
    }

    public static List<Vendor> buildVendorsList(Integer locationId, Integer serviceCategoryId) {
        return List.of(
                createVendor(1, true, locationId, serviceCategoryId),
                createVendor(2, false, locationId, serviceCategoryId),
                createVendor(3, true, locationId, serviceCategoryId),
                createVendor(4, false, 2, serviceCategoryId),
                createVendor(5, false, locationId, 2),
                createVendor(6, locationId, Map.of(2, false, serviceCategoryId, true)),
                createVendor(7, locationId, Map.of(2, true, serviceCategoryId, false)));
    }
}
