package tests.support;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public final class TestConfig {
    private static final Properties PROPERTIES = loadProperties();

    private TestConfig() {}

    public static String baseUrl() { return value("ecommerce.base-url", "ECOMMERCE_BASE_URL"); }
    public static String apiBaseUrl() { return value("ecommerce.api-base-url", "ECOMMERCE_API_BASE_URL"); }
    public static String testPassword() { return value("test.account.password", "TEST_ACCOUNT_PASSWORD"); }
    public static boolean headless() { return Boolean.parseBoolean(value("headless", "HEADLESS")); }
    public static Duration explicitWait() {
        return Duration.ofSeconds(Long.parseLong(value("wait.timeout.seconds", "WAIT_TIMEOUT_SECONDS")));
    }
    public static Duration apiConnectTimeout() {
        return Duration.ofMillis(Long.parseLong(value("api.connect-timeout-ms", "API_CONNECT_TIMEOUT_MS")));
    }
    public static Duration apiReadTimeout() {
        return Duration.ofMillis(Long.parseLong(value("api.read-timeout-ms", "API_READ_TIMEOUT_MS")));
    }
    public static Duration apiConnectionRequestTimeout() {
        return Duration.ofMillis(Long.parseLong(value(
                "api.connection-request-timeout-ms", "API_CONNECTION_REQUEST_TIMEOUT_MS")));
    }
    public static boolean mysqlConfigured() { return !optionalValue("mysql.url", "MYSQL_URL").isBlank(); }
    public static String mysqlUrl() { return value("mysql.url", "MYSQL_URL"); }
    public static String mysqlUsername() { return optionalValue("mysql.username", "MYSQL_USER"); }
    public static String mysqlPassword() { return optionalValue("mysql.password", "MYSQL_PASSWORD"); }
    public static String mysqlHealthQuery() { return value("mysql.health-query", "MYSQL_HEALTH_QUERY"); }
    public static boolean openAiEnabled() {
        return Boolean.parseBoolean(value("openai.enabled", "OPENAI_ENABLED"));
    }
    public static String openAiBaseUrl() { return value("openai.base-url", "OPENAI_BASE_URL"); }
    public static String openAiModel() { return value("openai.model", "OPENAI_MODEL"); }
    public static String openAiApiKey() { return optionalValue("openai.api-key", "OPENAI_API_KEY"); }
    public static Duration openAiTimeout() {
        return Duration.ofSeconds(Long.parseLong(value("openai.timeout-seconds", "OPENAI_TIMEOUT_SECONDS")));
    }

    private static String value(String propertyName, String environmentName) {
        String result = optionalValue(propertyName, environmentName);
        if (result.isBlank()) {
            throw new IllegalStateException("Missing configuration value: " + propertyName);
        }
        return result;
    }

    private static String optionalValue(String propertyName, String environmentName) {
        String systemValue = System.getProperty(propertyName);
        if (systemValue != null && !systemValue.isBlank()) return systemValue.trim();
        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.isBlank()) return environmentValue.trim();
        return PROPERTIES.getProperty(propertyName, "").trim();
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) throw new IllegalStateException("config.properties was not found on the test classpath");
            properties.load(input);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load config.properties", exception);
        }
    }
}
