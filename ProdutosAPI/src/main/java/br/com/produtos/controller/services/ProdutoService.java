package br.com.produtos.controller.services;

import br.com.produtos.model.Produto;
import br.com.produtos.persistence.ProdutoDao;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Regras HTTP do catálogo. Os clientes Dell/Fujioka continuam usando o contrato JSON. */
public final class ProdutoService {
    private static final Gson GSON = new Gson();
    private static final Logger LOG = Logger.getLogger(ProdutoService.class.getName());
    private static final int MAX_BODY_CHARS = 65_536;

    private final ProdutoDao dao;

    public ProdutoService() {
        this(new ProdutoDao());
    }

    public ProdutoService(ProdutoDao dao) {
        this.dao = Objects.requireNonNull(dao);
    }

    public void get(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            Integer id = parseId(idParam);
            if (id == null) {
                json(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("erro", "ID inválido."));
                return;
            }
            try {
                Produto produto = dao.getProdutoById(id);
                if (produto == null) {
                    json(response, HttpServletResponse.SC_NOT_FOUND,
                            Map.of("erro", "Produto não encontrado."));
                    return;
                }
                json(response, HttpServletResponse.SC_OK, produto);
            } catch (IllegalStateException error) {
                internalError(response, error);
            }
            return;
        }
        try {
            List<Produto> produtos = dao.getAllProdutos();
            json(response, HttpServletResponse.SC_OK, produtos);
        } catch (IllegalStateException error) {
            internalError(response, error);
        }
    }

    public void post(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Produto produto = readProduto(request, response);
        if (produto == null) return;
        try {
            dao.insertProduto(produto);
            json(response, HttpServletResponse.SC_CREATED,
                    Map.of("mensagem", "Produto adicionado com sucesso."));
        } catch (IllegalStateException error) {
            internalError(response, error);
        }
    }

    public void put(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = parseId(request.getParameter("id"));
        if (id == null) {
            json(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("erro", "ID inválido."));
            return;
        }
        Produto produto = readProduto(request, response);
        if (produto == null) return;
        try {
            if (!dao.updateProduto(id, produto)) {
                json(response, HttpServletResponse.SC_NOT_FOUND,
                        Map.of("erro", "Produto não encontrado."));
                return;
            }
            json(response, HttpServletResponse.SC_OK,
                    Map.of("mensagem", "Produto atualizado com sucesso."));
        } catch (IllegalStateException error) {
            internalError(response, error);
        }
    }

    public void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer id = parseId(request.getParameter("id"));
        if (id == null) {
            json(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("erro", "ID inválido."));
            return;
        }
        try {
            if (!dao.deleteProduto(id)) {
                json(response, HttpServletResponse.SC_NOT_FOUND,
                        Map.of("erro", "Produto não encontrado."));
                return;
            }
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (IllegalStateException error) {
            internalError(response, error);
        }
    }

    static Integer parseId(String valor) {
        if (valor == null || !valor.matches("[0-9]{1,10}")) return null;
        try {
            int id = Integer.parseInt(valor);
            return id > 0 ? id : null;
        } catch (NumberFormatException error) {
            return null;
        }
    }

    private Produto readProduto(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String contentType = request.getContentType();
        if (contentType == null ||
                !contentType.toLowerCase(java.util.Locale.ROOT).split(";", 2)[0].trim()
                        .equals("application/json")) {
            json(response, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                    Map.of("erro", "Envie Content-Type: application/json."));
            return null;
        }

        StringBuilder body = new StringBuilder();
        try (Reader reader = request.getReader()) {
            char[] chunk = new char[4096];
            int read;
            while ((read = reader.read(chunk)) != -1) {
                if (body.length() + read > MAX_BODY_CHARS) {
                    json(response, HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
                            Map.of("erro", "Corpo JSON acima de 65536 caracteres."));
                    return null;
                }
                body.append(chunk, 0, read);
            }
        }
        try {
            Produto produto = GSON.fromJson(body.toString(), Produto.class);
            validarProduto(produto);
            produto.setNomeProduto(produto.getNomeProduto().trim());
            produto.setCategoria(produto.getCategoria().trim());
            return produto;
        } catch (JsonParseException | IllegalArgumentException error) {
            json(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("erro", "Dados do produto ou JSON inválidos."));
            return null;
        }
    }

    static void validarProduto(Produto produto) {
        if (produto == null || produto.getNomeProduto() == null ||
                produto.getCategoria() == null ||
                produto.getNomeProduto().isBlank() || produto.getCategoria().isBlank() ||
                produto.getNomeProduto().length() > 150 ||
                produto.getCategoria().length() > 100 ||
                !Double.isFinite(produto.getPreco()) || produto.getPreco() <= 0) {
            throw new IllegalArgumentException("Produto inválido.");
        }
    }

    private static void json(HttpServletResponse response, int status, Object dados)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.getWriter().write(GSON.toJson(dados));
    }

    private static void internalError(HttpServletResponse response, IllegalStateException error)
            throws IOException {
        LOG.log(Level.SEVERE, "Falha de persistência no catálogo de produtos.", error);
        json(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Map.of("erro", "Falha interna ao processar a solicitação."));
    }
}
