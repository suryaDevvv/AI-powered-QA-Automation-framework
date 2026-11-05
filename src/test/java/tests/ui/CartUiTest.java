package tests.ui;

import io.qameta.allure.Description;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.ProductDetailsPage;
import pages.ProductsPage;
import tests.support.FailureArtifactsListener;
import tests.support.UiTestBase;

@Listeners(FailureArtifactsListener.class)
public class CartUiTest extends UiTestBase {
    @Test(description = "UI-009 | User can add a product to the cart", groups = {"ui", "smoke", "regression"})
    @Description("Add a product from its detail page and verify the cart contains one item.")
    public void userCanAddProductToCart() {
        CartPage cart = cartWithOneProduct(1);
        Assert.assertEquals(cart.itemNames().size(), 1, "One product should be in the cart.");
    }

    @Test(description = "UI-010 | User can remove a product from the cart", groups = {"ui", "regression"})
    @Description("Add a product, remove it from the cart, and verify the cart is empty.")
    public void userCanRemoveProductFromCart() {
        CartPage cart = cartWithOneProduct(1);
        Assert.assertEquals(cart.itemNames().size(), 1);
        cart.removeFirstProduct();
        Assert.assertTrue(cart.itemNames().isEmpty(), "The removed product should no longer be listed.");
    }

    @Test(description = "UI-011 | Cart quantity matches the selected quantity", groups = {"ui", "regression"})
    @Description("Set a product quantity before adding it and verify the cart shows the requested quantity.")
    public void cartShowsUpdatedQuantity() {
        CartPage cart = cartWithOneProduct(4);
        Assert.assertEquals(cart.firstQuantity(), "4");
    }

    private CartPage cartWithOneProduct(int quantity) {
        ProductsPage products = new ProductsPage(driver);
        products.open();
        products.openFirstProduct();
        ProductDetailsPage details = new ProductDetailsPage(driver);
        details.addToCart(quantity);
        details.openCartFromConfirmation();
        return new CartPage(driver);
    }
}
