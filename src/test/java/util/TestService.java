package util;

import com.gambim.framework.annotation.Component;
import util.testClasses.TestRepository;

@Component
public class TestService {
    private final TestRepository testRepository;

    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }
}
