package unit.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gambim.domain.ServiceCategory;
import com.gambim.repository.ServiceCategoryRepository;
import com.gambim.seeder.ServiceCategorySeeder;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ServiceCategorySeederTest {
    ServiceCategoryRepository mockedRepository = mock(ServiceCategoryRepository.class);
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testSeed() {
        List<String> serviceCategoriesToCreate = List.of(
                "Access Control Software",
                "Air Conditioning",
                "Landscaping",
                "Landscaping Maintenance",
                "Snow and Ice Removal",
                "Sewer and Water Pipelining");
        ArgumentCaptor<ServiceCategory> argument = ArgumentCaptor.forClass(ServiceCategory.class);

        ServiceCategorySeeder seeder =
                assertDoesNotThrow(() -> new ServiceCategorySeeder(mockedRepository, objectMapper));
        assertDoesNotThrow(seeder::seed);

        verify(mockedRepository, times(serviceCategoriesToCreate.size())).saveWithPredefinedId(argument.capture());
        assertEquals(
                serviceCategoriesToCreate,
                argument.getAllValues().stream().map(ServiceCategory::getName).toList());
    }
}
