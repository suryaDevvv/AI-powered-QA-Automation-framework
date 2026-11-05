package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import tests.support.TestConfig;

import java.util.List;

public class ProductsPage extends BasePage {
    private static final By PRODUCT_LINKS = By.cssSelector(".features_items a[href*='/product_details/']");
    private static final By PRODUCT_NAMES = By.cssSelector(".features_items .productinfo p");
    private static final By SEARCH_INPUT = By.id("search_product");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open the products catalogue")
    public void open() {
        driver.get(TestConfig.baseUrl() + "/products");
        wait.until(ExpectedConditions.visibilityOfElementLocated(PRODUCT_LINKS));
    }

    @Step("Search products for {query}")
    public List<String> search(String query) {
        type(SEARCH_INPUT, query);
        click(By.id("submit_search"));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector("h2.title"), "SEARCHED PRODUCTS"));
        return texts(PRODUCT_NAMES);
    }

    @Step("Filter products by category {parentCategory} / {category}")
    public String filterByCategory(String parentCategory, String category) {
        click(By.xpath("//div[@id='accordian']//a[contains(normalize-space(.),'" + parentCategory + "')]"));
        click(By.xpath("//div[@id='accordian']//a[contains(@href,'/category_products/')"
                + " and contains(normalize-space(.),'" + category + "')]"));
        return text(By.cssSelector("h2.title"));
    }

    @Step("Open the first product details page")
    public void openFirstProduct() {
        tests.support.AdPopupHandler.dismissIfPresent(driver);
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_LINKS)).click();
        wait.until(currentDriver -> {
            tests.support.AdPopupHandler.dismissIfPresent(currentDriver);
            for (WebElement productName : currentDriver.findElements(By.cssSelector(".product-information h2"))) {
                if (productName.isDisplayed()) return productName;
            }
            return null;
        });
    }
}
