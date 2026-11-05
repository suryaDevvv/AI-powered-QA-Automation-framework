package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductDetailsPage extends BasePage {
    private static final By NAME = By.cssSelector(".product-information h2");
    private static final By QUANTITY = By.id("quantity");

    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }

    @Step("Read the product name")
    public String name() {
        return text(NAME);
    }

    @Step("Check the product details are visible")
    public boolean detailsVisible() {
        return exists(NAME)
                && exists(By.cssSelector(".product-information span span"))
                && pageBody().contains("Category:");
    }

    @Step("Add the product to the cart with quantity {quantity}")
    public void addToCart(int quantity) {
        type(QUANTITY, Integer.toString(quantity));
        click(By.cssSelector(".product-information button.cart"));
        visible(By.cssSelector(".modal-content"));
    }

    @Step("Open cart from the add-to-cart confirmation")
    public void openCartFromConfirmation() {
        click(By.cssSelector(".modal-content a[href='/view_cart']"));
    }
}
