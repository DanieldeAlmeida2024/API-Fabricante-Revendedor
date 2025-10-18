package br.com.produtos.controller;

import br.com.produtos.controller.services.ProdutoService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Pattern;

@WebServlet("/")
public class ProdutoController extends HttpServlet {

    public ProdutoController(){
        super();
    }


    protected boolean produtoMiddleware(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        String sqlInjectionPattern = "(?i).*(' OR 1=1 --|' OR 'a'='a'|; DROP TABLE|; DELETE FROM).*";

        Pattern pattern = Pattern.compile(sqlInjectionPattern, Pattern.CASE_INSENSITIVE);


        String requestURL = request.getRequestURL().toString();

        if (pattern.matcher(requestURL).matches()) {

            System.out.println("SQL Injection detectado na URL: " + requestURL);

            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Conteúdo inválido na URL.");

            return false;

        }


        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {

            String paramName = parameterNames.nextElement();

            String paramValue = request.getParameter(paramName);


            if (paramValue != null && pattern.matcher(paramValue).matches()) {

                System.out.println("SQL Injection detectado no parâmetro '" + paramName + "' com valor: " + paramValue);

                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Conteúdo inválido nos parâmetros da requisição.");

                return false;

            }

        }


        return true;

    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if(produtoMiddleware(request, response)){
            ProdutoService.post(request,response);
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(produtoMiddleware(request, response)){
            ProdutoService.get(request, response);
        }
    }


    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (produtoMiddleware(request, response)){
            ProdutoService.put(request, response);
        }
    }


    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(produtoMiddleware(request, response)) {
            ProdutoService.delete(request, response);
        }
    }
}
