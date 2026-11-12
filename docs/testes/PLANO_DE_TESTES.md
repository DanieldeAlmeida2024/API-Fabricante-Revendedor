# Testes — Três aplicações Java: ProdutosAPI, fabricante Dell e revendedor Fujioka

**Versão 1.0.0** (11/11/2026), código `1cb5d55aa6aae36d4a27709c5573c5450c17f9a4`. Auditoria estática dos módulos; testes de integração não executados nesta revisão.

| ID | Cenário | Critério de aceitação | Situação |
|---|---|---|---|
| FR-01 | ProdutosAPI POST / | PreparedStatement persiste produto | Ainda sem execução neste PR |
| FR-02 | ProdutosAPI GET / | Lista Produto com ID/preço corretos | Ainda sem execução neste PR |
| FR-03 | ProdutosAPI consulta ID | Corrigir e testar ResultSet.next() | Ainda sem execução neste PR |
| FR-04 | PUT ID inexistente | Resposta sem falso sucesso | Ainda sem execução neste PR |
| FR-05 | DELETE existente/inexistente | Sem efeitos em outro ID | Ainda sem execução neste PR |
| FR-06 | Payload com aspas e ponto e vírgula | SQL parametrizado não executa comando | Ainda sem execução neste PR |
| FR-07 | DellAPIService POST API 400/500 | Não retornar mensagem de sucesso | Ainda sem execução neste PR |
| FR-08 | Produto com aspas no nome | JSON válido via serialização Gson | Ainda sem execução neste PR |
| FR-09 | Fujioka GET /dell | JSP recebe lista do serviço | Ainda sem execução neste PR |
| FR-10 | API Dell offline | Timeout/erro HTTP controlado | Ainda sem execução neste PR |
| FR-11 | Proteção de dados em logs | Não imprimir payload sensível | Ainda sem execução neste PR |
| FR-12 | Credenciais de .env versionadas | Remover de código/rotacionar caso necessário | Ainda sem execução neste PR |

## Camadas
Testes unitários de modelos/serviços em Java, testes REST/Servlet com mocks e integração com banco efêmero. Cada módulo Maven deve ser executado em seu diretório; a execução de um não garante o outro. Registrar SHA, runtime Java, configuração sanitizada, estado e evidência por caso. Não usar credenciais locais em CI.

## Execução FR-CRUD v1.1.0 — 12/11/2026

As pendências FR-03..FR-08 de regras DAO, cursor, resposta HTTP do consumidor e serialização foram parcialmente cobertas com **12/12 testes JUnit3 PASS** (6 SQLite in-memory, 6 HTTP loopback) em [CI #43221444058](https://github.com/DanieldeAlmeida2024/API-Fabricante-Revendedor/actions/runs/43221444058), SHA `275221ee0a95c6ad45683d75478b31f0f3b88c9c`. Ver [matriz executada](EXECUCAO_JDBC_HTTP_LOCAL.md). FR-09 (Fujioka) e testes E2E Servlet e autenticação/TLS **continuam pendentes**, não considerar verificados por JUnit do DAO.

## FR-FUJ v1.2.0 — integração do consumidor Fujioka

Casos FUJ-01..FUJ-14: seis testes JUnit3 de HTTP local e quatro verificações estáticas Python de escape HTML/JSP e higiene de `.env`, além do gate Git. O CI atual passa a executar `mvn clean verify` para **ProdutosAPI, Dell e fujioka**. Renderização Tomcat/JSP real, autenticação e XSS dinâmico continuam **pendentes**. [Plano detalhado](FUJIOKA_CONTRATOS_JSP.md).
