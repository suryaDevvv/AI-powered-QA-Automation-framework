package tests.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AiFailureAnalyzer {
    private static final ObjectMapper JSON = new ObjectMapper();
    private AiFailureAnalyzer() {}

    public static String analyze(String testName, String failure, String apiEvidence) throws Exception {
        String apiKey = TestConfig.openAiApiKey();
        if (apiKey.isBlank()) {
            return "AI analysis was enabled but OPENAI_API_KEY is not set; the structured failure report is available.";
        }
        String prompt = "Review this automated QA failure. Give a concise likely cause, evidence, and next diagnostic step. "
                + "Do not claim certainty. Do not repeat credentials or personal data.\n\nTest: " + testName
                + "\nFailure: " + redact(failure) + "\nAPI evidence: " + redact(apiEvidence);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", TestConfig.openAiModel());
        body.put("input", prompt);
        body.put("max_output_tokens", 350);
        String baseUrl = TestConfig.openAiBaseUrl().replaceAll("/+$", "");
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/responses"))
                .timeout(TestConfig.openAiTimeout())
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)))
                .build();
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            return "AI analysis request failed with HTTP " + response.statusCode() + ". Structured report remains available.";
        }
        return extractText(response.body());
    }

    private static String extractText(String responseJson) throws Exception {
        JsonNode root = JSON.readTree(responseJson);
        if (root.hasNonNull("output_text")) return root.get("output_text").asText();
        StringBuilder text = new StringBuilder();
        for (JsonNode output : root.path("output")) {
            for (JsonNode content : output.path("content")) {
                if (content.hasNonNull("text")) {
                    if (!text.isEmpty()) text.append("\n");
                    text.append(content.get("text").asText());
                }
            }
        }
        return text.isEmpty() ? "AI response did not contain readable text." : text.toString();
    }

    private static String redact(String value) {
        return value.replaceAll("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}", "[EMAIL]")
                .replaceAll("(?i)(password\\s*[:=]\\s*)[^\\s,]+", "$1[REDACTED]");
    }
}
