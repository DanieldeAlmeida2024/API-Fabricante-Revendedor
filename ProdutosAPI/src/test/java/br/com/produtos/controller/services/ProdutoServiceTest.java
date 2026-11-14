package br.com.produtos.controller.services;

import br.com.produtos.model.Produto;
import br.com.produtos.persistence.ProdutoDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {
    ProdutoDao dao;
    ProdutoService service;
    HttpServletRequest request;
    HttpServletResponse response;
    StringWriter body;
    PrintWriter writer;

    @BeforeEach
    void preparar() throws Exception {
        dao = mock(ProdutoDao.class);
        service = new ProdutoService(dao);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        body = new StringWriter();
        writer = new PrintWriter(body, true);
        when(response.getWriter()).thenReturn(writer);
    }

    void json(String texto) throws Exception {
        when(request.getContentType()).thenReturn("application/json; charset=utf-8");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(texto)));
    }

    @Test
    void consultaIdInexistenteRetorna404() throws Exception {
        when(request.getParameter("id")).thenReturn("42");
        when(dao.getProdutoById(42)).thenReturn(null);
        service.get(request, response);
        verify(response).setStatus(404);
        writer.flush();
        assertTrue(body.toString().contains("Produto não encontrado"));
    }

    @Test
    void consultaIdValidoRetornaObjetoJson() throws Exception {
        when(request.getParameter("id")).thenReturn("1");
        Produto produto = new Produto();
        produto.setId(1);
        produto.setNomeProduto("Teclado");
        produto.setCategoria("Acessórios");
        produto.setPreco(49.90);
        when(dao.getProdutoById(1)).thenReturn(produto);
        service.get(request, response);
        verify(response).setStatus(200);
        writer.flush();
        assertTrue(body.toString().contains("Teclado"));
    }

    @Test
    void listaVaziaNaoRetornaFalha() throws Exception {
        when(dao.getAllProdutos()).thenReturn(List.of());
        service.get(request, response);
        verify(response).setStatus(200);
        writer.flush();
        assertEquals("[]", body.toString());
    }

    @Test
    void idInvalidoGera400SemConsultarBanco() throws Exception {
        for (String id : List.of("0", "-5", "abc", "2147483648", "")) {
            assertNull(ProdutoService.parseId(id), "ID inválido: " + id);
        }
        when(request.getParameter("id")).thenReturn("abc");
        service.get(request, response);
        verify(response).setStatus(400);
        verifyNoInteractions(dao);
    }

    @Test
    void criaProdutoValidoERetorna201() throws Exception {
        json("{\"produto\":\"Mouse\",\"categoria\":\"Periféricos\",\"preco\":125.5}");
        service.post(request, response);
        verify(dao).insertProduto(argThat(produto ->
            produto.getNomeProduto().equals("Mouse") &&
            produto.getCategoria().equals("Periféricos") &&
            produto.getPreco() == 125.5
        ));
        verify(response).setStatus(201);
    }

    @Test
    void jsonCorrompidoEPrecoInfinitoGeram400() throws Exception {
        json("{\"produto\":");
        service.post(request, response);
        verify(response).setStatus(400);
        verifyNoInteractions(dao);
    }

    @Test
    void precoInfinitoEhInvalidoMesmoNoDominio() {
        Produto produto = new Produto();
        produto.setNomeProduto("Monitor");
        produto.setCategoria("Eletrônicos");
        produto.setPreco(Double.POSITIVE_INFINITY);
        assertThrows(IllegalArgumentException.class, () -> ProdutoService.validarProduto(produto));
        produto.setPreco(Double.NaN);
        assertThrows(IllegalArgumentException.class, () -> ProdutoService.validarProduto(produto));
    }

    @Test
    void semTipoJsonRetorna415() throws Exception {
        when(request.getContentType()).thenReturn("text/plain");
        service.post(request, response);
        verify(response).setStatus(415);
        verifyNoInteractions(dao);
    }

    @Test
    void bodyMaiorQueLimiteRetorna413SemInserir() throws Exception {
        json("a".repeat(66_000));
        service.post(request, response);
        verify(response).setStatus(413);
        verifyNoInteractions(dao);
    }

    @Test
    void atualizacaoAusenteRetorna404() throws Exception {
        when(request.getParameter("id")).thenReturn("99");
        json("{\"produto\":\"Notebook\",\"categoria\":\"Computadores\",\"preco\":999.9}");
        when(dao.updateProduto(eq(99), any(Produto.class))).thenReturn(false);
        service.put(request, response);
        verify(response).setStatus(404);
    }

    @Test
    void deletarAusenteRetorna404EOExistente204() throws Exception {
        when(request.getParameter("id")).thenReturn("99");
        when(dao.deleteProduto(99)).thenReturn(false);
        service.delete(request, response);
        verify(response).setStatus(404);

        reset(response);
        when(dao.deleteProduto(99)).thenReturn(true);
        service.delete(request, response);
        verify(response).setStatus(204);
        verify(response, never()).getWriter();
    }

    @Test
    void falhaDeBancoRetorna500SemExporDetalhes() throws Exception {
        when(dao.getAllProdutos()).thenThrow(new IllegalStateException("connection error jdbc:sqlite:secret"));
        service.get(request, response);
        verify(response).setStatus(500);
        writer.flush();
        assertFalse(body.toString().contains("jdbc:sqlite:secret"));
    }
}
