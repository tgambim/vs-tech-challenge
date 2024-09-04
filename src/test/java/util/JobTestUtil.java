package util;

import com.gambim.domain.Job;

public class JobTestUtil {

    public static Job createJob(Integer id) {
        Job job = new Job();
        job.setId(id);
        job.setDescription("test job");
        job.setServiceCategoryId(1);
        job.setLocationId(1);
        return job;
    }
}
