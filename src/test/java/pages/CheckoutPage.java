package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class CheckoutPage extends BasePage {
    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    @Step("Read delivery and billing addresses")
    public String addressDetails() {
        return text(By.id("address_delivery")) + "\n" + text(By.id("address_invoice"));
    }

    @Step("Continue from checkout to payment")
    public void placeOrder() {
        type(By.name("message"), "QA automation practice order");
        click(By.cssSelector("a[href='/payment']"));
        visible(By.cssSelector("input[data-qa='name-on-card']"));
    }

    @Step("Enter practice card details")
    public void enterPaymentDetails(String name, String number, String cvc, String month, String year) {
        type(By.cssSelector("input[data-qa='name-on-card']"), name);
        type(By.cssSelector("input[data-qa='card-number']"), number);
        type(By.cssSelector("input[data-qa='cvc']"), cvc);
        type(By.cssSelector("input[data-qa='expiry-month']"), month);
        type(By.cssSelector("input[data-qa='expiry-year']"), year);
    }

    @Step("Submit the payment form")
    public void submitPayment() {
        click(By.cssSelector("button[data-qa='pay-button']"));
    }

    @Step("Check whether the payment form rejects missing fields")
    public boolean paymentFormHasInvalidFields() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('input[data-qa=\"name-on-card\"],"
                        + "input[data-qa=\"card-number\"],input[data-qa=\"cvc\"],"
                        + "input[data-qa=\"expiry-month\"],input[data-qa=\"expiry-year\"]'))"
                        + ".some(input => !input.checkValidity());"));
    }

    @Step("Read order confirmation text")
    public String confirmationMessage() {
        return text(By.cssSelector("[data-qa='order-placed']"));
    }
}
