package br.com.produtos.persistence;

import br.com.produtos.model.Produto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDao {
    private final Connection connection;

    public ProdutoDao() {
        this(new Connect().getConnection());
    }

    // Injeção de Connection permite testes SQLite in-memory sem criar produtos.db.
    public ProdutoDao(Connection connection) {
        if (connection == null) {
            throw new IllegalStateException("Conexão com banco de produtos indisponível");
        }
        this.connection = connection;
    }

    public int insertProduto(Produto produto) {
        final String sql = "INSERT INTO produtos (nome_produto, categoria, preco) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, produto.getNomeProduto());
            stmt.setString(2, produto.getCategoria());
            stmt.setDouble(3, produto.getPreco());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao inserir produto", e);
        }
    }

    public List<Produto> getAllProdutos() {
        final List<Produto> produtos = new ArrayList<>();
        final String sql = "SELECT id, nome_produto, categoria, preco FROM produtos ORDER BY id";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet results = stmt.executeQuery()) {
            while (results.next()) {
                produtos.add(mapProduto(results));
            }
            return produtos;
        } catch (SQLException e) {
            // Não transformar falha de consulta em lista vazia HTTP 200.
            throw new IllegalStateException("Falha ao listar produtos", e);
        }
    }

    public int updateProduto(int id, Produto produto) {
        final String sql = "UPDATE produtos SET nome_produto=?, categoria=?, preco=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, produto.getNomeProduto());
            stmt.setString(2, produto.getCategoria());
            stmt.setDouble(3, produto.getPreco());
            stmt.setInt(4, id);
            return stmt.executeUpdate(); // 0 = registro inexistente
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao atualizar produto", e);
        }
    }

    public int deleteProduto(int id) {
        final String sql = "DELETE FROM produtos WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate(); // 0 = registro inexistente
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao excluir produto", e);
        }
    }

    public Produto getProdutoById(int id) {
        if (id <= 0) return null;
        final String sql = "SELECT id, nome_produto, categoria, preco FROM produtos WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet results = stmt.executeQuery()) {
                if (!results.next()) return null; // cursor precisa ser avançado antes da leitura
                return mapProduto(results);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao consultar produto por ID", e);
        }
    }

    private Produto mapProduto(ResultSet row) throws SQLException {
        final Produto produto = new Produto();
        produto.setId(row.getInt("id"));
        produto.setNomeProduto(row.getString("nome_produto"));
        produto.setCategoria(row.getString("categoria"));
        produto.setPreco(row.getDouble("preco"));
        return produto;
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao fechar conexão de produtos", e);
        }
    }
}
