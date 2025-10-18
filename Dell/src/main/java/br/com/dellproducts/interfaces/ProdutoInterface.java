package br.com.dellproducts.interfaces;

public interface ProdutoInterface {
    void setId(int id);
    int getId();
    void setProduto(String produto);
    String getProduto();
    void setCategoria(String categoria);
    String getCategoria();
    void setPreco(double preco);
    double getPreco();
}
