# API Fabricante ↔ Revenda — Projeto de Estudo

Um projeto de estudo para explorar modelos de implementação e padronização da comunicação entre fabricantes e revendas autorizadas.  
Neste repositório há uma implementação simples em Java usando Servlets que demonstra um contrato básico entre as partes — como exemplo prático usamos "Dell" como fabricante e "Fujioka" como revenda, mas a implementação é genérica e destinada a experimentação e aprendizado.

Aviso: este projeto é uma prova de conceito / estudo. Não é uma solução pronta para produção. O objetivo é demonstrar padrões, fluxos e pontos a considerar ao projetar integrações B2B entre fabricantes e revendas.

Sumário
- Sobre
- Recursos implementados
- Modelo de dados (contrato compartilhado)
- Endpoints e exemplos de uso
- Fluxo de requisição (como funciona)
- Estrutura do projeto
- Padrões arquiteturais observados
- Middleware e segurança (apenas POC)
- Limitações e recomendações para produção
- Como rodar localmente (rápido)
- Contribuição e contato
- Licença

Sobre
Este repositório demonstra um serviço RESTful simples (implementado com Java Servlets) que oferece operações CRUD básicas para um recurso "Produto". A intenção é servir como referência para discutir e testar estratégias de padronização entre:
- Fabricante (ex.: Dell) — publica catálogo/estoque/preço.
- Revenda autorizada (ex.: Fujioka) — consome catálogo e cria pedidos/integra com seus sistemas.
- API central / adapter — realiza a mediação e padronização dos dados.

Recursos implementados
- Recurso principal: Produto (CRUD)
  - Listar produtos (GET)
  - Consultar produto por id (GET?id=NN)
  - Criar produto (POST)
  - Atualizar produto (PUT?id=NN)
  - Excluir produto (DELETE?id=NN)

Esses serviços estão implementados nas classes:
- br.com.produtos.controller.ProdutoController — servlet que recebe requisições e aplica um middleware simples
- br.com.produtos.controller.services.ProdutoService — lógica de leitura do body, validação mínima e chamada ao DAO
- br.com.produtos.persistence.ProdutoDao — camada de persistência (DAO)
- br.com.produtos.interfaces.ProdutoInterface — contrato do modelo compartilhado

Modelo de dados (contrato compartilhado)
A interface compartilhada define o objeto Produto mínimo usado entre Dell, Fujioka e a API:

Campos principais (implícito pela interface)
- id (int)
- nomeProduto (String)
- categoria (String)
- preco (double)

Exemplo JSON de produto:
{
  "id": 1,
  "nomeProduto": "Notebook X",
  "categoria": "Computadores",
  "preco": 2999.99
}

Observação de estudo: para sistemas reais, recomendamos usar BigDecimal para valores monetários, incluir currency, sku, estoque, timestamps, e versionamento do contrato.

Endpoints e exemplos (API mapeada em "/")
Observação: o servlet está anotado com @WebServlet("/"), por isso as operações são roteadas por método HTTP ao mesmo caminho base.

- Listar todos os produtos
  GET /
  Exemplo:
  curl -X GET "http://localhost:8080/" -H "Accept: application/json"

- Consultar produto por id
  GET /?id=NN
  Exemplo:
  curl -X GET "http://localhost:8080/?id=1"

- Criar novo produto
  POST /
  Body: JSON com nomeProduto, categoria, preco
  Exemplo:
  curl -X POST "http://localhost:8080/" -H "Content-Type: application/json" -d '{"nomeProduto":"X","categoria":"Y","preco":100.0}'

- Atualizar produto existente
  PUT /?id=NN
  Body: JSON com os campos atualizados
  Exemplo:
  curl -X PUT "http://localhost:8080/?id=1" -H "Content-Type: application/json" -d '{"nomeProduto":"X2","categoria":"Y2","preco":110.0}'

- Excluir produto
  DELETE /?id=NN
  Exemplo:
  curl -X DELETE "http://localhost:8080/?id=1"

Como a aplicação funciona (fluxo)
1. Requisição HTTP chega ao servlet ProdutoController.
2. ProdutoController executa `produtoMiddleware(...)` — verificação simples de entradas suspeitas (regex).
3. Se o middleware permitir, a requisição é delegada a ProdutoService (métodos estáticos get/post/put/delete).
4. ProdutoService realiza:
   - leitura do corpo (para POST/PUT) via BufferedReader,
   - desserialização/serialização com Gson,
   - validações básicas (ex.: nome != null, categoria != null, preco > 0),
   - chamada ao ProdutoDao para acesso ao banco.
