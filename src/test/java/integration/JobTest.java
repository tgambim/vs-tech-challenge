package integration;

import static org.junit.jupiter.api.Assertions.*;
import static util.HttpUtil.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gambim.dto.*;
import com.gambim.framework.HttpApplication;
import com.gambim.framework.routing.ErrorResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JobTest {

    static final HttpApplication application = new HttpApplication();
    static final ObjectMapper objectMapper = application.getContainer().getInstance(ObjectMapper.class);

    final Map<String, String> authHeader =
            Map.of("Authorization", "Basic dnNfdGVjaF9jaGFsbGVuZ2U6U3VwZXJTZWN1cmVQYXNzd29yZDEyM0A=");

    @BeforeAll
    static void beforeAll() {
        application.startHttpServer();
    }

    @AfterAll
    static void afterAll() {
        application.stopHttpServer();
    }

    @Test
    void testCreateJob() throws URISyntaxException, IOException, InterruptedException {
        CreateJobRequest request = new CreateJobRequest("created job", 3, 3);
        String url = "/api/jobs";

        HttpResponse<String> jsonResponse = doRequest(post(url, request, authHeader));

        assertEquals(201, jsonResponse.statusCode());
        JobResponse response = objectMapper.readValue(jsonResponse.body(), JobResponse.class);
        assertNotNull(response.id());
        assertEquals(request.description(), response.description());
    }

    @Test
    void testCreateJobWithInvalidBody() throws URISyntaxException, IOException, InterruptedException {
        CreateJobRequest request = new CreateJobRequest(null, null, null);
        String url = "/api/jobs";

        HttpResponse<String> jsonResponse = doRequest(post(url, request, authHeader));

        assertEquals(422, jsonResponse.statusCode());
        ErrorResponse response = objectMapper.readValue(jsonResponse.body(), ErrorResponse.class);
        assertFalse(response.validationErrors().isEmpty());
        assertTrue(response.validationErrors().containsKey("description"));
        assertTrue(response.validationErrors().containsKey("locationId"));
        assertTrue(response.validationErrors().containsKey("serviceCategoryId"));
    }

    @Test
    void testCreateJobUnauthenticated() throws URISyntaxException, IOException, InterruptedException {
        CreateJobRequest request = new CreateJobRequest("created job", 3, 3);
        String url = "/api/jobs";

        HttpResponse<String> jsonResponse = doRequest(post(url, request));

        assertEquals(401, jsonResponse.statusCode());
    }
}
