package br.com.produtos.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Abre uma conexão SQLite por operação.
 *
 * O caminho do banco pode ser configurado por `PRODUTOS_DB_PATH` ou
 * pela propriedade JVM `produtos.db.path`. O padrão didático é produtos.db.
 */
public final class Connect {
    private final String jdbcUrl;

    public Connect() {
        this("jdbc:sqlite:" + databasePath());
    }

    public Connect(String jdbcUrl) {
        if (jdbcUrl == null || !jdbcUrl.startsWith("jdbc:sqlite:")) {
            throw new IllegalArgumentException("A URL precisa usar jdbc:sqlite:.");
        }
        this.jdbcUrl = jdbcUrl;
    }

    private static String databasePath() {
        String property = System.getProperty("produtos.db.path");
        if (property != null && !property.isBlank()) {
            return property;
        }
        String env = System.getenv("PRODUTOS_DB_PATH");
        return env == null || env.isBlank() ? "produtos.db" : env;
    }

    public Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl);
            try {
                createTable(connection);
                return connection;
            } catch (SQLException error) {
                connection.close();
                throw error;
            }
        } catch (SQLException error) {
            throw new IllegalStateException("Não foi possível acessar o catálogo SQLite.", error);
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS produtos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome_produto TEXT NOT NULL," +
                "categoria TEXT NOT NULL," +
                "preco REAL NOT NULL CHECK (preco > 0)" +
                ")";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
        }
    }
}
