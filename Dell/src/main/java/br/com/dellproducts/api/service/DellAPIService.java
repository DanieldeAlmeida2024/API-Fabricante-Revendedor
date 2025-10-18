package br.com.dellproducts.api.service;
import br.com.dellproducts.api.DellAPI;
import br.com.dellproducts.enums.Http;
import br.com.dellproducts.model.Produto;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import static br.com.dellproducts.enums.Http.*;

public abstract class DellAPIService {
    static Gson gson = new Gson();
    static DellAPI api = new DellAPI();

    private static HttpResponse<String> requestHttp(String urlRequest, String paramsBodyJson, Enum method) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        if (method.equals(GET)) {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(urlRequest))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());

        } else if (method.equals(POST)){
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlRequest))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(paramsBodyJson))
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } else if (method.equals(PUT)){
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlRequest))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(paramsBodyJson))
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } else if (method.equals((DELETE))){
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlRequest))
                    .DELETE()
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        return null;
    }


    public static List<Produto> getProdutos() throws IOException, InterruptedException {
        try{
            String urlRequest = api.getHost() + api.getPorta() + api.getContextPath();
            HttpResponse<String> responseAPI = requestHttp(urlRequest, null, GET);
            String listaProdutos = responseAPI.body();
            System.out.println(listaProdutos);
            Type listType = new TypeToken<List<Produto>>(){}.getType();
            return gson.fromJson(listaProdutos, listType);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public static Produto getProduto(int id) throws IOException, InterruptedException {
        String urlRequest = api.getHost() + api.getPorta() + api.getContextPath() + "?id="+id;
        HttpResponse<String> responseAPI = requestHttp(urlRequest, null, GET);
        String listaProdutosJson = responseAPI.body();
        Type listType = new TypeToken<List<Produto>>(){}.getType();
        List<Produto> listaProdutos = gson.fromJson(listaProdutosJson, listType);
        if (listaProdutos != null && !listaProdutos.isEmpty()) {
            return listaProdutos.get(0);
        } else {
            return null;
        }
    }

    public static String postProduto(Produto produto) throws IOException, InterruptedException {
        String params = "{\"produto\": \""+produto.getProduto()+"\", \"categoria\": \""+produto.getCategoria()+"\", \"preco\": "+produto.getPreco()+"}";
        String urlRequest = api.getHost() + api.getPorta() + api.getContextPath();
        HttpResponse<String> responseAPI = requestHttp(urlRequest, params, POST);
        boolean sucesso = false;
        String mensagem = "";

        try {
            sucesso = true;
            mensagem = "Produto " + produto.getProduto() + " salvo com sucesso!";
        } catch (Exception e) {
            mensagem = "Erro interno ao salvar no banco de dados: " + e.getMessage();
            sucesso = false;
        }

        if (sucesso) {
            return mensagem;
        } else {
            return "Erro ao processar a requisição.";
        }
    }

    public static String putProduto(Produto produto) throws IOException, InterruptedException {
        String params =  "{\"produto\": \""+produto.getProduto()+"\", \"categoria\": \""+produto.getCategoria()+"\", \"preco\": "+produto.getPreco()+"}";
        String urlRequest = api.getHost() + api.getPorta() + api.getContextPath() + "?id="+produto.getId();
        HttpResponse<String> responseAPI = requestHttp(urlRequest, params, PUT);
        return responseAPI.body();
    }

    public static String deleteProduto(int id) throws IOException, InterruptedException {
        String urlRequest = api.getHost() + api.getPorta() + api.getContextPath()+"?id="+id;
        HttpResponse<String> responseAPI= requestHttp(urlRequest,null, DELETE);
        return responseAPI.body();
    }
}
