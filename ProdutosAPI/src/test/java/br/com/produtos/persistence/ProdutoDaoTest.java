package br.com.produtos.persistence;

import br.com.produtos.model.Produto;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import junit.framework.TestCase;

public class ProdutoDaoTest extends TestCase {
    private Connection conn;
    private ProdutoDao dao;

    @Override
    protected void setUp() throws Exception {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE produtos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome_produto TEXT NOT NULL, categoria TEXT NOT NULL, preco REAL NOT NULL)");
        }
        dao = new ProdutoDao(conn);
    }

    @Override
    protected void tearDown() throws Exception {
        if (!conn.isClosed()) conn.close();
    }

    private static Produto produto(String nome, double preco) {
        Produto p = new Produto();
        p.setNomeProduto(nome);
        p.setCategoria("Hardware");
        p.setPreco(preco);
        return p;
    }

    public void testCreateAndReadByIdAdvancesCursor() {
        assertEquals(1, dao.insertProduto(produto("Notebook A", 2999.99)));
        Produto loaded = dao.getProdutoById(1);
        assertNotNull(loaded);
        assertEquals(1, loaded.getId());
        assertEquals("Notebook A", loaded.getNomeProduto());
        assertEquals("Hardware", loaded.getCategoria());
        assertEquals(2999.99, loaded.getPreco(), 0.001);
    }

    public void testMissingIdReturnsNullAndNoPhantomProduct() {
        assertNull(dao.getProdutoById(99));
        assertNull(dao.getProdutoById(0));
        assertNull(dao.getProdutoById(-1));
        assertTrue(dao.getAllProdutos().isEmpty());
    }

    public void testUpdateAndDeleteReturnAffectedRows() {
        dao.insertProduto(produto("Antigo", 10.0));
        assertEquals(0, dao.updateProduto(90, produto("Novo", 20.0)));
        assertEquals(1, dao.updateProduto(1, produto("Novo", 20.0)));
        assertEquals("Novo", dao.getProdutoById(1).getNomeProduto());
        assertEquals(20.0, dao.getProdutoById(1).getPreco(), 0.001);
        assertEquals(0, dao.deleteProduto(90));
        assertEquals(1, dao.deleteProduto(1));
        assertEquals(0, dao.deleteProduto(1));
        assertNull(dao.getProdutoById(1));
    }

    public void testSqlParameterBindingPreservesQuotesAndDoesNotAlterSchema() {
        String text = "Tablet'); DROP TABLE produtos; --";
        assertEquals(1, dao.insertProduto(produto(text, 199.99)));
        assertEquals(text, dao.getProdutoById(1).getNomeProduto());
        List<Produto> list = dao.getAllProdutos();
        assertEquals(1, list.size());
        assertEquals(text, list.get(0).getNomeProduto());
    }

    public void testSqlFailureCannotMasqueradeAsEmptyListOrSuccess() throws Exception {
        conn.close();
        try {
            dao.getAllProdutos();
            fail("SQL error must propagate");
        } catch (IllegalStateException expected) { }
        try {
            dao.insertProduto(produto("Fail", 1.0));
            fail("Write error must propagate");
        } catch (IllegalStateException expected) { }
        try {
            dao.getProdutoById(1);
            fail("Read error must propagate");
        } catch (IllegalStateException expected) { }
    }

    public void testConstructorRejectsUnavailableConnection() {
        try {
            new ProdutoDao(null);
            fail("A missing connection must fail closed");
        } catch (IllegalStateException expected) { }
    }
}
