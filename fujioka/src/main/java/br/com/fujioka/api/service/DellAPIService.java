package br.com.fujioka.api.service;

import br.com.fujioka.api.DellAPI;
import br.com.fujioka.model.ProdutoDell;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public abstract class DellAPIService {
    private static final Gson gson = new Gson();
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    static DellAPI api = new DellAPI();

    private static String baseUrl() {
        return api.getHost() + api.getPorta() + api.getContextPath();
    }

    private static String getJson(String url) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .build();
        final HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            // Não expor o corpo de resposta upstream em logs, erros ou JSP.
            throw new IOException("Catálogo upstream retornou HTTP " + response.statusCode());
        }
        return response.body();
    }

    private static List<ProdutoDell> parseCatalog(String json) throws IOException {
        try {
            final JsonElement parsed = JsonParser.parseString(json);
            if (!parsed.isJsonArray()) {
                throw new IOException("Formato inválido: catálogo deve ser um array JSON");
            }
            final Type listType = new TypeToken<List<ProdutoDell>>() {}.getType();
            final List<ProdutoDell> items = gson.fromJson(parsed, listType);
            if (items == null) throw new IOException("Catálogo indisponível");
            return items;
        } catch (JsonSyntaxException | IllegalStateException e) {
            throw new IOException("JSON inválido no catálogo upstream", e);
        }
    }

    public static List<ProdutoDell> getProdutosDell() throws IOException, InterruptedException {
        return parseCatalog(getJson(baseUrl()));
    }

    public static ProdutoDell getProdutoDell(int id) throws IOException, InterruptedException {
        if (id <= 0) throw new IllegalArgumentException("ID deve ser positivo");
        final String raw = getJson(baseUrl() + "?id=" + id);
        try {
            final JsonElement parsed = JsonParser.parseString(raw);
            if (parsed.isJsonNull()) return null;
            if (parsed.isJsonObject()) return gson.fromJson(parsed, ProdutoDell.class);
            if (parsed.isJsonArray()) {
                final List<ProdutoDell> values = parseCatalog(raw);
                return values.isEmpty() ? null : values.get(0);
            }
            throw new IOException("Formato inválido de produto upstream");
        } catch (JsonSyntaxException | IllegalStateException e) {
            throw new IOException("JSON inválido em produto upstream", e);
        }
    }
}
