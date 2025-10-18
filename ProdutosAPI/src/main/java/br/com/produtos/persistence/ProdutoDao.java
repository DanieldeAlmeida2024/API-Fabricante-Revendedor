package br.com.produtos.persistence;
import br.com.produtos.persistence.Connect;
import br.com.produtos.model.Produto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDao {

    private Connection connection;

    public ProdutoDao() {
        Connect conexao = new Connect();
        this.connection = conexao.getConnection();
    }


    public void insertProduto(Produto produto) {
        String sql = "INSERT INTO produtos (nome_produto, categoria, preco) VALUES (?, ?, ?);";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, produto.getNomeProduto());
            statement.setString(2, produto.getCategoria());
            statement.setDouble(3, produto.getPreco());
            statement.executeUpdate();
            System.out.println("Produto inserido com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Produto> getAllProdutos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos;";
        try (PreparedStatement statement = this.connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Produto produto = new Produto();
                produto.setId(resultSet.getInt("id"));
                produto.setNomeProduto(resultSet.getString("nome_produto"));
                produto.setCategoria(resultSet.getString("categoria"));
                produto.setPreco(resultSet.getDouble("preco"));
                produtos.add(produto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produtos;
    }

    public void updateProduto(int idProduto, Produto produto) {
        String sql = "UPDATE produtos SET nome_produto = ?, categoria = ?, preco = ? WHERE id = ?;";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, produto.getNomeProduto());
            statement.setString(2, produto.getCategoria());
            statement.setDouble(3, produto.getPreco());
            statement.setInt(4, idProduto);
            statement.executeUpdate();
            System.out.println("Produto atualizado com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteProduto(int id) {
        String sql = "DELETE FROM produtos WHERE id = ?;";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Produto excluído com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Produto getProdutoById(int id){
        Produto produto = new Produto();
        String sql = "SELECT * FROM produtos WHERE id=?;";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)){
            statement.setInt(1,id);
            ResultSet resultSet = statement.executeQuery();
            produto.setId(resultSet.getInt("id"));
            produto.setNomeProduto(resultSet.getString("nome_produto"));
            produto.setCategoria(resultSet.getString("categoria"));
            produto.setPreco(resultSet.getDouble("preco"));
            System.out.println("Busca por id concluída");
            return produto;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() {
        try {
            if (this.connection != null) {
                this.connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}