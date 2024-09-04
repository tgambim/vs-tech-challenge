package integration;

import static org.junit.jupiter.api.Assertions.*;
import static util.HttpUtil.doRequest;
import static util.HttpUtil.get;
import static util.HttpUtil.post;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gambim.dto.CreateJobRequest;
import com.gambim.dto.CreateVendorRequest;
import com.gambim.dto.VendorResponse;
import com.gambim.dto.VendorsCountResponse;
import com.gambim.framework.HttpApplication;
import com.gambim.framework.routing.ErrorResponse;
import com.gambim.service.JobService;
import com.gambim.service.VendorService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class VendorTest {

    static final HttpApplication application = new HttpApplication();
    static final VendorService vendorService = application.getContainer().getInstance(VendorService.class);
    static final JobService jobService = application.getContainer().getInstance(JobService.class);
    static final ObjectMapper objectMapper = application.getContainer().getInstance(ObjectMapper.class);

    final Map<String, String> authHeader =
            Map.of("Authorization", "Basic dnNfdGVjaF9jaGFsbGVuZ2U6U3VwZXJTZWN1cmVQYXNzd29yZDEyM0A=");

    @BeforeAll
    static void beforeAll() {
        application.startHttpServer();
        seedVendors();
        seedJobs();
    }

    private static void seedVendors() {
        List.of(
                        new CreateVendorRequest("vendor 1", 2, Map.of(2, true)),
                        new CreateVendorRequest("vendor 2", 2, Map.of(2, false)))
                .forEach(vendorService::create);
    }

    private static void seedJobs() {
        List.of(new CreateJobRequest("Job 1", 2, 2), new CreateJobRequest("Job 2", 1, 1))
                .forEach(jobService::create);
    }

    @AfterAll
    static void afterAll() {
        application.stopHttpServer();
    }

    @Test
    void testGetVendorsCount() throws URISyntaxException, IOException, InterruptedException {
        String url = "/api/vendors/count?locationId=2&serviceCategoryId=2";

        VendorsCountResponse response = doRequest(get(url), VendorsCountResponse.class);

        assertEquals(2, response.totalCount());
        assertEquals(1, response.compliantCount());
        assertEquals(1, response.notCompliantCount());
    }

    @Test
    void testGetVendorsCountWithNoVendors() throws URISyntaxException, IOException, InterruptedException {
        String url = "/api/vendors/count?locationId=1&serviceCategoryId=1";

        VendorsCountResponse response = doRequest(get(url), VendorsCountResponse.class);

        assertEquals(0, response.totalCount());
        assertEquals(0, response.compliantCount());
        assertEquals(0, response.notCompliantCount());
    }

    @Test
    void testListVendors() throws URISyntaxException, IOException, InterruptedException {
        String url = "/api/vendors?jobId=1";

        HttpResponse<String> jsonResponse = doRequest(get(url, authHeader));

        assertEquals(200, jsonResponse.statusCode());
        List<VendorResponse> response =
                objectMapper.readValue(jsonResponse.body(), new TypeReference<List<VendorResponse>>() {});
        assertEquals(2, response.size());
        assertEquals(List.of(1, 2), response.stream().map(VendorResponse::id).toList());
    }

    @Test
    void testListVendorsWithNoVendors() throws URISyntaxException, IOException, InterruptedException {
        String url = "/api/vendors?jobId=2";

        HttpResponse<String> jsonResponse = doRequest(get(url, authHeader));

        assertEquals(200, jsonResponse.statusCode());
        List<VendorResponse> response =
                objectMapper.readValue(jsonResponse.body(), new TypeReference<List<VendorResponse>>() {});
        assertEquals(0, response.size());
    }

    @Test
    void testListVendorsUnauthenticated() throws URISyntaxException, IOException, InterruptedException {
        String url = "/api/vendors?jobId=2";

        HttpResponse<String> jsonResponse = doRequest(get(url));

        assertEquals(401, jsonResponse.statusCode());
    }

    @Test
    void testCreateVendor() throws URISyntaxException, IOException, InterruptedException {
        CreateVendorRequest request = new CreateVendorRequest("created vendor", 3, Map.of(3, true));
        String url = "/api/vendors";

        HttpResponse<String> jsonResponse = doRequest(post(url, request, authHeader));

        assertEquals(201, jsonResponse.statusCode());
        VendorResponse response = objectMapper.readValue(jsonResponse.body(), VendorResponse.class);
        assertNotNull(response.id());
        assertEquals(request.name(), response.name());
    }

    @Test
    void testCreateVendorWithInvalidBody() throws URISyntaxException, IOException, InterruptedException {
        CreateVendorRequest request = new CreateVendorRequest(null, null, Map.of(-1, false));
        String url = "/api/vendors";

        HttpResponse<String> jsonResponse = doRequest(post(url, request, authHeader));

        assertEquals(422, jsonResponse.statusCode());
        ErrorResponse response = objectMapper.readValue(jsonResponse.body(), ErrorResponse.class);
        assertFalse(response.validationErrors().isEmpty());
        assertTrue(response.validationErrors().containsKey("name"));
        assertTrue(response.validationErrors().containsKey("locationId"));
        assertTrue(response.validationErrors().containsKey("serviceCategoryId"));
    }

    @Test
    void testCreateVendorUnauthenticated() throws URISyntaxException, IOException, InterruptedException {
        CreateVendorRequest request = new CreateVendorRequest("created vendor", 3, Map.of(3, true));
        String url = "/api/vendors";

        HttpResponse<String> response = doRequest(post(url, request));

        assertEquals(401, response.statusCode());
    }
}
