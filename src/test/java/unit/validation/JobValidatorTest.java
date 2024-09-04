package unit.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gambim.domain.Location;
import com.gambim.domain.ServiceCategory;
import com.gambim.dto.CreateJobRequest;
import com.gambim.framework.exception.NotFoundException;
import com.gambim.framework.exception.ValidationException;
import com.gambim.service.LocationService;
import com.gambim.service.ServiceCategoryService;
import com.gambim.validation.JobValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class JobValidatorTest {

    LocationService mockedLocationService = mock(LocationService.class);
    ServiceCategoryService mockedServiceCategoryService = mock(ServiceCategoryService.class);
    JobValidator jobValidator = new JobValidator(mockedLocationService, mockedServiceCategoryService);

    @Test
    void testValidateCreation() {
        CreateJobRequest request = new CreateJobRequest("test", 1, 1);
        when(mockedLocationService.findById(request.locationId())).thenReturn(new Location());
        when(mockedServiceCategoryService.findById(request.serviceCategoryId())).thenReturn(new ServiceCategory());

        assertDoesNotThrow(() -> jobValidator.validateCreation(request));
    }

    @ParameterizedTest
    @CsvSource(value = {",,", "'',-1,-1"})
    void testValidateCreationWithInvalidInput(String description, Integer serviceCategoryId, Integer locationId) {
        CreateJobRequest request = new CreateJobRequest(description, serviceCategoryId, locationId);
        when(mockedLocationService.findById(request.locationId()))
                .thenThrow(new NotFoundException("Location not found."));
        when(mockedServiceCategoryService.findById(request.serviceCategoryId()))
                .thenThrow(new NotFoundException("Service category not found."));

        ValidationException resultException =
                assertThrows(ValidationException.class, () -> jobValidator.validateCreation(request));

        assertEquals("The job contains errors and cannot not be created.", resultException.getMessage());
        assertEquals(422, resultException.getStatusCode());
        assertTrue(resultException.getValidationErrors().containsKey("locationId"));
        assertTrue(resultException.getValidationErrors().containsKey("serviceCategoryId"));
        assertTrue(resultException.getValidationErrors().containsKey("description"));
        assertEquals(
                "Location not found.", resultException.getValidationErrors().get("locationId"));
        assertEquals(
                "Service category not found.",
                resultException.getValidationErrors().get("serviceCategoryId"));
        assertEquals(
                "Description cannot be blank.",
                resultException.getValidationErrors().get("description"));
    }

    @Test
    void testValidateCreationWithOnlyOneInvalidField() {
        CreateJobRequest request = new CreateJobRequest("", 1, 1);
        when(mockedLocationService.findById(request.locationId())).thenReturn(new Location());
        when(mockedServiceCategoryService.findById(request.serviceCategoryId())).thenReturn(new ServiceCategory());

        ValidationException resultException =
                assertThrows(ValidationException.class, () -> jobValidator.validateCreation(request));

        assertTrue(resultException.getValidationErrors().containsKey("description"));
        assertFalse(resultException.getValidationErrors().containsKey("serviceCategoryId"));
        assertFalse(resultException.getValidationErrors().containsKey("locationId"));
    }
}
