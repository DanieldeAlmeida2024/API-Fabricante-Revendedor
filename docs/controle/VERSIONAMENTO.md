# Versionamento — API Fabricante ↔ Revendedor

## FR-MAIN 1.3.0 · 14/11/2026 — Dell/Fujioka sobre main atual

- Integra as correções de clientes HTTP propostas nos PRs antigos #3/#4 **apenas** nos módulos Dell e Fujioka.
- Mantém sem regressão o catálogo ProdutosAPI da `main`, incluindo DTO/Servlet atuais, conexões SQLite por operação e testes JUnit5.
- Dell: JSON via Gson; HTTP não-2xx não pode gerar retorno de sucesso.
- Fujioka: valida status e formato JSON; falhas retornam 502/503; dados do catálogo são escapados em JSP antes de chegar ao DOM e ao JavaScript.
- CI: matriz Maven dos três WARs, regressões de catálogo, testes JUnit HTTP local e validação estática da JSP e de `.env` rastreado.
- Documentação independente para o fluxo Fujioka, cenários de teste e UML.

**Pendente:** Tomcat/Jasper em execução, integração HTTP entre os três serviços, autenticação por usuário e revendedor, URL dinâmica de serviço, TLS e testes dinâmicos de segurança. Não representa produto pronto para produção.

## Proveniência

Os PRs antigos são encadeados e não podem ser mesclados diretamente sem conflitos e risco de sobrescrever código mais novo. Este PR reaplica **somente** os arquivos específicos ainda faltantes no HEAD. Nenhuma reescrita de histórico ou migração de banco é feita.
