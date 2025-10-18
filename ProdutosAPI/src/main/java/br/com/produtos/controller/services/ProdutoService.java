package br.com.produtos.controller.services;

import br.com.produtos.model.Produto;
import br.com.produtos.persistence.ProdutoDao;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public abstract class ProdutoService {
    private static final Gson gson = new Gson();
    static ProdutoDao dao = new ProdutoDao();
    public static void get(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //GET Se passar o parametro ID
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try{
                Produto produto = dao.getProdutoById(Integer.parseInt(request.getParameter("id")));
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String jsonResponse = gson.toJson(produto);
                response.getWriter().write(jsonResponse);
            } catch (NumberFormatException | IOException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                try {
                    response.getWriter().write("{\"error\":\"ID inválido ou erro interno.\"}");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                e.printStackTrace();
            }
        } else { //GET Se não passar, Busca todos os registros
            try {
                List<Produto> produtos = dao.getAllProdutos();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                Gson gson = new Gson();
                String jsonResponse = gson.toJson(produtos);
                response.getWriter().write(jsonResponse);
            } catch (NumberFormatException | IOException e){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                try {
                    response.getWriter().write("{\"error\":\"erro interno.\"}");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }
        public static void post(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            request.setCharacterEncoding("UTF-8");

            String jsonBody = null;
            try (BufferedReader reader = request.getReader()) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                jsonBody = sb.toString();
                jsonBody = jsonBody.trim();
                Produto produto = gson.fromJson(jsonBody, Produto.class);

                System.out.println("Produto: " + produto.getNomeProduto() + " Categoria: " + produto.getCategoria() + " Preço: " + produto.getPreco());

                if (produto != null && produto.getNomeProduto() != null && produto.getCategoria() != null && produto.getPreco() > 0) {
                    dao.insertProduto(produto);
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    response.getWriter().write(gson.toJson("Produto adicionado com sucesso."));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(gson.toJson("Dados do produto inválidos."));
                }
            } catch (com.google.gson.JsonSyntaxException e) {
                System.err.println("Erro de sintaxe JSON. Body: [" + jsonBody + "]");
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson("Erro: O JSON enviado está malformado."));
            } catch (Exception e) {
                System.err.println("Erro inesperado no servidor: " + e.getMessage());
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson("Erro ao processar a requisição."));
            }
        }

    public static void put(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        int idProduto = Integer.parseInt(request.getParameter("id"));
        if (idProduto == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson("O parâmetro 'ID' é obrigatório."));
            return;
        }

        String jsonBody = null;
        try (BufferedReader reader = request.getReader()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            jsonBody = sb.toString();
            jsonBody = jsonBody.trim();
            Produto produtoAtualizado = gson.fromJson(jsonBody, Produto.class);
            if (produtoAtualizado != null && produtoAtualizado.getNomeProduto() != null && produtoAtualizado.getCategoria() != null && produtoAtualizado.getPreco() > 0) {
                dao.updateProduto(idProduto, produtoAtualizado);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(gson.toJson("Produto atualizado com sucesso."));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson("Dados do produto inválidos."));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson("Erro ao processar a requisição."));
        }
    }
    public static void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        int idProduto = Integer.parseInt(request.getParameter("id"));
        if (idProduto == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson("O parâmetro 'ID' é obrigatório para exclusão."));
            return;
        }

        try {
            dao.deleteProduto(idProduto);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson("Produto excluído com sucesso."));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson("Erro ao excluir o produto."));
        }
    }
}


