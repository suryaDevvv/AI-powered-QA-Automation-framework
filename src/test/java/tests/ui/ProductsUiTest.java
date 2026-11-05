package tests.ui;

import io.qameta.allure.Description;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.ProductDetailsPage;
import pages.ProductsPage;
import tests.support.FailureArtifactsListener;
import tests.support.UiTestBase;

import java.util.List;

@Listeners(FailureArtifactsListener.class)
public class ProductsUiTest extends UiTestBase {
    @Test(description = "UI-006 | Product search returns matching items", groups = {"ui", "smoke", "regression"})
    @Description("Search the catalogue and verify the visible result names match the query.")
    public void productSearchReturnsMatchingItems() {
        ProductsPage products = new ProductsPage(driver);
        products.open();
        List<String> names = products.search("Blue Top");
        Assert.assertFalse(names.isEmpty(), "Search should return at least one product.");
        Assert.assertTrue(names.stream().allMatch(name -> name.toLowerCase().contains("blue top")),
                "Every result should match Blue Top: " + names);
    }

    @Test(description = "UI-007 | Category filter shows the selected category", groups = {"ui", "regression"})
    @Description("Filter the catalogue by a category and verify that the category page is selected.")
    public void categoryFilterShowsSelectedCategory() {
        ProductsPage products = new ProductsPage(driver);
        products.open();
        String heading = products.filterByCategory("Women", "Dress");
        Assert.assertTrue(heading.toLowerCase().contains("women"), "Expected a Women category heading: " + heading);
    }

    @Test(description = "UI-008 | Product detail page shows product information", groups = {"ui", "regression"})
    @Description("Open a product detail page and verify product name, price, and category information are visible.")
    public void productDetailsAreVisible() {
        ProductsPage products = new ProductsPage(driver);
        products.open();
        products.openFirstProduct();
        ProductDetailsPage details = new ProductDetailsPage(driver);
        Assert.assertFalse(details.name().isBlank(), "Product name should be populated.");
        Assert.assertTrue(details.detailsVisible(), "Product price and category should be visible.");
    }
}
