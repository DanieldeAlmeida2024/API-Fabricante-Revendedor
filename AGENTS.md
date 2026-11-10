# Contexto para agentes — API-Fabricante-Revendedor

Prova de conceito Java de comunicação entre fabricante, revenda e catálogo de produtos.

- Ler README e Dell/, fujioka/, ProdutosAPI/ antes de alterações.
- Preservar stack (Java Servlets, Maven, JSP e persistência relacional) e direitos/autoria de projetos didáticos.
- Usar português em documentação e novos identificadores internos, sem modificar interfaces públicas sem migração.
- Separar apresentação, aplicação, domínio e infraestrutura conforme necessidade real.
- Não inventar integrações nem alegar builds bem-sucedidos sem logs.
- Não versionar segredos, variáveis reais, dados pessoais ou artefatos de compilação.
- Validar com `cd ProdutosAPI && mvn test` e testes completos da stack.

**Risco:** Existem arquivos .env rastreados em subprojetos; verificar imediatamente se contêm credenciais e rotacionar segredos se necessário. Projeto não é produção.
