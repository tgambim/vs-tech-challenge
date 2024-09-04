package unit.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.gambim.domain.Location;
import com.gambim.framework.data.InMemoryRepository;
import com.gambim.framework.exception.InternalException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.testClasses.LocationTestUtil;

class InMemoryRepositoryTest {

    InMemoryRepository<Location> inMemoryRepository;

    @BeforeEach
    void beforeEach() {
        inMemoryRepository = new InMemoryRepository<>();
        IntStream.range(0, 10).mapToObj(LocationTestUtil::createLocation).forEach(inMemoryRepository::save);
    }

    @Test
    void testSaveWithPredefinedId() {
        Location location = LocationTestUtil.createLocation(11, "test location", "TX");

        Location savedLocation = inMemoryRepository.saveWithPredefinedId(location);

        assertNotNull(savedLocation.getId());
        assertEquals(location.getId(), savedLocation.getId());
        assertEquals(location.getName(), savedLocation.getName());
        assertEquals(location.getState(), savedLocation.getState());
    }

    @Test
    void testSaveWithPredefinedIdThatIsDuplicated() {
        Location location = LocationTestUtil.createLocation(1, "test location", "TX");

        InternalException resultException =
                assertThrows(InternalException.class, () -> inMemoryRepository.saveWithPredefinedId(location));

        assertEquals("Entity id already exists.", resultException.getMessage());
    }

    @Test
    void testSaveWithPredefinedIdThatIsNull() {
        Location location = LocationTestUtil.createLocation("test location", "TX");

        InternalException resultException =
                assertThrows(InternalException.class, () -> inMemoryRepository.saveWithPredefinedId(location));

        assertEquals("You have to inform de entity id.", resultException.getMessage());
    }

    @Test
    void testSave() {
        Location location = LocationTestUtil.createLocation("test location", "TX");

        Location savedLocation = inMemoryRepository.save(location);

        assertNotNull(savedLocation.getId());
        assertEquals(location.getName(), savedLocation.getName());
        assertEquals(location.getState(), savedLocation.getState());
    }

    @Test
    void testList() {
        Collection<Location> locations = inMemoryRepository.list();

        assertEquals(10, locations.size());

        OptionalInt min = locations.stream().mapToInt(Location::getId).min();
        OptionalInt max = locations.stream().mapToInt(Location::getId).max();
        Set<Integer> uniqueIds = locations.stream().map(Location::getId).collect(Collectors.toSet());
        assertEquals(10, uniqueIds.size());
        assertTrue(min.isPresent());
        assertTrue(max.isPresent());
        assertEquals(1, min.getAsInt());
        assertEquals(10, max.getAsInt());
    }

    @Test
    void testFind() {
        Optional<Location> location = inMemoryRepository.find(1);

        assertTrue(location.isPresent());
        assertEquals(1, location.get().getId());
        assertEquals("TX", location.get().getState());
        assertEquals("location 0", location.get().getName());
    }

    @Test
    void testFindWithInvalidId() {
        Optional<Location> location = inMemoryRepository.find(11);

        assertFalse(location.isPresent());
    }
}