5. Resposta JSON é escrita diretamente no HttpServletResponse com status HTTP apropriado (201, 200, 400, 500...).

Estrutura de pastas (essencial)
- br/com/produtos/controller
  - ProdutoController.java (Servlet com middleware)
- br/com/produtos/controller/services
  - ProdutoService.java (lógica CRUD, usa ProdutoDao)
- br/com/produtos/interfaces
  - ProdutoInterface.java (contrato compartilhado)
- br/com/produtos/model
  - (implementação do model Produto)
- br/com/produtos/persistence
  - ProdutoDao.java (acesso ao BD)

Padrões arquiteturais observados
- Arquitetura em camadas: Controller → Service → DAO → Model.
- DAO Pattern para encapsular persistência.
- Uso de DTO/Model padronizado via interface compartilhada.
- Middleware implementado diretamente no Servlet (para estudo; em produção seria melhor como Filter).
- Serialização JSON via Gson.

Middleware e segurança (POC)
- ProdutoController possui `produtoMiddleware` que aplica um regex simples para detectar padrões comuns de injeção SQL em URL e parâmetros.
- Essa técnica é apenas demonstrativa — regex não é uma proteção suficiente. A proteção real deve incluir:
  - Prepared statements / parametrização no DAO
  - Validação estrita dos inputs
  - Autenticação/Autorização (JWT, API Keys, OAuth2)
  - TLS/HTTPS
  - Rate limiting e logs

Limitações importantes (por ser um projeto de estudo)
- Uso de double para preço (problemas de precisão) — em produção use BigDecimal.
- Métodos e DAO estáticos dificultam testes e injeção de dependências.
- Integração via query param id em vez de path params (ex.: /produtos/{id}) — menos RESTful.
- Logs via System.out e printStackTrace — troque por SLF4J / Logback.
- Ausência de autenticação/autorização.
- Middleware por regex é insuficiente como proteção contra SQL injection.
- Sem documentação OpenAPI/Swagger no projeto atual.
- Respostas de erro não padronizadas; recomenda-se um modelo ErrorResponse.

Recomendações rápidas para evolução (se seguir para protótipo avançado)
- Converter preço para BigDecimal e adicionar currency.
- Separar ProdutoController / ProductService / ProductRepository com DI (ex.: usar Spring Boot).
- Implementar Servlet Filter para validações transversais e um ExceptionHandler global.
- Produzir OpenAPI/Swagger e testes de contrato (Pact ou Spring Cloud Contract) entre Dell e Fujioka.
- Publicar a interface/contrato como artefato compartilhado (Maven/Gradle) para evitar divergências.
- Garantir prepared statements no DAO e connection pooling (HikariCP).
- Adicionar autenticação (API Key para POC, JWT/OAuth2 para realidade).

Como rodar (orientação rápida — ambiente Java/Servlet)
Pré-requisitos:
- JDK 11+ (ou versão compatível)
- Maven ou Gradle (depende do build do projeto)
- Servlet container (Tomcat) ou use um plugin embarcado de execução pelo build

Passos gerais:
1. Build
   - Com Maven: mvn clean package
2. Deploy
   - Copie o WAR gerado para a pasta webapps do Tomcat, ou execute via plugin do Maven/Gradle.
3. Acesse
   - A aplicação ficará disponível em http://localhost:8080/ (ou contexto configurado).

(Se preferir, posso gerar um exemplo de pom.xml e instruções de deploy no Tomcat.)

Contribuição
Este é um projeto de estudo — contribuições são bem-vindas para:
- refatorações para maior testabilidade,
- melhorias na modelagem do contrato (BigDecimal, currency, sku),
- implementação de autenticação e documentação OpenAPI,
- testes automatizados (unit + integration + contract).

Para contribuir:
- Fork → branch feature/x → PR com descrição das mudanças.
- Mantenha o foco em educação: escreva motivos e trade-offs nas descrições dos PRs.

Licença
MIT — projeto de estudo. Veja o arquivo LICENSE para detalhes.

Contato / Maintainer
- Daniel de Almeida — GitHub: @DanieldeAlmeida2024

Notas finais (contexto educacional)
Este repositório é intencionalmente simples para permitir experimentação e discussão sobre a padronização da comunicação entre fabricantes e revendas. Ele serve como ponto de partida para avaliar pontos críticos (contratos, validação, segurança, versionamento e teste de contrato) antes de uma implementação de produção.
