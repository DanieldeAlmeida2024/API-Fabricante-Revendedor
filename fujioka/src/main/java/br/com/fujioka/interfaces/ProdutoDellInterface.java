package br.com.fujioka.interfaces;

public interface ProdutoDellInterface {
    void setId(int id);
    int getId();
    void setProduto(String produto);
    String getProduto();
    void setCategoria(String categoria);
    String getCategoria();
    void setPreco(double preco);
    double getPreco();
}
