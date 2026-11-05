package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class CartPage extends BasePage {
    private static final By ROWS = By.cssSelector("tr[id^='product-']");
    private static final By ITEM_NAMES = By.cssSelector("td.cart_description h4 a");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open the cart")
    public void open() {
        click(By.cssSelector("a[href='/view_cart']"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cart_info_table")));
    }

    @Step("Read item names from the cart")
    public List<String> itemNames() {
        if (driver.findElements(ROWS).isEmpty()) return List.of();
        return texts(ITEM_NAMES);
    }

    @Step("Read the first cart item quantity")
    public String firstQuantity() {
        return text(By.cssSelector("tr[id^='product-'] td.cart_quantity button"));
    }

    @Step("Remove the first product from the cart")
    public void removeFirstProduct() {
        click(By.cssSelector("tr[id^='product-'] a.cart_quantity_delete"));
        wait.until(ExpectedConditions.numberOfElementsToBe(ROWS, 0));
    }

    @Step("Proceed to checkout")
    public void proceedToCheckout() {
        click(By.cssSelector("a.btn.check_out"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("address_delivery")));
    }
}
