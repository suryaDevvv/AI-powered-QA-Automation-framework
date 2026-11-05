package tests.support;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public final class EcommerceApi {
    private EcommerceApi() {}

    private static RequestSpecification request() {
        RestAssuredConfig config = RestAssuredConfig.config().httpClient(
                HttpClientConfig.httpClientConfig()
                        .connectTimeout(TestConfig.apiConnectTimeout())
                        .readTimeout(TestConfig.apiReadTimeout())
                        .connectionRequestTimeout(TestConfig.apiConnectionRequestTimeout()));
        return RestAssured.given().baseUri(TestConfig.apiBaseUrl()).config(config)
                .header("Accept", "application/json");
    }

    @Step("Get the product catalogue")
    public static Response getProducts() {
        return record("GET", "/productsList", request().get("/productsList"));
    }

    @Step("Search the product catalogue for {searchTerm}")
    public static Response searchProducts(String searchTerm) {
        return record("POST", "/searchProduct", request()
                .formParam("search_product", searchTerm).post("/searchProduct"));
    }

    @Step("Create test account {user.email}")
    public static Response createAccount(TestUser user) {
        return record("POST", "/createAccount", request()
                .formParams(user.toFormParams()).post("/createAccount"));
    }

    @Step("Get account details for {email}")
    public static Response getAccount(String email) {
        return record("GET", "/getUserDetailByEmail", request()
                .queryParam("email", email).get("/getUserDetailByEmail"));
    }

    @Step("Update the test account details for {user.email}")
    public static Response updateAccount(TestUser user) {
        return record("PUT", "/updateAccount", request()
                .formParams(user.toFormParams()).put("/updateAccount"));
    }

    @Step("Delete test account {user.email}")
    public static Response deleteAccount(TestUser user) {
        return record("DELETE", "/deleteAccount", request()
                .formParam("email", user.email()).formParam("password", user.password()).delete("/deleteAccount"));
    }

    @Step("Verify login for {email}")
    public static Response verifyLogin(String email, String password) {
        return record("POST", "/verifyLogin", request()
                .formParam("email", email).formParam("password", password).post("/verifyLogin"));
    }

    @Step("Attempt to update an account with invalid credentials")
    public static Response updateAccountWithoutAuthorization(TestUser user) {
        return record("PUT", "/updateAccount", request()
                .formParam("name", user.name()).formParam("email", user.email())
                .formParam("password", "incorrect-password").put("/updateAccount"));
    }

    private static Response record(String method, String path, Response response) {
        ApiEvidence.record(method, path, response);
        return response;
    }
}
