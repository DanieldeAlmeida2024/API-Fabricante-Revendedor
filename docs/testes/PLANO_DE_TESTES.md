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
