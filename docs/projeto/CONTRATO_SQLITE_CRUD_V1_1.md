# API Fabricante–Revendedor — contrato de persistência e consumo HTTP

> **FR-CRUD v1.1.0 proposta** · 12/11/2026 · revisão do fluxo **Dell → ProdutosAPI → SQLite**, sobre PR documental #2. Não é autorização para produção.

## Contexto e topologia
Este repositório mantém três WARs Maven independentes: `ProdutosAPI` publica o cadastro SQLite por Servlet; `Dell` atua como consumidor HTTP e tem páginas administrativas; `fujioka` usa um consumidor diferente e JSP, fora desta mudança. **Não** foi migrado para Spring Boot nem convertido em monorepo com POM pai.

A documentação v1.0.0 identificou um bug reproduzível: `ProdutoDao.getProdutoById` lia colunas de `ResultSet` antes de `next()`; os métodos `insert/update/delete` capturavam `SQLException` e apenas imprimiam stack trace, retornando sucesso aparente; o consumidor Dell montava payload JSON por concatenação e devolvia mensagem de êxito mesmo quando a API retornava 500.

## Novos contratos — ProdutosAPI

| Endpoint | Resultado |
|---|---|
| `GET /` | 200 com array de produtos; consulta falha = 500, não `[]` |
| `GET /?id=N` | 200 com objeto, 404 inexistente, 400 ID inválido, 500 falha JDBC |
| `POST /` | 201 **somente** se `executeUpdate() == 1`; 400 para payload malformado/nome/categoria vazios/preço não positivo ou não finito; falha JDBC = 500 |
| `PUT /?id=N` | 200 quando atualização afeta 1+ registros, 404 se 0 linhas, 400 entrada inválida, 500 falha JDBC |
| `DELETE /?id=N` | 200 se exclui registro, 404 se já não existe, 400 ID inválido, 500 falha JDBC |

A camada DAO agora permite **injeção de `Connection`** apenas no construtor usado para testes/ambientes específicos, sem modificar o banco ou o construtor padrão. Todos os comandos continuam usando `PreparedStatement`. `ResultSet` é fechado por `try-with-resources` e `getProdutoById` devolve `null` quando não há linha, sem objetos com ID zero. `SQLException` vira `IllegalStateException` (com causa) para não apresentar falsa resposta de êxito.

## Novos contratos — consumidor Dell

`DellAPIService` utiliza `Gson.toJson(produto)` para serializar objetos com nomes contendo aspas ou Unicode; usa um `HttpClient` compartilhado com timeouts e valida **status 2xx** antes de reportar sucesso. `POST` devolve uma mensagem de êxito somente se o upstream realmente responder 2xx. `GET /?id=` aceita a resposta atual em forma de **objeto JSON**, além da lista legada, e retorna `null` para `null`/array vazio. Erros HTTP do upstream são propagados como `IOException` sem repassar corpo da API.

O módulo Dell ainda não distingue todos os códigos de erro na sua própria camada Servlet e não está pronto para tráfego público; os testes desta versão validam a biblioteca consumidora por HTTP **local** em memória, sem criar clientes externos. `fujioka` permanece intocado e exige revisão própria.

## Testes e limite de evidência

- JUnit3 (`ProdutosAPI/...`): **6 casos** SQLite `jdbc:sqlite::memory:` cobrem inserção, cursor, registro ausente, update/delete, metacaracteres SQL tratados como dados e erro JDBC que não vira sucesso. Sem criar `produtos.db` no repositório do job.
- JUnit3 (`Dell/...`): **6 casos** usando `HttpServer` local em `127.0.0.1`: serialização válida de aspas/Unicode, 201, 500, GET objeto/lista, 404/PUT/DELETE inválidos e formato inesperado.
- Maven `clean verify` com Java 17 compilando fonte para **Java 11**, WARs separados. Python documental mantém contratos anteriores.
- [CI #43221444058](https://github.com/DanieldeAlmeida2024/API-Fabricante-Revendedor/actions/runs/43221444058): **12/12 testes JUnit, ambos os builds WAR PASS**, 2/2 verificações documentais PASS, no commit `275221ee0a95c6ad45683d75478b31f0f3b88c9c`.

**Não há testes de servlet HTTP end-to-end com Tomcat, fujioka JSP ou banco persistente; não afirmar que estas camadas passaram.** Falta autenticação, autorização, TLS, controle de concorrência, rollback transacional, audit trail e gestão de credenciais por ambiente. E os dois `.env` rastreados contêm configurações de endpoint (host/porta/caminho), não senhas ou tokens identificados nesta revisão — ainda devem migrar para arquivos de exemplo por higiene.

[Relatório de testes](../testes/EXECUCAO_JDBC_HTTP_LOCAL.md) · [UML](../uml/03_fluxo_crud_dell_sqlite.puml) · [Controle de versão](../controle/VERSIONAMENTO.md).
