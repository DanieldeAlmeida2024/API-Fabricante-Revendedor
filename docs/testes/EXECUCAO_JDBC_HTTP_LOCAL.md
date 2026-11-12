# API Fabricante–Revendedor — matriz e execução CRUD SQLite / consumidor Dell

**FR-CRUD-TST v1.1.0 proposta** · sem APIs externas, banco real ou VM.

| Caso | Teste executado | Oráculo |
|---|---|---|
| FR-03 | `ProdutoDaoTest.testCreateAndReadByIdAdvancesCursor` | Objeto recuperado por ID com campos corretos, após `ResultSet.next()` |
| FR-04 | `testMissingIdReturnsNullAndNoPhantomProduct` | Inexistente devolve `null` |
| FR-05 | `testUpdateAndDeleteReturnAffectedRows` | 1 linha modificada vs. 0 para ausente; GET posterior consistente |
| FR-06 | `testSqlParameterBindingPreservesQuotesAndDoesNotAlterSchema` | Texto com aspas/SQL permanece dado; tabela preservada |
| FR-07a | `testSqlFailureCannotMasqueradeAsEmptyListOrSuccess` | Banco fechado => erro propagado |
| FR-07b | `DellAPIServiceTest.testPost500DoesNotReturnFalseSuccess` | Servidor HTTP 500 => IOException, sem sucesso falso |
| FR-08 | `testPostUsesGsonAndRespects201` | JSON serialize correto com aspas/Unicode, status 201 aceito |
| FR-GET | `testGetSingleSupportsObjectResponse` / `testGetAllSupportsListResponse` | GET objeto e list compatíveis |
| FR-HTTP | `testGet404AndMutationsRejectUpstreamError` | Erros 4xx não viram sucesso no cliente |
| FR-INVALID | `testDecodeSingleSupportsArrayNullAndInvalidType` | Tipo inesperado recusado, array/null tratados |
| FR-DAO-SETUP | `testConstructorRejectsUnavailableConnection` | Falha de conexão não vira DAO funcional |
| FR-PENDING-01 | Tomcat Servlet 400/404/500 reais | **Pendente** |
| FR-PENDING-02 | Integração Fujioka JSP + rede externa | **Pendente** |
| FR-PENDING-03 | Segurança, TLS, auth e rollback de banco | **Pendente** |

### Comandos
```sh
mvn -B -ntp -f ProdutosAPI/pom.xml clean verify
mvn -B -ntp -f Dell/pom.xml clean verify
python3 -m unittest discover -s tests -p 'test_*.py' -v
```

[CI #43221444058](https://github.com/DanieldeAlmeida2024/API-Fabricante-Revendedor/actions/runs/43221444058) **PASS** em `275221ee0a95c6ad45683d75478b31f0f3b88c9c`, com `6 + 6` testes JUnit3 e dois builds Maven WAR. Novo commit de documentação exigirá rechecagem do SHA final. Nenhum teste foi executado em ambiente de produção.
