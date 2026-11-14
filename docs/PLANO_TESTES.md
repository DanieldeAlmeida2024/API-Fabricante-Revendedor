# Plano de testes — ProdutosAPI e integração de módulos

Revisão de 14/11/2026.

Comando: `mvn -B -ntp -f ProdutosAPI/pom.xml verify`. O GitHub Actions executa também `mvn verify` para `Dell/` e `fujioka/` em jobs separados.

| Caso | Subcamada | Esperado |
| --- | --- | --- |
| DAO-01 | SQLite isolado | Criar, obter, atualizar e excluir registro real |
| DAO-02 | SQLite isolado | GET não encontrado retorna null |
| DAO-03 | SQLite isolado | UPDATE/DELETE ausentes retornam false |
| DAO-04 | SQL parametrizado | Nome com `'; DROP TABLE` é armazenado literalmente, sem execução |
| DAO-05 | Conexões | Duas instâncias conseguem ler/gravar mesmo arquivo sequencialmente |
| DAO-06 | Falha de I/O | Exceção explícita, sem resultado falso |
| HTTP-01 | Service | GET inexistente retorna 404 |
| HTTP-02 | Service | GET válido retorna objeto JSON |
| HTTP-03 | Service | Lista vazia retorna `[]` e 200 |
| HTTP-04 | Service | IDs inválidos retornam 400 sem consulta ao DAO |
| HTTP-05 | Service | POST válido retorna 201 e gravação é chamada |
| HTTP-06 | Service | JSON malformado / preço NaN/Infinity é rejeitado |
| HTTP-07 | Service | Content-Type incorreto retorna 415 |
| HTTP-08 | Service | Corpo acima de 65536 caracteres retorna 413 |
| HTTP-09 | Service | PUT de ID inexistente retorna 404 |
| HTTP-10 | Service | DELETE ausente 404; existente 204 sem corpo |
| HTTP-11 | Service | Falha SQLite retorna 500, não expõe detalhes internos |
| BUILD-01 | Maven | `verify` de ProdutosAPI, Dell e fujioka |

**Limites:** testes HTTP do serviço usam mocks (não sobem Tomcat); SQLite é realmente exercitado em arquivos temporários. O build dos consumidores não comprova E2E nem disponibilidade dos endpoints. Isso exige ambiente isolado com três WARs, testes HTTP e verificação de contrato antes de uso real.

## FR-MAIN 1.3.0 — contratos Dell/Fujioka sobre a main atual

| ID | Cenário | Critério de aceitação |
| --- | --- | --- |
| FR-D01 | Cliente Dell recebe HTTP 4xx/5xx | Erro propagado, sem declarar POST bem-sucedido |
| FR-D02 | Produto tem aspas ou Unicode | Serialização Gson preserva JSON válido |
| FR-F01 | Fujioka recebe catálogo inválido ou status de falha | Não interpreta falha como lista vazia |
| FR-F02 | Servlet Fujioka recebe erro da API | HTTP 502/503; não faz forward com catálogo parcial |
| FR-F03 | JSP recebe texto de catálogo não confiável | Escape nos atributos/textos e nenhuma interpolação direta em JavaScript |
| FR-E01 | `.env` de módulos é rastreado | Reprovar no CI, preservar `.env.example` |
| FR-C01 | Maven dos três módulos + documentação | CI `.github/workflows/contrato-catalogo.yml` sem dependência externa |

Separação: tests HTTP `HttpServer` e validações estáticas **não** garantem renderização real da JSP em Tomcat; E2E de autenticação, XSS dinâmico, URLs configuráveis e TLS continuam pendentes.
