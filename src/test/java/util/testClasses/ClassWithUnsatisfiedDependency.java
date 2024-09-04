package util.testClasses;

import java.time.LocalDate;

public class ClassWithUnsatisfiedDependency {
    private final LocalDate date;

    public ClassWithUnsatisfiedDependency(LocalDate date) {
        this.date = date;
    }
}
