package br.com.produtos.controller.services;

import br.com.produtos.model.Produto;
import br.com.produtos.persistence.ProdutoDao;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public abstract class ProdutoService {
    private static final Gson gson = new Gson();
    private static final ProdutoDao dao = new ProdutoDao();

    private static boolean validProduto(Produto p) {
        return p != null &&
               p.getNomeProduto() != null && !p.getNomeProduto().trim().isEmpty() &&
               p.getCategoria() != null && !p.getCategoria().trim().isEmpty() &&
               Double.isFinite(p.getPreco()) && p.getPreco() > 0.0;
    }

    private static int parseId(String value) {
        final int id = Integer.parseInt(value);
        if (id <= 0) throw new NumberFormatException("ID deve ser positivo");
        return id;
    }

    private static void respond(HttpServletResponse response, int status, String payload)
            throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(payload);
    }

    private static void error(HttpServletResponse response, int status, String message)
            throws IOException {
        respond(response, status, gson.toJson(java.util.Collections.singletonMap("error", message)));
    }

    public static void get(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            final String rawId = request.getParameter("id");
            if (rawId != null) {
                final Produto produto = dao.getProdutoById(parseId(rawId));
                if (produto == null) {
                    error(response, HttpServletResponse.SC_NOT_FOUND, "Produto não encontrado.");
                } else {
                    respond(response, HttpServletResponse.SC_OK, gson.toJson(produto));
                }
            } else {
                final List<Produto> produtos = dao.getAllProdutos();
                respond(response, HttpServletResponse.SC_OK, gson.toJson(produtos));
            }
        } catch (NumberFormatException e) {
            error(response, HttpServletResponse.SC_BAD_REQUEST, "ID de produto inválido.");
        } catch (RuntimeException e) {
            error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Falha ao consultar produtos.");
        }
    }

    public static void post(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            final Produto produto = gson.fromJson(request.getReader(), Produto.class);
            if (!validProduto(produto)) {
                error(response, HttpServletResponse.SC_BAD_REQUEST, "Dados do produto inválidos.");
                return;
            }
            final int changed = dao.insertProduto(produto);
            if (changed != 1) {
                error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Falha ao criar produto.");
                return;
            }
            respond(response, HttpServletResponse.SC_CREATED,
                gson.toJson(java.util.Collections.singletonMap("message", "Produto criado.")));
        } catch (JsonSyntaxException e) {
            error(response, HttpServletResponse.SC_BAD_REQUEST, "JSON inválido.");
        } catch (RuntimeException e) {
            error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Falha ao criar produto.");
        }
    }

    public static void put(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        final int id;
        try {
            id = parseId(request.getParameter("id"));
        } catch (NumberFormatException e) {
            error(response, HttpServletResponse.SC_BAD_REQUEST, "ID de produto inválido.");
            return;
        }
        try {
            final Produto produto = gson.fromJson(request.getReader(), Produto.class);
            if (!validProduto(produto)) {
                error(response, HttpServletResponse.SC_BAD_REQUEST, "Dados do produto inválidos.");
                return;
            }
            final int changed = dao.updateProduto(id, produto);
            if (changed == 0) {
                error(response, HttpServletResponse.SC_NOT_FOUND, "Produto não encontrado.");
                return;
            }
            respond(response, HttpServletResponse.SC_OK,
                gson.toJson(java.util.Collections.singletonMap("message", "Produto atualizado.")));
        } catch (JsonSyntaxException e) {
            error(response, HttpServletResponse.SC_BAD_REQUEST, "JSON inválido.");
        } catch (RuntimeException e) {
            error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Falha ao atualizar produto.");
        }
    }

    public static void delete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final int id;
        try {
            id = parseId(request.getParameter("id"));
        } catch (NumberFormatException e) {
            error(response, HttpServletResponse.SC_BAD_REQUEST, "ID de produto inválido.");
            return;
        }
        try {
            final int changed = dao.deleteProduto(id);
            if (changed == 0) {
                error(response, HttpServletResponse.SC_NOT_FOUND, "Produto não encontrado.");
                return;
            }
            respond(response, HttpServletResponse.SC_OK,
                gson.toJson(java.util.Collections.singletonMap("message", "Produto excluído.")));
        } catch (RuntimeException e) {
            error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Falha ao excluir produto.");
        }
    }
}
