package br.com.produtos.interfaces;

public interface ProdutoInterface {
    void setId(int id);
    int getId();
    void setNomeProduto(String produto);
    String getNomeProduto();
    void setCategoria(String categoria);
    String getCategoria();
    void setPreco(double preco);
    double getPreco();

}
