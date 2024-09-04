package unit.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gambim.domain.Location;
import com.gambim.domain.ServiceCategory;
import com.gambim.dto.CreateVendorRequest;
import com.gambim.framework.exception.NotFoundException;
import com.gambim.framework.exception.ValidationException;
import com.gambim.service.LocationService;
import com.gambim.service.ServiceCategoryService;
import com.gambim.validation.VendorValidator;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class VendorValidatorTest {

    LocationService mockedLocationService = mock(LocationService.class);
    ServiceCategoryService mockedServiceCategoryService = mock(ServiceCategoryService.class);
    VendorValidator vendorValidator = new VendorValidator(mockedLocationService, mockedServiceCategoryService);

    @Test
    void testValidateCreation() {
        CreateVendorRequest request = new CreateVendorRequest("test", 1, Map.of(1, true));
        when(mockedLocationService.findById(request.locationId())).thenReturn(new Location());
        when(mockedServiceCategoryService.findById(1)).thenReturn(new ServiceCategory());

        assertDoesNotThrow(() -> vendorValidator.validateCreation(request));
    }

    @ParameterizedTest
    @CsvSource(value = {",-1,,false", "'',-1,-1,false"})
    void testValidateCreationWithInvalidInput(
            String description, Integer serviceCategoryId, Integer locationId, Boolean compliant) {
        CreateVendorRequest request =
                new CreateVendorRequest(description, locationId, Map.of(serviceCategoryId, compliant));
        when(mockedLocationService.findById(request.locationId()))
                .thenThrow(new NotFoundException("Location not found."));
        when(mockedServiceCategoryService.findById(serviceCategoryId))
                .thenThrow(new NotFoundException("Service category not found."));

        ValidationException resultException =
                assertThrows(ValidationException.class, () -> vendorValidator.validateCreation(request));

        assertEquals("The vendor contains errors and cannot not be created.", resultException.getMessage());
        assertEquals(422, resultException.getStatusCode());
        assertTrue(resultException.getValidationErrors().containsKey("locationId"));
        assertTrue(resultException.getValidationErrors().containsKey("serviceCategoryId"));
        assertTrue(resultException.getValidationErrors().containsKey("name"));
        assertEquals(
                "Location not found.", resultException.getValidationErrors().get("locationId"));
        assertEquals(
                "Service category not found.",
                resultException.getValidationErrors().get("serviceCategoryId"));
        assertEquals(
                "Name cannot be blank.", resultException.getValidationErrors().get("name"));
    }

    @Test
    void testValidateCreationWithOnlyOneInvalidField() {
        CreateVendorRequest request = new CreateVendorRequest("", 1, Map.of(1, true));
        when(mockedLocationService.findById(request.locationId())).thenReturn(new Location());
        when(mockedServiceCategoryService.findById(1)).thenReturn(new ServiceCategory());

        ValidationException resultException =
                assertThrows(ValidationException.class, () -> vendorValidator.validateCreation(request));

        assertTrue(resultException.getValidationErrors().containsKey("name"));
        assertFalse(resultException.getValidationErrors().containsKey("serviceCategoryId"));
        assertFalse(resultException.getValidationErrors().containsKey("locationId"));
        assertFalse(resultException.getValidationErrors().containsKey("compliant"));
    }
}
