package br.com.fujioka.api.service;

import br.com.fujioka.model.ProdutoDell;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import junit.framework.TestCase;

public class DellAPIServiceTest extends TestCase {
    private HttpServer server;
    private String savedHost;
    private int savedPort;
    private final AtomicInteger status = new AtomicInteger(200);
    private final AtomicReference<String> body = new AtomicReference<>("[]");

    @Override
    protected void setUp() throws Exception {
        savedHost = DellAPIService.api.getHost();
        savedPort = DellAPIService.api.getPorta();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/ProdutosAPI/", exchange -> {
            final byte[] bytes = body.get().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(status.get(), bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        DellAPIService.api.setHost("http://127.0.0.1:");
        DellAPIService.api.setPorta(server.getAddress().getPort());
    }

    @Override
    protected void tearDown() throws Exception {
        DellAPIService.api.setHost(savedHost);
        DellAPIService.api.setPorta(savedPort);
        server.stop(0);
    }

    public void testCatalogArrayAndEscapedUnicode() throws Exception {
        body.set("[{\"id\":7,\"produto\":\"Notebook \\\"X\\\" & <em>\",\"categoria\":\"Peças\",\"preco\":199.50}]");
        List<ProdutoDell> items = DellAPIService.getProdutosDell();
        assertEquals(1, items.size());
        assertEquals(7, items.get(0).getId());
        assertEquals("Notebook \"X\" & <em>", items.get(0).getProduto());
        assertEquals(199.50, items.get(0).getPreco(), 0.001);
    }

    public void testEmptyArrayIsValidEmptyCatalog() throws Exception {
        assertTrue(DellAPIService.getProdutosDell().isEmpty());
    }

    public void testHttp503CannotBecomeEmptyCatalog() throws Exception {
        status.set(503);
        body.set("{\"error\":\"private server log\"}");
        try {
            DellAPIService.getProdutosDell();
            fail("upstream failure must propagate");
        } catch (IOException expected) {
            assertTrue(expected.getMessage().contains("503"));
            assertFalse(expected.getMessage().contains("private server log"));
        }
    }

    public void testMalformedOrWrongShapeCannotBecomeSuccess() throws Exception {
        for (String raw : new String[]{"{broken", "{}", "null", "\"oops\""}) {
            body.set(raw);
            try {
                DellAPIService.getProdutosDell();
                fail("invalid catalog must fail: " + raw);
            } catch (IOException expected) { }
        }
    }

    public void testGetIdAcceptsObjectAndLegacyArray() throws Exception {
        body.set("{\"id\":3,\"produto\":\"Monitor\",\"categoria\":\"Vídeo\",\"preco\":250.0}");
        assertEquals("Monitor", DellAPIService.getProdutoDell(3).getProduto());
        body.set("[{\"id\":3,\"produto\":\"Mouse\",\"categoria\":\"Vídeo\",\"preco\":10.0}]");
        assertEquals("Mouse", DellAPIService.getProdutoDell(3).getProduto());
        body.set("[]");
        assertNull(DellAPIService.getProdutoDell(3));
    }

    public void testGetIdRejectsInvalidIdAnd404() throws Exception {
        try {
            DellAPIService.getProdutoDell(0);
            fail("ID 0 must be rejected");
        } catch (IllegalArgumentException expected) { }
        status.set(404);
        try {
            DellAPIService.getProdutoDell(999);
            fail("upstream 404 must be propagated");
        } catch (IOException expected) { }
    }
}
