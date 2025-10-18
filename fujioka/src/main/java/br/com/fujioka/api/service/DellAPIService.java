package br.com.fujioka.api.service;
import br.com.fujioka.api.DellAPI;
import br.com.fujioka.model.ProdutoDell;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import static br.com.fujioka.enums.Http.*;

public abstract class DellAPIService {
    private static final Gson gson = new Gson();
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

        }
        return null;
    }


    private static ProdutoDell getProdutoDell(int id) throws IOException, InterruptedException {
        String urlRequest = api.getHost() + api.getPorta() + api.getContextPath() + "?id="+id;
        HttpResponse<String> responseAPI = requestHttp(urlRequest, null, GET);
        String listaProdutosJson = responseAPI.body();
        Type listType = new TypeToken<List<ProdutoDell>>(){}.getType();
        List<ProdutoDell> listaProdutos = gson.fromJson(listaProdutosJson, listType);
        if (listaProdutos != null && !listaProdutos.isEmpty()) {
            return listaProdutos.get(0);
        } else {
            return null;
        }
    }

    public static List<ProdutoDell> getProdutosDell() throws IOException, InterruptedException {
        try{
            String urlRequest = api.getHost() + api.getPorta() + api.getContextPath();
            HttpResponse<String> responseAPI = requestHttp(urlRequest, null, GET);
            String listaProdutos = responseAPI.body();
            System.out.println(listaProdutos);
            Type listType = new TypeToken<List<ProdutoDell>>(){}.getType();
            return gson.fromJson(listaProdutos, listType);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    public static final List<ProdutoDell> getProdutos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if(idParam != null && !idParam.isEmpty()){
            try {
                return Collections.singletonList(getProdutoDell(Integer.parseInt(idParam)));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return getProdutosDell();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    */
}
