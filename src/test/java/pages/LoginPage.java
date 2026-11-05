package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import tests.support.TestConfig;

public class LoginPage extends BasePage {
    private static final By LOGIN_EMAIL = By.cssSelector("input[data-qa='login-email']");
    private static final By LOGIN_PASSWORD = By.cssSelector("input[data-qa='login-password']");
    private static final By LOGIN_BUTTON = By.cssSelector("button[data-qa='login-button']");
    private static final By SIGNUP_NAME = By.cssSelector("input[data-qa='signup-name']");
    private static final By SIGNUP_EMAIL = By.cssSelector("input[data-qa='signup-email']");
    private static final By SIGNUP_BUTTON = By.cssSelector("button[data-qa='signup-button']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open the signup and login page")
    public void open() {
        driver.get(TestConfig.baseUrl() + "/login");
        visible(SIGNUP_EMAIL);
        visible(LOGIN_EMAIL);
    }

    @Step("Log in as {email}")
    public void login(String email, String password) {
        type(LOGIN_EMAIL, email);
        type(LOGIN_PASSWORD, password);
        click(LOGIN_BUTTON);
    }

    @Step("Submit signup email {email}")
    public void beginSignup(String name, String email) {
        type(SIGNUP_NAME, name);
        type(SIGNUP_EMAIL, email);
        click(SIGNUP_BUTTON);
    }

    @Step("Read the login validation message")
    public String loginError() {
        return text(By.cssSelector("form[action='/login'] p"));
    }

    @Step("Read the signup validation message")
    public String signupError() {
        return text(By.cssSelector("form[action='/signup'] p"));
    }

    @Step("Check whether the account is logged in")
    public boolean isLoggedIn() {
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector(".navbar-nav"), "Logged in as"));
    }
}
