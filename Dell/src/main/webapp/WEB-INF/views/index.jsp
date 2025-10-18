<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import= "br.com.dellproducts.model.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<!DOCTYPE HTML>
<html language='pt-Br'>
<head>
    <meta charset="utf-8">
    <title>Dell</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">

    <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>

    <link rel="stylesheet" href="./css/style.css">
</head>
<body>

    <div class="container mt-5">
        <h1>Módulo de Produtos Dell</h1>

        <table class="table table-striped">
            <thead class="thead-dark">
                <tr>
                    <th scope="col">ID:</th>
                    <th scope="col">Produto:</th>
                    <th scope="col">Categoria:</th>
                    <th scope="col">Preço:</th>
                    <th scope="col">Ações</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="produto" items="${getProdutos}">
                <tr>
                    <th scope="row">${produto.getId()}</th>
                    <td>${produto.getProduto()}</td>
                    <td>${produto.getCategoria()}</td>
                    <td>R$ ${produto.getPreco()}</td>
                    <td>
                        <button class="btn btn-warning btn-sm" data-product-id="${produto.getId()}">Alterar</button>
                        <button class="btn btn-danger btn-sm delete-btn" data-product-id="${produto.getId()}">Excluir</button>
                    </td>
                </tr>
                </c:forEach>
            </tbody>
        </table>

        <button id="openModalBtn" class="btn btn-primary mb-3">Adicionar Novo Produto</button>

        <div id="productModal" class="modal">

            <div class="modal-content">
                <span class="close-btn">&times;</span>
                <h2>Novo Produto</h2>

                <form id="productForm" action="/produto" method="POST">
                    <div class="form-group">
                        <label for="produto">Produto:</label>
                        <input type="text" class="form-control" id="produto" name="produto" required>
                    </div>
                    <div class="form-group">
                        <label for="categoria">Categoria:</label>
                        <input type="text" class="form-control" id="categoria" name="categoria" required>
                    </div>
                    <div class="form-group">
                        <label for="preco">Preço (R$):</label>
                        <input type="number" step="0.01" class="form-control" id="preco" name="preco" required>
                    </div>
                    <button type="submit" class="btn btn-success">Salvar Produto</button>
                </form>
                </div>
        </div>
    </div>
</body>
    <script>
        const baseUrl = '<%= request.getContextPath() %>';
    </script>
    <script src="./js/HttpMethods.js"></script>
</html>