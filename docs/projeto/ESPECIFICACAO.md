# Três aplicações Java: ProdutosAPI, fabricante Dell e revendedor Fujioka

**Versão 1.0.0** (11/11/2026), código `1cb5d55aa6aae36d4a27709c5573c5450c17f9a4`. Auditoria estática dos módulos; testes de integração não executados nesta revisão.

## Requisitos de engenharia e comportamento observado
A arquitetura deste repositório é composta por **três módulos Maven/Servlet/JSP**, com `pom.xml` independentes. `ProdutosAPI` publica CRUD de produtos por `ProdutoController @WebServlet("/")`, `ProdutoService` e `ProdutoDao` com JDBC. `Dell` é um cliente Java HTTP que consome a API, usando `DellAPIService` com `HttpClient`, enum `Http` e Gson para serializar/deserializar produtos. `fujioka` monta uma interface JSP e `ProdutosDellController @WebServlet("/dell")` carrega produtos via cliente Dell e faz `forward` a `/WEB-INF/views/index.jsp`.

O DAO tem SQL com parâmetros `?` em INSERT/SELECT/UPDATE/DELETE, bom para evitar concatenação de entrada. Porém `getProdutoById(int)` chama `resultSet.getInt/getString` sem `resultSet.next()`, causando leitura incorreta ou exceção. O `ProdutoController` filtra um pequeno conjunto de substrings de SQL injection em URL/params; **blacklist regex não é defesa suficiente** e sua telemetria imprime parâmetros rejeitados; o controle principal deve estar em `PreparedStatement`, validação e permissões do banco.

`DellAPIService.postProduto` monta JSON por concatenação de strings e define `sucesso=true` independentemente do status HTTP, podendo relatar cadastro bem-sucedido quando a API recusou. O mesmo método deve tratar resposta/erro com estrutura de dados e status. Arquivos `Dell/.env` e `fujioka/.env` aparecem no Git: não incluir seu conteúdo nesta documentação; auditar segredos e, se reais, rotacionar.

Não transformar estes três WARs em uma aplicação Spring Boot única sem nova decisão de arquitetura. O fluxo técnico é fabricante/client HTTP → API de produtos → banco, e revendedor JSP exibe o catálogo consumido.

## Como verificar um subprojeto
```bash
cd ProdutosAPI && mvn test
```

Outros módulos têm comandos próprios; consultar seu `pom.xml` antes de executar. A matriz de cenários está em [testes](../testes/PLANO_DE_TESTES.md) e os diagramas em [UML](../uml/README.md).

## Atualização FR-CRUD v1.1.0 — contrato implementado de SQLite e cliente Dell

Os problemas de `ResultSet` e de sucesso falso documentados na v1.0.0 foram tratados nesta branch proposta: o DAO usa `next()`, retorna `null` quando não há ID, informa linhas modificadas e propaga `SQLException`; a camada Servlet responde status coerentes, e o consumidor Dell usa Gson + validação de 2xx. [Detalhamento próprio](CONTRATO_SQLITE_CRUD_V1_1.md). Os `.env` rastreados foram examinados **por nomes de campos** e contêm parâmetros de endpoint/porta, sem senha/token identificado; ainda devem sair do versionamento em revisão separada.
