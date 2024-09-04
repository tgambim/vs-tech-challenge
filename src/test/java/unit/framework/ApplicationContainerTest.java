package unit.framework;

import static org.junit.jupiter.api.Assertions.*;

import com.gambim.framework.annotation.Controller;
import com.gambim.framework.exception.InternalException;
import com.gambim.framework.container.ApplicationContainer;
import com.gambim.framework.security.AuthenticationService;
import com.gambim.service.BasicAuthenticationService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.TestService;
import util.testClasses.CircularDependencyClass;
import util.testClasses.ClassWithUnsatisfiedDependency;
import util.testClasses.TestController;
import util.testClasses.TestRepository;

class ApplicationContainerTest {
    static ApplicationContainer applicationContainer = new ApplicationContainer();

    @BeforeAll
    static void beforeAll() {
        applicationContainer.registerAll(Set.of(TestController.class));
    }

    @Test
    void testGetInstance() {
        TestController testController = assertDoesNotThrow(() -> applicationContainer.getInstance(TestController.class));

        assertNotNull(testController);
    }

    @Test
    void testGetInstanceWithNonRegisteredClass() {
        ApplicationContainerTest testClass = assertDoesNotThrow(() -> applicationContainer.getInstance(ApplicationContainerTest.class));

        assertNull(testClass);
    }

    @Test
    void testInitWithAlreadyRegisteredClass() {
        TestController oldController = applicationContainer.getInstance(TestController.class);

        assertDoesNotThrow(() -> applicationContainer.registerAll(Set.of(TestController.class)));

        assertEquals(oldController, applicationContainer.getInstance(TestController.class));
    }

    @Test
    void testRegisterAllWithDependency() {
        assertDoesNotThrow(() -> applicationContainer.registerAll(Set.of(TestService.class, TestRepository.class)));

        assertNotNull(applicationContainer.getInstance(TestService.class));
        assertNotNull(applicationContainer.getInstance(TestRepository.class));
    }

    @Test
    void testRegisterAllWithCircularDependency() {
        InternalException resultException = assertThrows(
                InternalException.class, () -> applicationContainer.registerAll(Set.of(CircularDependencyClass.class)));

        assertEquals("You may have a circular dependency.", resultException.getMessage());
    }

    @Test
    void testRegisterAllWithUnsatisfiedDependency() {
        InternalException resultException = assertThrows(
                InternalException.class, () -> applicationContainer.registerAll(Set.of(ClassWithUnsatisfiedDependency.class)));

        assertEquals(
                "Unsatisfied dependency: could not find an instance of java.time.LocalDate",
                resultException.getMessage());
    }

    @Test
    void testRegisterInstance() {
        LocalDateTime localDateTime = LocalDateTime.now();
        assertDoesNotThrow(() -> applicationContainer.registerInstance(localDateTime));

        assertEquals(localDateTime, applicationContainer.getInstance(LocalDateTime.class));
    }

    @Test
    void testGetInstancesThatImplements() {
        BasicAuthenticationService basicAuthenticationService = new BasicAuthenticationService();
        assertDoesNotThrow(() -> applicationContainer.registerInstance(basicAuthenticationService));

        List<AuthenticationService> services = applicationContainer.getInstancesThatImplements(AuthenticationService.class);

        assertEquals(1, services.size());
        assertEquals(basicAuthenticationService, services.get(0));
    }

    @Test
    void testGetInstancesByAnnotation() {
        List<Object> controllers = applicationContainer.getInstancesByAnnotation(Controller.class);

        assertEquals(1, controllers.size());
        assertInstanceOf(TestController.class, controllers.get(0));
    }
}
