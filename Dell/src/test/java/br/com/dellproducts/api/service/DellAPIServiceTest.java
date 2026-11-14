package br.com.dellproducts.api.service;

import br.com.dellproducts.model.Produto;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import junit.framework.TestCase;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DellAPIServiceTest extends TestCase {
    private HttpServer server;
    private final AtomicInteger responseStatus = new AtomicInteger(200);
    private final AtomicReference<String> responseBody = new AtomicReference<>("{}");
    private final AtomicReference<String> incomingBody = new AtomicReference<>("");
    private String savedHost;
    private int savedPort;

    @Override
    protected void setUp() throws Exception {
        savedHost = DellAPIService.api.getHost();
        savedPort = DellAPIService.api.getPorta();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/ProdutosAPI/", exchange -> {
            incomingBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] payload = responseBody.get().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(responseStatus.get(), payload.length);
            exchange.getResponseBody().write(payload);
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

    private static Produto produto() {
        Produto p = new Produto();
        p.setId(7);
        p.setProduto("Notebook \"X\" ; \u00e1");
        p.setCategoria("Computadores");
        p.setPreco(100.99);
        return p;
    }

    public void testPostUsesGsonAndRespects201() throws Exception {
        responseStatus.set(201);
        responseBody.set("{\"message\":\"Produto criado\"}");
        assertEquals("Produto cadastrado com sucesso.", DellAPIService.postProduto(produto()));
        JsonObject json = JsonParser.parseString(incomingBody.get()).getAsJsonObject();
        assertEquals("Notebook \"X\" ; \u00e1", json.get("produto").getAsString());
        assertEquals(100.99, json.get("preco").getAsDouble(), 0.001);
    }

    public void testPost500DoesNotReturnFalseSuccess() throws Exception {
        responseStatus.set(500);
        responseBody.set("{\"error\":\"Banco indisponível\"}");
        try {
            DellAPIService.postProduto(produto());
            fail("HTTP 500 deve provocar erro");
        } catch (IOException expected) {
            assertTrue(expected.getMessage().contains("HTTP 500"));
            assertFalse(expected.getMessage().contains("Banco indisponível"));
        }
    }

    public void testGetSingleSupportsObjectResponse() throws Exception {
        responseStatus.set(200);
        responseBody.set("{\"id\":7,\"produto\":\"Mouse\",\"categoria\":\"Hardware\",\"preco\":25.5}");
        Produto result = DellAPIService.getProduto(7);
        assertEquals("Mouse", result.getProduto());
        assertEquals(7, result.getId());
    }

    public void testGetAllSupportsListResponse() throws Exception {
        responseBody.set("[{\"id\":1,\"produto\":\"SSD\",\"categoria\":\"Storage\",\"preco\":29.0}]");
        List<Produto> result = DellAPIService.getProdutos();
        assertEquals(1, result.size());
        assertEquals("SSD", result.get(0).getProduto());
    }

    public void testGet404AndMutationsRejectUpstreamError() throws Exception {
        responseStatus.set(404);
        try { DellAPIService.getProduto(700); fail(); } catch (IOException expected) { }
        try { DellAPIService.putProduto(produto()); fail(); } catch (IOException expected) { }
        try { DellAPIService.deleteProduto(7); fail(); } catch (IOException expected) { }
    }

    public void testDecodeSingleSupportsArrayNullAndInvalidType() throws Exception {
        assertNull(DellAPIService.decodeSingleProduct("null"));
        assertNull(DellAPIService.decodeSingleProduct("[]"));
        assertEquals("Mouse", DellAPIService.decodeSingleProduct("[{\"produto\":\"Mouse\"}]").getProduto());
        try {
            DellAPIService.decodeSingleProduct("123");
            fail("Formato incompatível deve ser rejeitado");
        } catch (IOException expected) { }
    }
}
