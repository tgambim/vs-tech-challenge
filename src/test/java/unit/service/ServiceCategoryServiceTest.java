package unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gambim.domain.ServiceCategory;
import com.gambim.framework.exception.NotFoundException;
import com.gambim.repository.ServiceCategoryRepository;
import com.gambim.service.ServiceCategoryService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import util.ServiceCategoryTestUtil;

class ServiceCategoryServiceTest {

    ServiceCategoryRepository mockedServiceCategoryRepository = mock(ServiceCategoryRepository.class);
    ServiceCategoryService serviceCategoryService = new ServiceCategoryService(mockedServiceCategoryRepository);

    @Test
    void testFindById() {
        final Integer idToFind = 1;
        final ServiceCategory expectedServiceCategory = ServiceCategoryTestUtil.createServiceCategory(idToFind);
        when(mockedServiceCategoryRepository.find(idToFind)).thenReturn(Optional.of(expectedServiceCategory));

        ServiceCategory resultServiceCategory = assertDoesNotThrow(() -> serviceCategoryService.findById(idToFind));

        assertNotNull(resultServiceCategory);
        assertEquals(expectedServiceCategory, resultServiceCategory);
    }

    @Test
    void testFindByIdFailure() {
        final Integer idToFind = 2;
        final String expectedErrorMessage = "Service category not found.";
        when(mockedServiceCategoryRepository.find(idToFind)).thenReturn(Optional.empty());

        NotFoundException resultException =
                assertThrows(NotFoundException.class, () -> serviceCategoryService.findById(idToFind));

        assertNotNull(resultException);
        assertEquals(expectedErrorMessage, resultException.getMessage());
    }
}
