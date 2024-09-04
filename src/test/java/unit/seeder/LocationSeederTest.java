package unit.seeder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gambim.domain.Location;
import com.gambim.repository.LocationRepository;
import com.gambim.seeder.LocationSeeder;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class LocationSeederTest {
    LocationRepository mockedRepository = mock(LocationRepository.class);
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testSeed() {
        List<String> serviceCategoriesToCreate = List.of(
                "Glades",
                "Gulf",
                "Hamilton",
                "Hardee",
                "Hendry",
                "El Paso",
                "Erath",
                "Falls",
                "Fannin",
                "Fayette",
                "Fisher");
        ArgumentCaptor<Location> argument = ArgumentCaptor.forClass(Location.class);

        LocationSeeder seeder = assertDoesNotThrow(() -> new LocationSeeder(mockedRepository, objectMapper));
        assertDoesNotThrow(seeder::seed);

        verify(mockedRepository, times(serviceCategoriesToCreate.size())).saveWithPredefinedId(argument.capture());
        assertEquals(
                serviceCategoriesToCreate,
                argument.getAllValues().stream().map(Location::getName).toList());
    }
}
