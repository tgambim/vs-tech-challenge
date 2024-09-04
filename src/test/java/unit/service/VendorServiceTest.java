package unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gambim.converter.LocationConverter;
import com.gambim.converter.ServiceCategoryConverter;
import com.gambim.converter.VendorConverter;
import com.gambim.domain.Job;
import com.gambim.domain.Location;
import com.gambim.domain.ServiceCategory;
import com.gambim.domain.Vendor;
import com.gambim.dto.CreateVendorRequest;
import com.gambim.dto.VendorResponse;
import com.gambim.dto.VendorServiceCategoryResponse;
import com.gambim.dto.VendorsCountResponse;
import com.gambim.framework.exception.ValidationException;
import com.gambim.repository.VendorRepository;
import com.gambim.service.JobService;
import com.gambim.service.LocationService;
import com.gambim.service.ServiceCategoryService;
import com.gambim.service.VendorService;
import com.gambim.validation.VendorValidator;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import util.JobTestUtil;
import util.ServiceCategoryTestUtil;
import util.VendorTestUtil;
import util.testClasses.LocationTestUtil;

class VendorServiceTest {
    VendorRepository mockedVendorRepository = mock(VendorRepository.class);
    VendorValidator mockedVendorValidator = mock(VendorValidator.class);
    JobService mockedJobService = mock(JobService.class);
    LocationService mockedLocationService = mock(LocationService.class);
    ServiceCategoryService mockedServiceCategoryService = mock(ServiceCategoryService.class);
    VendorConverter vendorConverter = new VendorConverter(new ServiceCategoryConverter(), new LocationConverter());
    VendorService vendorService = new VendorService(
            mockedVendorRepository,
            vendorConverter,
            mockedVendorValidator,
            mockedJobService,
            mockedLocationService,
            mockedServiceCategoryService);

    @Test
    void testCreate() {
        Location location = LocationTestUtil.createLocation(2);
        ServiceCategory serviceCategory = ServiceCategoryTestUtil.createServiceCategory(1);
        Map<Integer, Boolean> categoriesMap = Map.of(serviceCategory.getId(), true);
        CreateVendorRequest createVendorRequest =
                new CreateVendorRequest("test vendor", location.getId(), categoriesMap);
        final Vendor createdVendor = vendorConverter.fromCreateVendorRequest(createVendorRequest);
        createdVendor.setId(1);
        when(mockedVendorRepository.save(any())).thenReturn(createdVendor);
        when(mockedLocationService.findById(createVendorRequest.locationId())).thenReturn(location);
        when(mockedServiceCategoryService.findById(serviceCategory.getId())).thenReturn(serviceCategory);

        VendorResponse response = assertDoesNotThrow(() -> vendorService.create(createVendorRequest));

        verify(mockedVendorValidator, times(1)).validateCreation(createVendorRequest);
        assertEquals(createVendorRequest.name(), response.name());
        assertEquals(createdVendor.getId(), response.id());
        assertEquals(createdVendor.getLocationId(), response.location().id());
        assertEquals(
                categoriesMap,
                response.serviceCategories().stream()
                        .collect(Collectors.toMap(
                                VendorServiceCategoryResponse::id, VendorServiceCategoryResponse::compliant)));
    }

    @Test
    void testCreateWithValidationFailure() {
        CreateVendorRequest createVendorRequest = new CreateVendorRequest(null, null, Map.of(-1, false));
        Exception expectedException = new ValidationException("error", Map.of());
        doThrow(expectedException).when(mockedVendorValidator).validateCreation(createVendorRequest);

        Exception exception = assertThrows(ValidationException.class, () -> vendorService.create(createVendorRequest));

        assertEquals(expectedException, exception);
    }

    @Test
    void testGetCount() {
        final Integer locationIdToFind = 1;
        final Integer serviceCategoryIdToFind = 1;
        final List<Vendor> vendors = VendorTestUtil.buildVendorsList(locationIdToFind, serviceCategoryIdToFind);
        when(mockedVendorRepository.list()).thenReturn(vendors);

        VendorsCountResponse response =
                assertDoesNotThrow(() -> vendorService.getCount(locationIdToFind, serviceCategoryIdToFind));

        assertNotNull(response);
        assertEquals(5, response.totalCount());
        assertEquals(3, response.compliantCount());
        assertEquals(2, response.notCompliantCount());
    }

    @Test
    void testGetCountWithNoVendors() {
        final Integer locationIdToFind = 1;
        final Integer serviceCategoryIdToFind = 1;
        final List<Vendor> vendors = List.of(
                VendorTestUtil.createVendor(1, false, 2, serviceCategoryIdToFind),
                VendorTestUtil.createVendor(2, false, locationIdToFind, 2));
        when(mockedVendorRepository.list()).thenReturn(vendors);

        VendorsCountResponse response =
                assertDoesNotThrow(() -> vendorService.getCount(locationIdToFind, serviceCategoryIdToFind));

        assertNotNull(response);
        assertEquals(0, response.totalCount());
        assertEquals(0, response.compliantCount());
        assertEquals(0, response.notCompliantCount());
    }

    @Test
    void testList() {
        final Integer jobIdToFind = 1;
        Job job = JobTestUtil.createJob(jobIdToFind);
        Location location = LocationTestUtil.createLocation(job.getLocationId());
        ServiceCategory serviceCategory = ServiceCategoryTestUtil.createServiceCategory(job.getServiceCategoryId());
        final List<Vendor> vendors = VendorTestUtil.buildVendorsList(job.getLocationId(), job.getServiceCategoryId());
        when(mockedVendorRepository.list()).thenReturn(vendors);
        when(mockedJobService.findById(jobIdToFind)).thenReturn(job);
        when(mockedLocationService.findById(job.getLocationId())).thenReturn(location);
        when(mockedServiceCategoryService.findById(job.getServiceCategoryId())).thenReturn(serviceCategory);

        Collection<VendorResponse> response = assertDoesNotThrow(() -> vendorService.list(jobIdToFind));

        assertNotNull(response);
        assertEquals(5, response.size());
        assertEquals(
                List.of(1, 3, 6, 2, 7),
                response.stream().map(VendorResponse::id).toList());
        assertTrue(response.stream()
                .flatMap(vendorResponse -> vendorResponse.serviceCategories().stream())
                .map(VendorServiceCategoryResponse::id)
                .allMatch(id -> id == 1));
    }
}
