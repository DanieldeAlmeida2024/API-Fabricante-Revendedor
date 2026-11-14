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
