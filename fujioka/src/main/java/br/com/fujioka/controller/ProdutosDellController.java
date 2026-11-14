package br.com.fujioka.controller;

import br.com.fujioka.model.ProdutoDell;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import static br.com.fujioka.api.service.DellAPIService.getProdutosDell;

@WebServlet("/dell")
public class ProdutosDellController extends HttpServlet {
    private static final String INDEX_PATH = "/WEB-INF/views/index.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final List<ProdutoDell> produtos;
        try {
            produtos = getProdutosDell();
        } catch (InterruptedException e) {
            // Preservar cancelamento e evitar forward de catálogo parcial.
            Thread.currentThread().interrupt();
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                               "Catálogo temporariamente indisponível.");
            return;
        } catch (IOException e) {
            // Erros de dependência não devem virar HTTP 200 ou resposta HTML parcial.
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY,
                               "Não foi possível consultar o catálogo.");
            return;
        }
        request.setAttribute("getProdutos", produtos);
        final RequestDispatcher dispatcher = request.getRequestDispatcher(INDEX_PATH);
        dispatcher.forward(request, response);
    }
}
