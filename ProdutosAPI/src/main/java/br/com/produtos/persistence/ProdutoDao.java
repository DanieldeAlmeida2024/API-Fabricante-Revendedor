package br.com.produtos.persistence;

import br.com.produtos.model.Produto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DAO SQLite: PreparedStatement parametriza dados; toda operação abre e fecha
 * conexão própria para não compartilhar um Connection entre threads do servlet.
 */
public final class ProdutoDao {
    private final Connect database;

    public ProdutoDao() {
        this(new Connect());
    }

    public ProdutoDao(Connect database) {
        this.database = Objects.requireNonNull(database);
    }

    public void insertProduto(Produto produto) {
        String sql = "INSERT INTO produtos (nome_produto, categoria, preco) VALUES (?, ?, ?)";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindProduto(statement, produto);
            statement.executeUpdate();
        } catch (SQLException error) {
            throw new IllegalStateException("Falha ao inserir produto no SQLite.", error);
        }
    }

    public List<Produto> getAllProdutos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, nome_produto, categoria, preco FROM produtos ORDER BY id";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                produtos.add(mapProduto(results));
            }
            return produtos;
        } catch (SQLException error) {
            throw new IllegalStateException("Falha ao listar produtos no SQLite.", error);
        }
    }

    /** Retorna true somente se uma linha existente foi atualizada. */
    public boolean updateProduto(int idProduto, Produto produto) {
        String sql = "UPDATE produtos SET nome_produto = ?, categoria = ?, preco = ? WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindProduto(statement, produto);
            statement.setInt(4, idProduto);
            return statement.executeUpdate() > 0;
        } catch (SQLException error) {
            throw new IllegalStateException("Falha ao atualizar produto no SQLite.", error);
        }
    }

    /** Retorna true somente se uma linha existente foi excluída. */
    public boolean deleteProduto(int id) {
        String sql = "DELETE FROM produtos WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException error) {
            throw new IllegalStateException("Falha ao excluir produto no SQLite.", error);
        }
    }

    /** Retorna null quando o ID não existe; não tenta ler antes de ResultSet.next(). */
    public Produto getProdutoById(int id) {
        String sql = "SELECT id, nome_produto, categoria, preco FROM produtos WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapProduto(result) : null;
            }
        } catch (SQLException error) {
            throw new IllegalStateException("Falha ao consultar produto no SQLite.", error);
        }
    }

    private static void bindProduto(PreparedStatement statement, Produto produto)
            throws SQLException {
        statement.setString(1, produto.getNomeProduto());
        statement.setString(2, produto.getCategoria());
        statement.setDouble(3, produto.getPreco());
    }

    private static Produto mapProduto(ResultSet result) throws SQLException {
        Produto produto = new Produto();
        produto.setId(result.getInt("id"));
        produto.setNomeProduto(result.getString("nome_produto"));
        produto.setCategoria(result.getString("categoria"));
        produto.setPreco(result.getDouble("preco"));
        return produto;
    }

    /** Mantido por compatibilidade com o código antigo: não há conexão persistente. */
    @Deprecated
    public void closeConnection() {
        // Cada chamada ao DAO já fecha automaticamente a sua conexão.
    }
}
