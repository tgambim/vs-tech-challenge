package unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gambim.domain.Location;
import com.gambim.framework.exception.NotFoundException;
import com.gambim.repository.LocationRepository;
import com.gambim.service.LocationService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import util.testClasses.LocationTestUtil;

class LocationServiceTest {

    LocationRepository mockedLocationRepository = mock(LocationRepository.class);
    LocationService locationService = new LocationService(mockedLocationRepository);

    @Test
    void testFindById() {
        final Integer idToFind = 1;
        final Location expectedLocation = LocationTestUtil.createLocation(idToFind);
        when(mockedLocationRepository.find(idToFind)).thenReturn(Optional.of(expectedLocation));

        Location resultLocation = assertDoesNotThrow(() -> locationService.findById(idToFind));

        assertNotNull(resultLocation);
        assertEquals(expectedLocation, resultLocation);
    }

    @Test
    void testFindByIdFailure() {
        final Integer idToFind = 2;
        final String expectedErrorMessage = "Location not found.";
        when(mockedLocationRepository.find(idToFind)).thenReturn(Optional.empty());

        NotFoundException resultException =
                assertThrows(NotFoundException.class, () -> locationService.findById(idToFind));

        assertNotNull(resultException);
        assertEquals(expectedErrorMessage, resultException.getMessage());
    }
}
