# Requisitos — API de produtos e revendedores

Revisão de 14/11/2026.

- **RF-01:** inserir produtos com `produto`, `categoria` e preço positivo e finito.
- **RF-02:** listar catálogo em JSON e consultar por ID numérico positivo.
- **RF-03:** editar e excluir somente registros existentes, com diferenciação HTTP 404.
- **RF-04:** rejeitar corpo vazio/malformado, categoria/nome em branco, preço inválido e ID inválido.
- **RF-05:** expor 201 em criação, 204 em exclusão, 400 em entrada inválida, 413 corpo acima do limite, 415 formato incorreto e 500 em falha de banco.
- **RF-06:** manter consumidores Dell e Fujioka no mesmo contrato de catálogo.

- **RNF-01:** usar JDBC parametrizado, sem confiar em regex como proteção contra SQL Injection.
- **RNF-02:** não compartilhar conexões SQLite entre threads; fechar recursos ao final das operações.
- **RNF-03:** configurar arquivo de banco por ambiente (`PRODUTOS_DB_PATH`) e não misturar dados de teste.
- **RNF-04:** validar o WAR de `ProdutosAPI`, `Dell` e `fujioka` via Maven em GitHub Actions.
- **RNF-05:** testes unitários HTTP (Mockito) e testes reais de persistência SQLite.
- **RNF-06:** distinguir claramente limitações didáticas, inclusive ausência de autenticação, logs estruturados, API pública e E2E.

**Fora do escopo desta revisão:** redesenho monetário com `BigDecimal`, migrações SQL, integração HTTP de três aplicações simultâneas e implantação.
