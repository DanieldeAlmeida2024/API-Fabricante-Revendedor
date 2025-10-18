package br.com.dellproducts.controller.service;

import br.com.dellproducts.api.service.DellAPIService;
import br.com.dellproducts.model.Produto;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public abstract class ProdutoService {
    private static String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        request.setCharacterEncoding("UTF-8");

        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }


    private static final Gson gson = new Gson();
    public static final List<Produto> getProdutos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if(idParam != null && !idParam.isEmpty()){
            try {
                return Collections.singletonList(DellAPIService.getProduto(Integer.parseInt(idParam)));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return DellAPIService.getProdutos();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void PostProduto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String respostaFinal = "Erro inesperado ao processar a requisição.";
        try {
            String jsonBody = getRequestBody(request);
            Produto produto = gson.fromJson(jsonBody, Produto.class);
            if (produto != null && produto.getProduto() != null && produto.getPreco() > 0) {
                respostaFinal = DellAPIService.postProduto(produto);
                response.setStatus(HttpServletResponse.SC_OK);

            } else {
                respostaFinal = "Dados de entrada incompletos ou inválidos.";
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Erro de sintaxe JSON: " + e.getMessage());
            respostaFinal = "Erro no formato JSON: Verifique chaves e valores.";
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
        } catch (Exception e) {
            System.err.println("Erro ao salvar produto: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
        }

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(respostaFinal);
    }

    public static final void putProduto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (BufferedReader reader = request.getReader()) {
            Produto produto = gson.fromJson(reader, Produto.class);
            if (produto != null && produto.getProduto() != null && produto.getCategoria() != null && produto.getPreco() > 0) {
                String respostaAPI =  DellAPIService.putProduto(produto);
                response.getWriter().write(respostaAPI);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Dados do produto inválidos.");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erro ao processar a requisição.");
        }
    }

    public static final void deleteProduto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int idProduto = Integer.parseInt(request.getParameter("id"));
        if (idProduto == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson("O parâmetro 'ID' é obrigatório para exclusão."));
            return;
        }

        try {
            DellAPIService.deleteProduto(idProduto);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson("Produto excluído com sucesso."));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson("Erro ao excluir o produto."));
        }
    }
}

