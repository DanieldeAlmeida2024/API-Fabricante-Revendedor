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

## FR-FUJ v1.2.0 proposta — 12/11/2026

| ID | Mudança | Artefato |
|---|---|---|
| FUJ-HTTP | Rejeitar HTTP não-2xx e JSON inválido, sem log de body; GET ID objeto/lista | `fujioka/.../api/service/DellAPIService.java` |
| FUJ-SERVLET | Mapear falha upstream a 502 e interrupção a 503, sem forward indevido | `fujioka/.../controller/ProdutosDellController.java` |
| FUJ-JSP | Escapar valores em HTML/JSP e gerar dados JS apenas de DOM `dataset` | `fujioka/src/main/webapp/WEB-INF/views/index.jsp` |
| FUJ-ENV | Remover `Dell/.env`, `fujioka/.env` do HEAD, introduzir `.env.example` e `.gitignore` raiz | arquivos config dos módulos Dell/Fujioka |
| FUJ-CI | Maven `clean verify` do Fujioka, testes estáticos JSP + bloqueio de `.env` rastreado | `.github/workflows/produtos-api-ci.yml` |
| FUJ-TEST | Seis testes JUnit3 em HTTP loopback + quatro testes Python estáticos | `fujioka/src/test/`, `tests/test_fujioka_jsp_security.py` |
| FUJ-DOC | Projeto, matriz e PlantUML próprios | `docs/projeto/FUJIOKA_INTEGRACAO_E_JSP.md`, `docs/testes/FUJIOKA_CONTRATOS_JSP.md`, `docs/uml/04_fujioka_catalogo_seguranca.puml` |

PR empilhado sobre #3 (que depende do documental #2), sem deploy/merge. **PENDENTES:** testes JSP/Tomcat reais, autenticação e TLS, fluxo CRUD completo da revenda e externalização efetiva das URLs. Remoção de `.env` do HEAD não reescreve commits antigos. Registrar SHA/resultado CI somente após sua execução.

### Validação confirmada — FR-FUJ v1.2.0

- **HEAD funcional:** `5ef17a7549b6efb21966ff4d1fbe7da446ad6122`.
- [GitHub Actions #43225983805](https://github.com/DanieldeAlmeida2024/API-Fabricante-Revendedor/actions/runs/43225983805) e [#43226000243](https://github.com/DanieldeAlmeida2024/API-Fabricante-Revendedor/actions/runs/43226000243): **SUCCESS**, três Maven WARs (ProdutosAPI, Dell, Fujioka), 18 testes JUnit3 e testes estáticos/documentais Python.
- Na execução inicial do PR #4 o teste de saída JSP sinalizou equivocadamente uma expressão de template literal JavaScript seguro `option.textContent` como JSP EL; oráculo corrigido para rejeitar iteração/EL JSP dentro do script, preservando os escapes HTML do código. Código funcional não foi relaxado.
- **Pendente:** executar JSP/Tomcat real, ataques dinâmicos XSS, integração dos três WARs, externalização efetiva do host, autenticação e TLS. Esta revisão documental gera novo HEAD e deve passar CI novamente antes de merge.
