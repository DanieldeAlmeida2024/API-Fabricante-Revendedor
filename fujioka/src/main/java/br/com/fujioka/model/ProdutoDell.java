package br.com.fujioka.model;

import br.com.fujioka.interfaces.ProdutoDellInterface;

public class ProdutoDell implements ProdutoDellInterface {
    private int id;
    private String produto;
    private String categoria;
    private double preco;

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setProduto(String produto) {
        this.produto = produto;
    }

    @Override
    public String getProduto() {
        return produto;
    }

    @Override
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public String getCategoria() {
        return this.categoria;
    }

    @Override
    public void setPreco(double preco) {
        this.preco = preco;
    }

    @Override
    public double getPreco() {
        return this.preco;
    }
}
