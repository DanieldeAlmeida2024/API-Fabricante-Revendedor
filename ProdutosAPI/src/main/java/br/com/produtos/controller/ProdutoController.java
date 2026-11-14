package br.com.produtos.controller;

import br.com.produtos.controller.services.ProdutoService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Encaminha requisições para o serviço.
 * Segurança contra SQL Injection depende de SQL parametrizado no DAO,
 * não de regex que bloqueia nomes legítimos de produtos.
 */
@WebServlet("/")
public final class ProdutoController extends HttpServlet {
    private final ProdutoService produtos = new ProdutoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        produtos.get(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        produtos.post(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        produtos.put(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        produtos.delete(request, response);
    }
}
