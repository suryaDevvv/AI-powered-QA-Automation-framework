package tests.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class MySqlClient {
    private MySqlClient() {}

    public static String runHealthQuery() throws SQLException {
        String query = TestConfig.mysqlHealthQuery();
        if (!query.stripLeading().toLowerCase().startsWith("select")) {
            throw new IllegalArgumentException("MYSQL_HEALTH_QUERY must be a read-only SELECT query");
        }
        try (Connection connection = DriverManager.getConnection(
                TestConfig.mysqlUrl(), TestConfig.mysqlUsername(), TestConfig.mysqlPassword());
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (!resultSet.next()) throw new SQLException("The configured MySQL health query returned no rows");
            return resultSet.getString(1);
        }
    }
}
