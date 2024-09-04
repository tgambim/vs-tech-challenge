package unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gambim.converter.JobConverter;
import com.gambim.converter.LocationConverter;
import com.gambim.converter.ServiceCategoryConverter;
import com.gambim.domain.Job;
import com.gambim.domain.Location;
import com.gambim.domain.ServiceCategory;
import com.gambim.dto.CreateJobRequest;
import com.gambim.dto.JobResponse;
import com.gambim.framework.exception.NotFoundException;
import com.gambim.framework.exception.ValidationException;
import com.gambim.repository.JobRepository;
import com.gambim.service.JobService;
import com.gambim.service.LocationService;
import com.gambim.service.ServiceCategoryService;
import com.gambim.validation.JobValidator;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import util.JobTestUtil;
import util.ServiceCategoryTestUtil;
import util.testClasses.LocationTestUtil;

class JobServiceTest {

    JobRepository mockedJobRepository = mock(JobRepository.class);
    JobValidator mockedJobValidator = mock(JobValidator.class);
    ServiceCategoryService mockedServiceCategoryService = mock(ServiceCategoryService.class);
    LocationService mockedLocationService = mock(LocationService.class);
    JobConverter jobConverter = new JobConverter(new ServiceCategoryConverter(), new LocationConverter());
    JobService jobService = new JobService(
            mockedJobRepository, jobConverter, mockedJobValidator, mockedLocationService, mockedServiceCategoryService);

    @Test
    void testCreate() {
        Location location = LocationTestUtil.createLocation(1);
        ServiceCategory serviceCategory = ServiceCategoryTestUtil.createServiceCategory(1);
        CreateJobRequest createJobRequest = new CreateJobRequest("test job", serviceCategory.getId(), location.getId());
        final Job createdJob = jobConverter.fromCreateJobRequest(createJobRequest);
        createdJob.setId(1);
        when(mockedJobRepository.save(any())).thenReturn(createdJob);
        when(mockedLocationService.findById(createJobRequest.locationId())).thenReturn(location);
        when(mockedServiceCategoryService.findById(createJobRequest.serviceCategoryId()))
                .thenReturn(serviceCategory);

        JobResponse response = assertDoesNotThrow(() -> jobService.create(createJobRequest));

        verify(mockedJobValidator, times(1)).validateCreation(createJobRequest);
        assertEquals(createJobRequest.description(), response.description());
        assertEquals(createdJob.getId(), response.id());
    }

    @Test
    void testCreateWithValidationFailure() {
        CreateJobRequest createJobRequest = new CreateJobRequest(null, null, null);
        Exception expectedException = new ValidationException("error", Map.of());
        doThrow(expectedException).when(mockedJobValidator).validateCreation(createJobRequest);

        Exception exception = assertThrows(ValidationException.class, () -> jobService.create(createJobRequest));

        assertEquals(expectedException, exception);
    }

    @Test
    void testFindById() {
        final Integer idToFind = 1;
        final Job expectedJob = JobTestUtil.createJob(idToFind);
        when(mockedJobRepository.find(idToFind)).thenReturn(Optional.of(expectedJob));

        Job resultJob = assertDoesNotThrow(() -> jobService.findById(idToFind));

        assertNotNull(resultJob);
        assertEquals(expectedJob, resultJob);
    }

    @Test
    void testFindByIdFailure() {
        final Integer idToFind = 2;
        final String expectedErrorMessage = "Job not found.";
        when(mockedJobRepository.find(idToFind)).thenReturn(Optional.empty());

        NotFoundException resultException = assertThrows(NotFoundException.class, () -> jobService.findById(idToFind));

        assertNotNull(resultException);
        assertEquals(expectedErrorMessage, resultException.getMessage());
    }
}
