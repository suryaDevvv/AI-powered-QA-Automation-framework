package tests.support;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class FailureArtifactsListener implements IInvokedMethodListener {
    private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        if (!method.isTestMethod() || result.getStatus() != ITestResult.FAILURE) return;

        String title = testTitle(result);
        String failure = failureDetails(result.getThrowable());
        String screenshot = captureScreenshot(title);
        String apiEvidence = ApiEvidence.lastCall().orElse("No REST Assured response was captured.");
        String aiSummary = "AI triage is disabled. Set OPENAI_ENABLED=true and OPENAI_API_KEY locally to request a summary.";
        if (TestConfig.openAiEnabled()) {
            try {
                aiSummary = AiFailureAnalyzer.analyze(title, failure, apiEvidence);
            } catch (Exception exception) {
                aiSummary = "AI triage could not run: " + exception.getClass().getSimpleName() + ": " + exception.getMessage();
            }
        }
        String report = report(title, failure, screenshot, apiEvidence, aiSummary);
        Allure.addAttachment("Failure bug report", "text/markdown",
                new ByteArrayInputStream(report.getBytes(StandardCharsets.UTF_8)), ".md");
        writeReport(title, report);
        ApiEvidence.clear();
    }

    private String captureScreenshot(String title) {
        Optional<WebDriver> driver = DriverManager.currentDriver();
        if (driver.isEmpty() || !(driver.get() instanceof TakesScreenshot screenshotDriver)) {
            return "Not available (no active UI browser)";
        }
        try {
            byte[] bytes = screenshotDriver.getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Failure screenshot - " + title, "image/png",
                    new ByteArrayInputStream(bytes), ".png");
            Path folder = Path.of("target", "bug-reports");
            Files.createDirectories(folder);
            Path image = folder.resolve(fileName(title) + ".png");
            Files.write(image, bytes);
            return image.toString();
        } catch (Exception exception) {
            return "Screenshot capture failed: " + exception.getMessage();
        }
    }

    private void writeReport(String title, String report) {
        try {
            Path folder = Path.of("target", "bug-reports");
            Files.createDirectories(folder);
            Files.writeString(folder.resolve(fileName(title) + ".md"), report, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            Allure.addAttachment("Bug report write error", "Could not write target/bug-reports report.");
        }
    }

    private String report(String title, String failure, String screenshot, String apiEvidence, String aiSummary) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String browserUrl = DriverManager.currentDriver().map(driver -> safe(driver.getCurrentUrl())).orElse("Not available");
        return "# Automated failure report\n\n"
                + "- Test case: " + title + "\n"
                + "- Time: " + timestamp + "\n"
                + "- Browser URL: " + browserUrl + "\n"
                + "- Screenshot: " + screenshot + "\n\n"
                + "## Failure\n\n<pre>\n" + failure + "\n</pre>\n\n"
                + "## Last API response\n\n<pre>\n" + apiEvidence + "\n</pre>\n\n"
                + "## AI triage\n\n" + aiSummary + "\n";
    }

    private String testTitle(ITestResult result) {
        String description = result.getMethod().getDescription();
        return description == null || description.isBlank() ? result.getName() : description;
    }

    private String failureDetails(Throwable throwable) {
        if (throwable == null) return "Test failed without a throwable detail.";
        StringBuilder text = new StringBuilder(throwable.getClass().getSimpleName())
                .append(": ").append(throwable.getMessage());
        for (StackTraceElement element : throwable.getStackTrace()) {
            text.append("\n  at ").append(element);
            if (text.length() > 6000) return text.substring(0, 6000) + "\n... [truncated]";
        }
        return text.toString();
    }

    private String fileName(String title) {
        return title.replaceAll("[^A-Za-z0-9._-]+", "_") + "-"
                + LocalDateTime.now().format(FILE_TIME);
    }

    private String safe(String value) {
        return value.replaceAll("(?i)(password|card-number|email)=([^&]+)", "$1=[REDACTED]");
    }
}
