package tests.support;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Optional;

public final class DriverManager {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private DriverManager() {}

    public static WebDriver startChrome() {
        ChromeOptions options = new ChromeOptions();
        if (TestConfig.headless()) options.addArguments("--headless=new");
        options.addArguments("--window-size=1440,1100", "--disable-dev-shm-usage", "--no-sandbox");
        WebDriver driver = new ChromeDriver(options);
        DRIVER.set(driver);
        return driver;
    }

    public static Optional<WebDriver> currentDriver() { return Optional.ofNullable(DRIVER.get()); }

    public static void stop() {
        WebDriver driver = DRIVER.get();
        try {
            if (driver != null) driver.quit();
        } finally {
            DRIVER.remove();
        }
    }
}
