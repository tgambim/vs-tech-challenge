package util.testClasses;

import com.gambim.framework.annotation.Component;

@Component
public class CircularDependencyClass {
    private final CircularDependencyClass circularDependencyClass;

    public CircularDependencyClass(CircularDependencyClass circularDependencyClass) {
        this.circularDependencyClass = circularDependencyClass;
    }
}
