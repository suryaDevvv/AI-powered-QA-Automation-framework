package tests.support;

import io.qameta.allure.Allure;
import io.restassured.response.Response;

import java.util.Optional;

public final class ApiEvidence {
    private static final ThreadLocal<String> LAST_CALL = new ThreadLocal<>();
    private ApiEvidence() {}

    public static void record(String method, String path, Response response) {
        String body = response.asString().replaceAll(
                "(?i)(\"(?:password|email|mobile_number)\"\\s*:\\s*\")[^\"]*(\")", "$1[REDACTED]$2");
        String evidence = method + " " + path + "\nHTTP " + response.statusCode()
                + "\n" + (body.length() > 5000 ? body.substring(0, 5000) + " ... [truncated]" : body);
        LAST_CALL.set(evidence);
        Allure.addAttachment("API response " + method + " " + path, "text/plain", evidence);
    }

    public static Optional<String> lastCall() { return Optional.ofNullable(LAST_CALL.get()); }
    public static void clear() { LAST_CALL.remove(); }
}
