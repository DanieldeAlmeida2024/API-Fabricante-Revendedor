package br.com.dellproducts.controller;
import br.com.dellproducts.controller.service.ProdutoService;
import br.com.dellproducts.model.Produto;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/produto")
public class ProdutoController extends HttpServlet {
    private String indexPath="/WEB-INF/views/index.jsp";
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            List<Produto> responseGetProdutos = ProdutoService.getProdutos(request, response);
            request.setAttribute("getProdutos", responseGetProdutos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher(indexPath);
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        ProdutoService.PostProduto(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        ProdutoService.deleteProduto(request, response);
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        request.setCharacterEncoding("UTF-8");
        ProdutoService.putProduto(request, response);
    }
}
