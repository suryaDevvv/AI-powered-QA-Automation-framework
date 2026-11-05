package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import tests.support.TestUser;

public class RegistrationPage extends BasePage {
    private static final By PASSWORD = By.id("password");
    private static final By CREATE_ACCOUNT = By.cssSelector("button[data-qa='create-account']");

    public RegistrationPage(WebDriver driver) {
        super(driver);
    }

    @Step("Complete the account form for {user.email}")
    public void completeAccount(TestUser user) {
        completeFields(user, true);
    }

    @Step("Complete the account form without a password")
    public void completeAccountWithoutPassword(TestUser user) {
        completeFields(user, false);
    }

    private void completeFields(TestUser user, boolean includePassword) {
        visible(PASSWORD);
        click(By.id("id_gender2"));
        if (includePassword) type(PASSWORD, user.password());
        select(By.id("days"), user.birthDate());
        select(By.id("months"), user.birthMonth());
        select(By.id("years"), user.birthYear());
        type(By.id("first_name"), user.firstName());
        type(By.id("last_name"), user.lastName());
        type(By.id("company"), user.company());
        type(By.id("address1"), user.address1());
        type(By.id("address2"), user.address2());
        select(By.id("country"), user.country());
        type(By.id("state"), user.state());
        type(By.id("city"), user.city());
        type(By.id("zipcode"), user.zipCode());
        type(By.id("mobile_number"), user.mobileNumber());
    }

    @Step("Submit the account creation form")
    public void createAccount() {
        click(CREATE_ACCOUNT);
    }

    @Step("Check whether the password form field is invalid")
    public boolean passwordIsInvalid() {
        return isInvalid(PASSWORD);
    }

    @Step("Check whether the account was created")
    public boolean accountCreated() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-qa='account-created']")));
        return true;
    }

    @Step("Continue after successful account creation")
    public void continueAfterCreate() {
        click(By.cssSelector("a[data-qa='continue-button']"));
    }
}
