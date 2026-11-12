# Versionamento — API-Fabricante-Revendedor

**v1.0.0** · 11/11/2026 · SHA `1cb5d55aa6aae36d4a27709c5573c5450c17f9a4`.

[Especificação](../projeto/ESPECIFICACAO.md) · [UML](../uml/README.md) · [Testes](../testes/PLANO_DE_TESTES.md).

| Documento | Versão | Base |
|---|---|---|
| PROJ-API-Fabricante-Revendedor | 1.0.0 | `1cb5d55aa6aa` |
| UML-API-Fabricante-Revendedor | 1.0.0 | `1cb5d55aa6aa` |
| TST-API-Fabricante-Revendedor | 1.0.0 | `1cb5d55aa6aa` |

MAJOR para contratos incompatíveis, MINOR para módulo/fluxo novo, PATCH para correção editorial. Cada PR funcional deve atualizar o caso de teste e sua evidência.

## FR-CRUD v1.1.0 proposta — 12/11/2026

| ID | Alteração | Artefatos |
|---|---|---|
| FR-SQL | Regressão `ResultSet.next()`, ausência de ID e recursos JDBC fechados | `ProdutosAPI/.../ProdutoDao.java` |
| FR-WRITE | `executeUpdate` passa a retornar quantidade; falha SQL propagada em vez de log + sucesso | `ProdutoDao.java` |
| FR-HTTP | Códigos 201/400/404/500 e validação de payload coerentes com contagem DAO | `ProdutosAPI/.../ProdutoService.java` |
| FR-DELL | Gson, resposta GET objeto/lista e verificação obrigatória de HTTP 2xx | `Dell/.../DellAPIService.java` |
| FR-JUNIT | 6 testes SQLite in-memory e 6 testes HttpServer loopback | `ProdutosAPI/src/test/` e `Dell/src/test/` |
| FR-MVN | Java 11 compilado com Maven moderno no JDK 17, WAR separado por módulo | `ProdutosAPI/pom.xml`, `Dell/pom.xml`, `.github/workflows/produtos-api-ci.yml` |
| FR-DOC | Arquitetura própria, matriz de teste executada, UML editável | `docs/projeto/CONTRATO_SQLITE_CRUD_V1_1.md`, `docs/testes/EXECUCAO_JDBC_HTTP_LOCAL.md`, `docs/uml/03_fluxo_crud_dell_sqlite.puml` |

Branch `fix/produtosapi-crud-sqlite-errors-20261112` empilhada sobre PR #2 (documentação própria). [CI #43221444058](https://github.com/DanieldeAlmeida2024/API-Fabricante-Revendedor/actions/runs/43221444058) **12 JUnit3 PASS, dois Maven WAR PASS**, commit `275221ee0a95c6ad45683d75478b31f0f3b88c9c`. Ainda **não** houve deploy/Teste E2E Tomcat, Fujioka ou migração de banco; todos pendentes. Revisar CI novamente no commit final de documentação.
