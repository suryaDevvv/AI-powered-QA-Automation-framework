package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import tests.support.TestConfig;
import tests.support.AdPopupHandler;

import java.util.List;

abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TestConfig.explicitWait());
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        AdPopupHandler.dismissIfPresent(driver);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        } catch (ElementClickInterceptedException exception) {
            AdPopupHandler.dismissIfPresent(driver);
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        }
    }

    protected void type(By locator, String value) {
        WebElement element = visible(locator);
        element.clear();
        element.sendKeys(value);
    }

    protected String text(By locator) {
        return visible(locator).getText().trim();
    }

    protected boolean exists(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected void select(By locator, String value) {
        new Select(visible(locator)).selectByVisibleText(value);
    }

    protected List<String> texts(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return driver.findElements(locator).stream().map(element -> element.getText().trim()).toList();
    }

    protected boolean isInvalid(By locator) {
        WebElement element = visible(locator);
        return Boolean.TRUE.equals(((JavascriptExecutor) driver)
                .executeScript("return !arguments[0].checkValidity();", element));
    }

    protected String pageBody() {
        return visible(By.tagName("body")).getText();
    }
}
