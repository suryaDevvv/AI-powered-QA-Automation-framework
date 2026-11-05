package tests.api;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import tests.support.ApiEvidence;
import tests.support.ApiJson;
import tests.support.ApiRetryAnalyzer;
import tests.support.EcommerceApi;
import tests.support.FailureArtifactsListener;
import tests.support.TestUser;

@Listeners(FailureArtifactsListener.class)
public class EcommerceApiTest {
    @Test(description = "API-001 | Get product catalogue", groups = {"api", "smoke", "regression"},
            retryAnalyzer = ApiRetryAnalyzer.class)
    @Description("Read the product catalogue and verify the API returns products.")
    public void getProductCatalogue() {
        Response response = EcommerceApi.getProducts();
        Assert.assertEquals(response.statusCode(), 200, response.asString());
        Assert.assertEquals(ApiJson.responseCode(response), 200, response.asString());
        Assert.assertFalse(ApiJson.read(response).path("products").isEmpty(), "Catalogue should contain products.");
    }

    @Test(description = "API-002 | Search products", groups = {"api", "regression"},
            retryAnalyzer = ApiRetryAnalyzer.class)
    @Description("Search products through the API and verify matching results are returned.")
    public void searchProducts() {
        Response response = EcommerceApi.searchProducts("top");
        Assert.assertEquals(response.statusCode(), 200, response.asString());
        Assert.assertEquals(ApiJson.responseCode(response), 200, response.asString());
        Assert.assertFalse(ApiJson.read(response).path("products").isEmpty(), "Search should return results.");
    }

    @Test(description = "API-003 | Create user account", groups = {"api", "smoke", "regression"},
            retryAnalyzer = ApiRetryAnalyzer.class)
    @Description("Create a unique account through the API; delete it before the test ends.")
    public void createAccount() {
        TestUser user = TestUser.fresh();
        try {
            Response response = EcommerceApi.createAccount(user);
            Assert.assertEquals(response.statusCode(), 200, response.asString());
            Assert.assertEquals(ApiJson.responseCode(response), 201, response.asString());
        } finally {
            cleanup(user);
        }
    }

    @Test(description = "API-004 | Get user account details", groups = {"api", "regression"},
            retryAnalyzer = ApiRetryAnalyzer.class)
    @Description("Create an account for this test, retrieve it by email, and remove it afterwards.")
    public void getAccountDetails() {
        TestUser user = createAccountForTest();
        try {
            Response response = EcommerceApi.getAccount(user.email());
            Assert.assertEquals(response.statusCode(), 200, response.asString());
            Assert.assertEquals(ApiJson.responseCode(response), 200, response.asString());
            Assert.assertEquals(ApiJson.read(response).path("user").path("email").asText(), user.email());
            Assert.assertEquals(ApiJson.read(response).path("user").path("name").asText(), user.name());
        } finally {
            cleanup(user);
        }
    }

    @Test(description = "API-005 | Update user account", groups = {"api", "regression"},
            retryAnalyzer = ApiRetryAnalyzer.class)
    @Description("Create a fresh account, update its name, and read back the update.")
    public void updateAccount() {
        TestUser user = createAccountForTest();
        TestUser changed = user.withName("Updated QA User");
        try {
            Response updated = EcommerceApi.updateAccount(changed);
            Assert.assertEquals(updated.statusCode(), 200, updated.asString());
            Assert.assertEquals(ApiJson.responseCode(updated), 200, updated.asString());
            Response fetched = EcommerceApi.getAccount(user.email());
            Assert.assertEquals(ApiJson.responseCode(fetched), 200, fetched.asString());
            Assert.assertEquals(ApiJson.read(fetched).path("user").path("name").asText(), changed.name());
        } finally {
            cleanup(changed);
        }
    }

    @Test(description = "API-006 | Delete user account", groups = {"api", "regression"},
            retryAnalyzer = ApiRetryAnalyzer.class)
    @Description("Create and delete a unique account, then verify the account can no longer be found.")
    public void deleteAccount() {
        TestUser user = createAccountForTest();
        Response deleted = EcommerceApi.deleteAccount(user);
        Assert.assertEquals(deleted.statusCode(), 200, deleted.asString());
        Assert.assertEquals(ApiJson.responseCode(deleted), 200, deleted.asString());
        Response fetched = EcommerceApi.getAccount(user.email());
        Assert.assertEquals(ApiJson.responseCode(fetched), 404, fetched.asString());
        ApiEvidence.clear();
    }

    @Test(description = "API-007 | Reject account update without valid credentials",
            groups = {"api", "smoke", "regression"}, retryAnalyzer = ApiRetryAnalyzer.class)
    @Description("Attempt an account update with invalid credentials and verify the service rejects it.")
    public void rejectUpdateWithoutValidCredentials() {
        TestUser neverCreated = TestUser.fresh();
        Response response = EcommerceApi.updateAccountWithoutAuthorization(neverCreated);
        Assert.assertEquals(response.statusCode(), 200, response.asString());
        Assert.assertEquals(ApiJson.responseCode(response), 404, response.asString());
        Assert.assertTrue(ApiJson.read(response).path("message").asText().toLowerCase().contains("not found"),
                "Expected an account-not-found response.");
        ApiEvidence.clear();
    }

    private TestUser createAccountForTest() {
        TestUser user = TestUser.fresh();
        Response response = EcommerceApi.createAccount(user);
        Assert.assertEquals(response.statusCode(), 200, response.asString());
        Assert.assertEquals(ApiJson.responseCode(response), 201, response.asString());
        return user;
    }

    private void cleanup(TestUser user) {
        try {
            EcommerceApi.deleteAccount(user);
        } catch (RuntimeException ignored) {
            // A cleanup failure should not hide the assertion that failed the test.
        } finally {
            ApiEvidence.clear();
        }
    }
}
