package br.com.dellproducts.api.service;

import br.com.dellproducts.api.DellAPI;
import br.com.dellproducts.enums.Http;
import br.com.dellproducts.model.Produto;
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
import java.util.Collections;
import java.util.List;

import static br.com.dellproducts.enums.Http.*;

public abstract class DellAPIService {
    private static final Gson gson = new Gson();
    static DellAPI api = new DellAPI();
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static String urlBase() {
        return api.getHost() + api.getPorta() + api.getContextPath();
    }

    private static HttpResponse<String> requestHttp(String urlRequest, String body, Http method)
            throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(urlRequest))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json");
        if (method == GET) {
            builder.GET();
        } else if (method == POST) {
            builder.header("Content-Type", "application/json; charset=utf-8")
                   .POST(HttpRequest.BodyPublishers.ofString(body));
        } else if (method == PUT) {
            builder.header("Content-Type", "application/json; charset=utf-8")
                   .PUT(HttpRequest.BodyPublishers.ofString(body));
        } else if (method == DELETE) {
            builder.DELETE();
        } else {
            throw new IllegalArgumentException("Método HTTP não suportado");
        }
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    static void requireSuccess(int status) throws IOException {
        if (status < 200 || status >= 300) {
            // Não ecoar payload de erro: pode conter dados internos.
            throw new IOException("API Produtos respondeu HTTP " + status);
        }
    }

    static String jsonProduto(Produto produto) {
        return gson.toJson(produto); // Escape correto para aspas, barras e Unicode.
    }

    static Produto decodeSingleProduct(String json) throws IOException {
        try {
            JsonElement root = JsonParser.parseString(json);
            if (root.isJsonNull()) return null;
            if (root.isJsonObject()) return gson.fromJson(root, Produto.class);
            if (root.isJsonArray()) {
                if (root.getAsJsonArray().isEmpty()) return null;
                return gson.fromJson(root.getAsJsonArray().get(0), Produto.class);
            }
        } catch (JsonSyntaxException e) {
            throw new IOException("Resposta JSON inválida da API Produtos", e);
        }
        throw new IOException("Formato de produto inesperado na resposta");
    }

    public static List<Produto> getProdutos() throws IOException, InterruptedException {
        HttpResponse<String> response = requestHttp(urlBase(), null, GET);
        requireSuccess(response.statusCode());
        try {
            Type listType = new TypeToken<List<Produto>>() {}.getType();
            List<Produto> products = gson.fromJson(response.body(), listType);
            return products == null ? Collections.emptyList() : products;
        } catch (JsonSyntaxException e) {
            throw new IOException("Lista de produtos em formato inválido", e);
        }
    }

    public static Produto getProduto(int id) throws IOException, InterruptedException {
        if (id <= 0) throw new IOException("ID de produto inválido");
        HttpResponse<String> response = requestHttp(urlBase() + "?id=" + id, null, GET);
        requireSuccess(response.statusCode());
        return decodeSingleProduct(response.body());
    }

    public static String postProduto(Produto produto) throws IOException, InterruptedException {
        if (produto == null) throw new IllegalArgumentException("Produto obrigatório");
        HttpResponse<String> response = requestHttp(urlBase(), jsonProduto(produto), POST);
        requireSuccess(response.statusCode());
        return "Produto cadastrado com sucesso.";
    }

    public static String putProduto(Produto produto) throws IOException, InterruptedException {
        if (produto == null || produto.getId() <= 0) throw new IllegalArgumentException("Produto ou ID inválido");
        HttpResponse<String> response = requestHttp(
                urlBase() + "?id=" + produto.getId(), jsonProduto(produto), PUT);
        requireSuccess(response.statusCode());
        return response.body();
    }

    public static String deleteProduto(int id) throws IOException, InterruptedException {
        if (id <= 0) throw new IllegalArgumentException("ID de produto inválido");
        HttpResponse<String> response = requestHttp(urlBase() + "?id=" + id, null, DELETE);
        requireSuccess(response.statusCode());
        return response.body();
    }
}
