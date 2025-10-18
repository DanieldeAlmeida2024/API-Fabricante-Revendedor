package br.com.produtos.model;

import br.com.produtos.interfaces.ProdutoInterface;

public class Produto implements ProdutoInterface {
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
    public void setNomeProduto(String produto) {
        this.produto = produto;
    }

    @Override
    public String getNomeProduto() {
        return this.produto;
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
