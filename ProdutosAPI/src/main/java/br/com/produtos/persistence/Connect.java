package br.com.produtos.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Connect {
    private Connection connection;
    public Connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:produtos.db");
            createTable(this.connection);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    public Connection getConnection(){
        return this.connection;
    }

    private void createTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS produtos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome_produto TEXT NOT NULL," +
                "categoria TEXT NOT NULL," +
                "preco REAL NOT NULL" +
                ");";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
        }
    }
}
