package util.testClasses;

import com.gambim.domain.Location;

public class LocationTestUtil {

    public static Location createLocation(Integer id) {
        Location location = new Location();
        location.setId(id);
        location.setName(String.format("location %d", id));
        location.setState("TX");
        return location;
    }

    public static Location createLocation(String name, String state) {
        Location location = new Location();
        location.setName(name);
        location.setState(state);
        return location;
    }

    public static Location createLocation(Integer id, String name, String state) {
        Location location = createLocation(name, state);
        location.setId(id);
        return location;
    }
}
