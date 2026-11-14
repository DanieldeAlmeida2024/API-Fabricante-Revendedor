# Arquitetura — API Fabricante ↔ Revendedor

Revisão de 14/11/2026 — catálogo acadêmico em Java.

## Componentes existentes

| Módulo | Tecnologias | Responsabilidade |
| --- | --- | --- |
| `ProdutosAPI/` | Java Servlet 4, Gson, SQLite JDBC | Fonte do catálogo: CRUD HTTP e persistência |
| `Dell/` | Java Servlet/JSP, HttpClient | Interface do fabricante, consumidora do catálogo |
| `fujioka/` | Java Servlet/JSP, HttpClient | Interface de revenda, consumidora do catálogo |

No `ProdutosAPI/`, o fluxo é `ProdutoController` → `ProdutoService` → `ProdutoDao` → `Connect` → SQLite. O servlet encaminha requisições e o serviço valida IDs, corpo JSON, tamanho e status HTTP.

## Persistência e isolamento

O `ProdutoDao` **não** compartilha mais um `java.sql.Connection` global entre requisições. Cada operação abre e fecha uma conexão própria com `try-with-resources`. As consultas e atualizações usam `PreparedStatement` e os métodos de UPDATE/DELETE retornam `true` apenas quando uma linha foi afetada. Uma falha SQL gera exceção — não pode ser confundida com sucesso.

A URL padrão usa o arquivo `produtos.db` relativo ao diretório de execução, e pode ser configurada por `PRODUTOS_DB_PATH` ou `-Dprodutos.db.path=/caminho/arquivo.db`. Não misture o banco de teste com produção. O ambiente CI usa diretórios temporários, sem serviço externo.

## Contrato

O JSON do modelo atual possui o campo **`produto`** (nome interno do atributo), `categoria` e `preco`. O texto histórico anterior usava `nomeProduto` em alguns exemplos, o que não corresponde ao Gson atual. Evite divergência entre consumidor e fabricante sem migração versionada.

- `GET /`: lista JSON, mesmo vazia.
- `GET /?id=N`: 200 objeto, 400 ID inválido, 404 inexistente.
- `POST /`: 201, 400 JSON/campos inválidos, 415 tipo de mídia incorreto, 413 corpo excessivo.
- `PUT /?id=N`: 200 somente se encontrou linha; 404 quando ausente.
- `DELETE /?id=N`: 204 somente se excluiu; 404 quando ausente.
- Falhas de SQLite: 500 genérico ao cliente; detalhes técnicos apenas no log do servidor.

## Segurança, limites e trade-offs

A antiga regex de bloqueio SQL foi removida: não protegia JSON e rejeitava texto legítimo. A mitigação real aqui é SQL parametrizado e validação de dados. Não há autenticação/autorização, rate limiting, TLS obrigatório, controle de concorrência de escrita, pool de conexões nem versionamento de banco; **não implantar na internet como API de fabricante**. Os clientes Dell/Fujioka continuam dependentes de URLs e Tomcat locais e precisam de testes de contrato HTTP ponta a ponta.
