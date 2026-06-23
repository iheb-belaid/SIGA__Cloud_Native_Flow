package com.stage.appToDo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class PostgresDatabaseInitializer {

    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/Siga-todo-cloud-native-Flow";
    private static final String DEFAULT_USERNAME = "postgres";
    private static final String DEFAULT_PASSWORD = "admin";
    private static final String JDBC_PREFIX = "jdbc:postgresql://";

    private PostgresDatabaseInitializer() {
    }

    public static void ensureDatabaseExists() {
        String datasourceUrl = readValue("SPRING_DATASOURCE_URL", DEFAULT_URL);
        if (!datasourceUrl.startsWith(JDBC_PREFIX)) {
            return;
        }

        DatabaseTarget target = DatabaseTarget.parse(datasourceUrl);
        if (target == null || "postgres".equalsIgnoreCase(target.databaseName())) {
            return;
        }

        String username = readValue("SPRING_DATASOURCE_USERNAME", DEFAULT_USERNAME);
        String password = readValue("SPRING_DATASOURCE_PASSWORD", DEFAULT_PASSWORD);

        try {
            Class.forName("org.postgresql.Driver");

            try (Connection connection = DriverManager.getConnection(target.adminUrl(), username, password)) {
                if (databaseExists(connection, target.databaseName())) {
                    return;
                }

                try (Statement statement = connection.createStatement()) {
                    statement.execute("CREATE DATABASE \"" + escapeIdentifier(target.databaseName()) + "\"");
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            throw new IllegalStateException(
                    "Impossible d'initialiser la base PostgreSQL " + target.databaseName(),
                    ex
            );
        }
    }

    private static boolean databaseExists(Connection connection, String databaseName) throws SQLException {
        try (PreparedStatement statement =
                     connection.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?")) {
            statement.setString(1, databaseName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private static String escapeIdentifier(String identifier) {
        return identifier.replace("\"", "\"\"");
    }

    private static String readValue(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private record DatabaseTarget(String adminUrl, String databaseName) {

        private static DatabaseTarget parse(String datasourceUrl) {
            String remainder = datasourceUrl.substring(JDBC_PREFIX.length());
            int slashIndex = remainder.indexOf('/');
            if (slashIndex < 0 || slashIndex == remainder.length() - 1) {
                return null;
            }

            String hostPart = remainder.substring(0, slashIndex);
            String databasePart = remainder.substring(slashIndex + 1);
            int queryIndex = databasePart.indexOf('?');

            String databaseName = queryIndex >= 0 ? databasePart.substring(0, queryIndex) : databasePart;
            String queryPart = queryIndex >= 0 ? databasePart.substring(queryIndex) : "";
            String adminUrl = JDBC_PREFIX + hostPart + "/postgres" + queryPart;

            return new DatabaseTarget(adminUrl, databaseName);
        }
    }
}
