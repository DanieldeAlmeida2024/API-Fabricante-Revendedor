package br.com.produtos.persistence;

import br.com.produtos.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoDaoTest {
    @TempDir Path temp;
    ProdutoDao dao;

    @BeforeEach
    void iniciarBancoIsolado() {
        dao = new ProdutoDao(new Connect("jdbc:sqlite:" + temp.resolve("catalogo-teste.db")));
    }

    private Produto produto(String nome, String categoria, double preco) {
        Produto p = new Produto();
        p.setNomeProduto(nome);
        p.setCategoria(categoria);
        p.setPreco(preco);
        return p;
    }

    @Test
    void criaConsultaAtualizaEExcluiProdutoRealEmSqlite() {
        assertTrue(dao.getAllProdutos().isEmpty());
        assertNull(dao.getProdutoById(1));
        dao.insertProduto(produto("Notebook", "Computadores", 2999.99));
        Produto gravado = dao.getAllProdutos().get(0);
        assertTrue(gravado.getId() > 0);
        assertEquals("Notebook", dao.getProdutoById(gravado.getId()).getNomeProduto());
        assertEquals(2999.99, gravado.getPreco(), 0.0000001);

        assertTrue(dao.updateProduto(gravado.getId(), produto("Notebook atualizado", "Eletrônicos", 3299.50)));
        assertEquals("Notebook atualizado", dao.getProdutoById(gravado.getId()).getNomeProduto());
        assertEquals("Eletrônicos", dao.getProdutoById(gravado.getId()).getCategoria());

        assertTrue(dao.deleteProduto(gravado.getId()));
        assertNull(dao.getProdutoById(gravado.getId()));
        assertFalse(dao.deleteProduto(gravado.getId()));
    }

    @Test
    void naoAfirmaSucessoSeProdutoNaoExiste() {
        assertFalse(dao.updateProduto(999, produto("Teste", "PC", 100)));
        assertFalse(dao.deleteProduto(999));
        assertNull(dao.getProdutoById(999));
    }

    @Test
    void textoComAspasNaoExecutaSqlNemCorrompeDados() {
        String literal = "Notebook'; DROP TABLE produtos; --";
        dao.insertProduto(produto(literal, "Produto de estudo", 1.25));
        dao.insertProduto(produto("Outro", "Catálogo", 20));
        List<Produto> encontrados = dao.getAllProdutos();
        assertEquals(2, encontrados.size());
        assertEquals(literal, encontrados.get(0).getNomeProduto());
    }

    @Test
    void duasInstanciasAcessamMesmoBancoEConexaoFecha() {
        dao.insertProduto(produto("Primeiro", "A", 10));
        ProdutoDao segunda = new ProdutoDao(new Connect(
                "jdbc:sqlite:" + temp.resolve("catalogo-teste.db")));
        assertEquals(1, segunda.getAllProdutos().size());
        segunda.insertProduto(produto("Segundo", "B", 20));
        assertEquals(2, dao.getAllProdutos().size());
    }

    @Test
    void daoPropagaFalhaDePersistenciaSemOcultarResultado() {
        ProdutoDao invalido = new ProdutoDao(new Connect(
                "jdbc:sqlite:" + temp.resolve("diretorio-inexistente").resolve("catalogo.db")));
        assertThrows(IllegalStateException.class, invalido::getAllProdutos);
    }
}
