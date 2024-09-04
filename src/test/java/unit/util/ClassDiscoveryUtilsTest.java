package unit.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gambim.controller.VendorController;
import com.gambim.converter.VendorConverter;
import com.gambim.framework.annotation.Component;
import com.gambim.framework.annotation.Controller;
import com.gambim.framework.routing.ApiRouter;
import com.gambim.framework.routing.RequestHandler;
import com.gambim.framework.util.ClassDiscoveryUtils;
import com.gambim.repository.JobRepository;
import com.gambim.seeder.ServiceCategorySeeder;
import com.gambim.service.LocationService;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ClassDiscoveryUtilsTest {

    @Test
    void testGetProjectClasses() {
        List<Class<?>> expectedClassesToLoad = List.of(
                JobRepository.class,
                VendorController.class,
                LocationService.class,
                VendorConverter.class,
                ServiceCategorySeeder.class,
                ApiRouter.class,
                RequestHandler.class);

        Set<Class<?>> projectClasses =
                ClassDiscoveryUtils.getClassesAnnotatedWith(Set.of(Controller.class, Component.class));

        assertTrue(projectClasses.containsAll(expectedClassesToLoad));
    }
}
