package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class HttpUtil {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private static final String BASE_URL = "http://localhost:8000";

    public static HttpRequest get(String url) throws URISyntaxException {
        return get(url, Map.of());
    }

    public static HttpRequest get(String url, Map<String, String> headers) throws URISyntaxException {
        return with(url, headers).GET().build();
    }

    public static HttpRequest post(String url, Object body) throws URISyntaxException, JsonProcessingException {
        return post(url, body, Map.of());
    }

    public static HttpRequest post(String url, Object body, Map<String, String> headers)
            throws URISyntaxException, JsonProcessingException {

        return with(url, headers)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                .build();
    }

    private static HttpRequest.Builder with(String url, Map<String, String> headers) throws URISyntaxException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(new URI(BASE_URL + url));
        headers.forEach(builder::header);
        return builder;
    }

    public static <T> T doRequest(HttpRequest request, Class<T> clazz) throws IOException, InterruptedException {

        return objectMapper.readValue(doRequest(request).body(), clazz);
    }

    public static HttpResponse<String> doRequest(HttpRequest request) throws IOException, InterruptedException {
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
