package tests.ui;

import io.qameta.allure.Description;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.RegistrationPage;
import tests.support.FailureArtifactsListener;
import tests.support.TestUser;
import tests.support.UiTestBase;

@Listeners(FailureArtifactsListener.class)
public class RegistrationUiTest extends UiTestBase {
    @Test(description = "UI-003 | User can register a new account", groups = {"ui", "smoke", "regression"})
    @Description("Create an account with unique test data and confirm the account-created state.")
    public void userCanRegister() {
        TestUser user = TestUser.fresh();
        try {
            LoginPage login = new LoginPage(driver);
            login.open();
            login.beginSignup(user.name(), user.email());
            RegistrationPage registration = new RegistrationPage(driver);
            registration.completeAccount(user);
            registration.createAccount();
            Assert.assertTrue(registration.accountCreated(), "Account-created confirmation should be visible.");
            registration.continueAfterCreate();
        } finally {
            deleteAccountQuietly(user);
        }
    }

    @Test(description = "UI-004 | Duplicate email is rejected", groups = {"ui", "regression"})
    @Description("Register an email through the API, then verify the signup form rejects the duplicate.")
    public void duplicateEmailIsRejected() {
        TestUser user = createAccountViaApi();
        try {
            LoginPage login = new LoginPage(driver);
            login.open();
            login.beginSignup(user.name(), user.email());
            Assert.assertTrue(login.signupError().toLowerCase().contains("already"),
                    "Expected a duplicate-email validation message.");
        } finally {
            deleteAccountQuietly(user);
        }
    }

    @Test(description = "UI-005 | Empty password is rejected during registration", groups = {"ui", "regression"})
    @Description("Verify the registration form prevents account creation when its required password is empty.")
    public void emptyPasswordIsRejected() {
        TestUser user = TestUser.fresh();
        LoginPage login = new LoginPage(driver);
        login.open();
        login.beginSignup(user.name(), user.email());
        RegistrationPage registration = new RegistrationPage(driver);
        registration.completeAccountWithoutPassword(user);
        registration.createAccount();
        Assert.assertTrue(registration.passwordIsInvalid(), "The required password field should be invalid.");
        deleteAccountQuietly(user.withPassword(""));
    }
}
