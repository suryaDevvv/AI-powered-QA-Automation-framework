package tests.support;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;

import java.util.List;

public final class AdPopupHandler {
    private static final String CLOSE_SCRIPT = """
            const candidates = [...document.querySelectorAll('button,a,[role="button"],[aria-label],span,div')];
            const close = candidates.find(el => el.childElementCount === 0
              && ((el.innerText || el.textContent || el.getAttribute('aria-label') || '').trim().toLowerCase() === 'close'));
            if (close) { close.click(); return true; }
            return false;
            """;

    private AdPopupHandler() {}

    public static void dismissIfPresent(WebDriver driver) {
        try {
            driver.switchTo().defaultContent();
            if (closeInCurrentContext(driver)) return;
            List<WebElement> frames = driver.findElements(By.tagName("iframe"));
            for (WebElement frame : frames) {
                try {
                    driver.switchTo().defaultContent();
                    driver.switchTo().frame(frame);
                    if (closeInCurrentContext(driver)) return;
                } catch (WebDriverException ignored) {
                    // Some third-party frames are unavailable or replaced during page load.
                } finally {
                    try {
                        driver.switchTo().defaultContent();
                    } catch (WebDriverException ignored) {
                        // The next browser operation will report a closed or invalid session.
                    }
                }
            }
        } catch (WebDriverException ignored) {
            // Ad dismissal is best-effort; normal page waits still determine test outcomes.
        }
    }

    private static boolean closeInCurrentContext(WebDriver driver) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(CLOSE_SCRIPT));
    }
}
