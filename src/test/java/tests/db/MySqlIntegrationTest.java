package tests.db;

import io.qameta.allure.Description;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import tests.support.FailureArtifactsListener;
import tests.support.MySqlClient;
import tests.support.TestConfig;

@Listeners(FailureArtifactsListener.class)
public class MySqlIntegrationTest {
    @Test(description = "DB-001 | MySQL connection and read-only health query",
            groups = {"db", "regression"})
    @Description("Connect to the configured MySQL test database and execute its read-only health query.")
    public void mysqlConnectionCanRunHealthQuery() throws Exception {
        if (!TestConfig.mysqlConfigured()) {
            throw new SkipException("Set MYSQL_URL, MYSQL_USER, and MYSQL_PASSWORD to enable MySQL integration.");
        }
        Assert.assertEquals(MySqlClient.runHealthQuery(), "1",
                "The default health query should return 1. Configure a compatible SELECT if the schema differs.");
    }
}
