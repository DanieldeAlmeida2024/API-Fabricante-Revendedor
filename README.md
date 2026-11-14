# API Fabricante ↔ Revendedor — integração de catálogo em Java

Laboratório de comunicação entre fabricante, revenda e catálogo central de produtos utilizando Java, Maven, Servlets/JSP e persistência SQLite. Os módulos `Dell/`, `fujioka/` e `ProdutosAPI/` representam responsabilidades distintas em um cenário didático B2B.

## Componentes e fluxo

- **ProdutosAPI:** serviço central de cadastro, consulta, alteração e exclusão de produtos.
- **Dell:** aplicação representando o fabricante no estudo de integração.
- **Fujioka:** aplicação representando a revenda e o consumo do catálogo.
- **Contrato HTTP/JSON:** formato compartilhado e comportamento de erros para interoperabilidade entre os módulos.

O fluxo de persistência da API utiliza `ProdutoController` → `ProdutoService` → `ProdutoDao`, com operações parametrizadas no SQLite.

## Contrato HTTP do catálogo

| Método | Rota | Resposta |
| --- | --- | --- |
| GET | `/` | 200, lista JSON |
| GET | `/?id=1` | 200, produto; 404 se inexistente |
| POST | `/` | 201, produto criado |
| PUT | `/?id=1` | 200, produto atualizado; 404 se ausente |
| DELETE | `/?id=1` | 204, sem corpo; 404 se ausente |

Exemplo de JSON recebido pelo módulo `ProdutosAPI`:

```json
{"produto":"Notebook X","categoria":"Computadores","preco":2999.99}
```

O campo de nome do produto no contrato HTTP é **`produto`**.

## Compilar e testar

Requer JDK 17 e Maven:

```bash
mvn -B -ntp -f ProdutosAPI/pom.xml verify
mvn -B -ntp -f Dell/pom.xml verify
mvn -B -ntp -f fujioka/pom.xml verify
```

O workflow `.github/workflows/contrato-catalogo.yml` executa a validação dos módulos. Os testes da `ProdutosAPI` usam SQLite temporário e respostas Servlet simuladas; os outros módulos passam por compilação e empacotamento WAR. O pipeline não constitui teste HTTP end-to-end dos três serviços com Tomcat.

## Persistência e validação

- Por padrão, o SQLite utiliza `produtos.db`; configure `PRODUTOS_DB_PATH` ou `-Dprodutos.db.path` para isolar os testes.
- Os métodos da API validam identificadores, preço numérico positivo e finito, limites de entrada e tipo de conteúdo.
- O DAO utiliza `PreparedStatement` para comandos SQL parametrizados.

## Limites do laboratório

Os nomes das organizações são exemplos didáticos, não representam parceria ou integração oficial. A aplicação não possui autenticação, autorização multiusuário, exigência de TLS, migrações versionadas ou testes completos com container Servlet. Não é um serviço pronto para exposição pública.

## Documentação

[Requisitos](docs/REQUISITOS.md) · [Arquitetura](docs/ARQUITETURA.md) · [UML](docs/UML.puml) · [Plano de testes](docs/PLANO_TESTES.md).

## Contratos HTTP do fabricante e escape do catálogo da revenda

Os clientes Java **Dell** e **Fujioka** passaram a validar resposta HTTP antes de usar o corpo JSON. O Dell serializa produtos via Gson (incluindo aspas e Unicode) e não confirma operações POST/PUT/DELETE após um 4xx/5xx. Na Fujioka, falha da dependência vira HTTP **502**, interrupção vira **503**, e a JSP escapa atributos e texto do catálogo em vez de inserir nomes de produtos diretamente num literal JavaScript.

Os três módulos têm builds independentes em Maven. Há testes de cliente HTTP com `HttpServer` local e testes estáticos para JSP; ainda **não existe evidência de uma execução E2E com Tomcat/Jasper ou de autenticação e TLS**. Os arquivos `.env.example` são modelos que o código Java não carrega automaticamente.

[Contrato do consumidor Fujioka](docs/projeto/FUJIOKA_INTEGRACAO_E_JSP.md) · [Plano de testes Fujioka](docs/testes/FUJIOKA_CONTRATOS_JSP.md) · [UML de integração](docs/uml/04_fujioka_catalogo_seguranca.puml) · [Histórico](docs/controle/VERSIONAMENTO.md).
