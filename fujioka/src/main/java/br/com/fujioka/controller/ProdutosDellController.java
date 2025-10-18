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
    private String indexPath="/WEB-INF/views/index.jsp";
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            List<ProdutoDell> responseGetProdutos = getProdutosDell();
            request.setAttribute("getProdutos", responseGetProdutos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher(indexPath);
        dispatcher.forward(request, response);
    }
}
