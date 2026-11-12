# UML — Três aplicações Java: ProdutosAPI, fabricante Dell e revendedor Fujioka

**Versão 1.0.0** (11/11/2026), código `1cb5d55aa6aae36d4a27709c5573c5450c17f9a4`. Auditoria estática dos módulos; testes de integração não executados nesta revisão.

- [Arquitetura e entidades](01_componentes.puml)
- [Sequência representativa](02_sequencia.puml)

Os módulos e interfaces seguem o código existente; diagramas são fontes editáveis PlantUML.

- [Sequência CRUD SQLite e integração Dell/HTTP com falha propagada](03_fluxo_crud_dell_sqlite.puml) — FR-CRUD v1.1.0 proposta.

- [Fujioka: consulta upstream, erros 502/503 e escape de catálogo no JSP](04_fujioka_catalogo_seguranca.puml) — FR-FUJ v1.2.0 proposta, sem teste Tomcat.
