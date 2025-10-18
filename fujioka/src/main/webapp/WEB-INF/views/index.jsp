<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import= "br.com.dellproducts.model.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<!DOCTYPE HTML>
<html language='pt-Br'>
<head>
    <meta charset="utf-8">
    <title>Detalhes do Produto Dell</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">

    <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>

    <link rel="stylesheet" href="./css/style.css">
    <style>
        .search-container {
            margin-bottom: 20px;
        }
    </style>
</head>
<body>

    <div class="container mt-5">
        <h1>Detalhes e Edição de Produtos Dell</h1>

        <div class="search-container">
            <input type="text" id="searchProduto" class="form-control" placeholder="Pesquisar produto pelo SN,MAC,DellID">
        </div>

        <div class="form-group">
            <label for="selectProduto">Selecione um Produto:</label>
            <select class="form-control" id="selectProduto" onchange="exibirDetalhesProduto()">
                <option value="">-- Selecione um Produto --</option>
                <c:forEach var="produto" items="${getProdutos}">
                    <option
                        value="${produto.getId()}"
                        data-id="${produto.getId()}"
                        data-produto="${produto.getProduto()}"
                        data-categoria="${produto.getCategoria()}"
                        data-preco="${produto.getPreco()}"
                    >
                        ${produto.getId()} - ${produto.getProduto()} (${produto.getCategoria()})
                    </option>
                </c:forEach>
            </select>
        </div>

        <hr>

        <form id="detalhesProdutoForm" class="mt-4" style="display: none;" action="/produto/alterar" method="POST">
            <h2>Informações do Produto Selecionado</h2>

            <input type="hidden" id="produtoId" name="id">

            <div class="form-group">
                <label for="produtoNome">Produto:</label>
                <input type="text" class="form-control" id="produtoNome" name="produto" disabled>
            </div>
            <div class="form-group">
                <label for="produtoCategoria">Categoria:</label>
                <input type="text" class="form-control" id="produtoCategoria" name="categoria" disabled>
            </div>
            <div class="form-group">
                <label for="produtoPreco">Preço (R$):</label>
                <input type="number" step="0.01" class="form-control" id="produtoPreco" name="preco" disabled>
            </div>
        </form>
    </div>

    <script>
        const produtosData = [
            <c:forEach var="produto" items="${getProdutos}" varStatus="status">
                {
                    id: ${produto.getId()},
                    produto: "${produto.getProduto()}",
                    categoria: "${produto.getCategoria()}",
                    preco: ${produto.getPreco()}
                }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];

        function exibirDetalhesProduto() {
            const select = document.getElementById('selectProduto');
            const form = document.getElementById('detalhesProdutoForm');
            const selectedOption = select.options[select.selectedIndex];

            if (selectedOption.value) {
                document.getElementById('produtoId').value = selectedOption.getAttribute('data-id');
                document.getElementById('produtoNome').value = selectedOption.getAttribute('data-produto');
                document.getElementById('produtoCategoria').value = selectedOption.getAttribute('data-categoria');
                document.getElementById('produtoPreco').value = parseFloat(selectedOption.getAttribute('data-preco')).toFixed(2);

                form.style.display = 'block';
                form.action = `/produto/alterar?id=${selectedOption.value}`;
            } else {
                form.style.display = 'none';
            }
        }

        document.getElementById('searchProduto').addEventListener('keyup', function() {
            const searchTerm = this.value.toLowerCase();
            const select = document.getElementById('selectProduto');

            const options = Array.from(select.options);
            options.forEach((option, index) => {
                if (index > 0) {
                    select.removeChild(option);
                }
            });

            document.getElementById('detalhesProdutoForm').style.display = 'none';

            const placeholderOption = options[0];
            placeholderOption.selected = true;

            if (select.options.length === 0 || select.options[0].value !== "") {
                select.appendChild(placeholderOption);
            }

            produtosData.filter(produto =>
                produto.produto.toLowerCase().includes(searchTerm) ||
                produto.categoria.toLowerCase().includes(searchTerm) ||
                produto.id.toString().includes(searchTerm)
            ).forEach(produto => {
                const option = document.createElement('option');
                option.value = produto.id;
                option.textContent = `${produto.id} - ${produto.produto} (${produto.categoria})`;
                option.setAttribute('data-id', produto.id);
                option.setAttribute('data-produto', produto.produto);
                option.setAttribute('data-categoria', produto.categoria);
                option.setAttribute('data-preco', produto.preco.toFixed(2));
                select.appendChild(option);
            });
        });

        function confirmarExclusao() {
            const productId = document.getElementById('produtoId').value;
            if (productId && confirm("Tem certeza de que deseja excluir este produto?")) {
                alert(`Simulando exclusão do produto ID: ${productId}. O backend deve ser chamado via POST/DELETE para /produto/excluir.`);
            }
        }
    </script>
</html>