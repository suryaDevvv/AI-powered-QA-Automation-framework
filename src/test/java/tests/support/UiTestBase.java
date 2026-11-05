package tests.support;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import io.restassured.response.Response;
import org.testng.Assert;

public abstract class UiTestBase {
    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void startBrowser() {
        driver = DriverManager.startChrome();
        driver.get(TestConfig.baseUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void stopBrowser() {
        DriverManager.stop();
        ApiEvidence.clear();
    }

    protected TestUser createAccountViaApi() {
        TestUser user = TestUser.fresh();
        Response response = EcommerceApi.createAccount(user);
        Assert.assertEquals(response.statusCode(), 200, response.asString());
        Assert.assertEquals(ApiJson.responseCode(response), 201, response.asString());
        return user;
    }

    protected void deleteAccountQuietly(TestUser user) {
        if (user == null) return;
        try {
            EcommerceApi.deleteAccount(user);
        } catch (RuntimeException ignored) {
            // Cleanup must not replace the original test failure.
        }
    }
}
