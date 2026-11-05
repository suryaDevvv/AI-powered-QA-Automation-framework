package tests.ui;

import io.qameta.allure.Description;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.ProductDetailsPage;
import pages.ProductsPage;
import tests.support.FailureArtifactsListener;
import tests.support.TestUser;
import tests.support.UiTestBase;

@Listeners(FailureArtifactsListener.class)
public class CheckoutUiTest extends UiTestBase {
    @Test(description = "UI-012 | Checkout shows the registered address", groups = {"ui", "regression"})
    @Description("Verify delivery and billing address details match the registered account.")
    public void checkoutShowsRegisteredAddress() {
        TestUser user = createAccountViaApi();
        try {
            CheckoutPage checkout = openCheckoutAs(user);
            String address = checkout.addressDetails().toLowerCase();
            Assert.assertTrue(address.contains(user.address1().toLowerCase()), "Delivery address should match.");
            Assert.assertTrue(address.contains(user.city().toLowerCase()), "Delivery city should match.");
            Assert.assertTrue(address.contains(user.zipCode()), "Delivery postal code should match.");
        } finally {
            deleteAccountQuietly(user);
        }
    }

    @Test(description = "UI-013 | User can complete checkout", groups = {"ui", "smoke", "regression"})
    @Description("Place an order with the practice payment form and verify the order confirmation.")
    public void userCanCompleteCheckout() {
        TestUser user = createAccountViaApi();
        try {
            CheckoutPage checkout = openCheckoutAs(user);
            checkout.placeOrder();
            checkout.enterPaymentDetails("QA Practice", "4111111111111111", "123", "12", "2030");
            checkout.submitPayment();
            Assert.assertTrue(checkout.confirmationMessage().toLowerCase().contains("order placed"),
                    "Expected an order confirmation.");
        } finally {
            deleteAccountQuietly(user);
        }
    }

    @Test(description = "UI-014 | Payment form rejects missing fields", groups = {"ui", "regression"})
    @Description("Submit a checkout payment without card fields and verify validation prevents submission.")
    public void paymentFormRejectsMissingFields() {
        TestUser user = createAccountViaApi();
        try {
            CheckoutPage checkout = openCheckoutAs(user);
            checkout.placeOrder();
            checkout.submitPayment();
            Assert.assertTrue(checkout.paymentFormHasInvalidFields(),
                    "At least one required payment field should remain invalid.");
        } finally {
            deleteAccountQuietly(user);
        }
    }

    private CheckoutPage openCheckoutAs(TestUser user) {
        LoginPage login = new LoginPage(driver);
        login.open();
        login.login(user.email(), user.password());
        Assert.assertTrue(login.isLoggedIn(), "Test account should be signed in.");

        ProductsPage products = new ProductsPage(driver);
        products.open();
        products.openFirstProduct();
        ProductDetailsPage details = new ProductDetailsPage(driver);
        details.addToCart(1);
        details.openCartFromConfirmation();
        CartPage cart = new CartPage(driver);
        cart.proceedToCheckout();
        return new CheckoutPage(driver);
    }
}
