package tests.ui;

import io.qameta.allure.Description;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.LoginPage;
import tests.support.FailureArtifactsListener;
import tests.support.TestUser;
import tests.support.UiTestBase;

@Listeners(FailureArtifactsListener.class)
public class LoginUiTest extends UiTestBase {
    @Test(description = "UI-001 | Valid user can log in", groups = {"ui", "smoke", "regression"})
    @Description("Verify an existing registered user can log in through the web form.")
    public void validUserCanLogIn() {
        TestUser user = createAccountViaApi();
        try {
            LoginPage login = new LoginPage(driver);
            login.open();
            login.login(user.email(), user.password());
            Assert.assertTrue(login.isLoggedIn(), "The page should show the logged-in account.");
        } finally {
            deleteAccountQuietly(user);
        }
    }

    @Test(description = "UI-002 | Invalid credentials show a login error", groups = {"ui", "regression"})
    @Description("Verify the login form rejects an unregistered email and incorrect password.")
    public void invalidCredentialsShowError() {
        LoginPage login = new LoginPage(driver);
        login.open();
        login.login("qa.invalid@example.com", "wrong-password");
        Assert.assertTrue(login.loginError().contains("incorrect"), "Expected the invalid-login message.");
    }
}
