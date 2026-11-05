package tests.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

public final class ApiJson {
    private static final ObjectMapper JSON = new ObjectMapper();
    private ApiJson() {}

    public static JsonNode read(Response response) {
        try {
            return JSON.readTree(response.asString());
        } catch (Exception exception) {
            throw new IllegalStateException("Expected a JSON API response but received: " + response.asString(), exception);
        }
    }

    public static int responseCode(Response response) {
        return read(response).path("responseCode").asInt(-1);
    }
}
