# Fujioka — integração HTTP e saída JSP do catálogo
**FR-FUJ v1.2.0 · proposta de 12/11/2026 · sem deploy**

## Contexto real e limites
O repositório contém três WARs Maven separados. A Fujioka é uma interface JSP em `/dell`, que busca um **array JSON** pela classe `br.com.fujioka.api.service.DellAPIService` no serviço `ProdutosAPI`. Não há autenticação implementada nessa camada, e os endereços locais estão atualmente fixos em `fujioka/src/main/java/br/com/fujioka/api/DellAPI.java`. Portanto, arquivos `.env` não são carregados automaticamente, mesmo que sejam fornecidos.

## Defeitos reproduzidos por inspeção de código
O consumidor anterior executava `HttpClient.send()` e desserializava o body mesmo em HTTP 4xx/5xx; imprimia a resposta integral em `System.out`; assumia lista inclusive para `GET ?id=`. O `ProdutosDellController` relançava falhas como `RuntimeException`, sem distinguir indisponibilidade do catálogo. A JSP escrevia nomes/categorias não confiáveis diretamente nos atributos HTML e **dentro de literais de JavaScript**, que podem quebrar a página e formar XSS armazenado/refletido.

## Comportamento revisado
| Componente | Contrato |
|---|---|
| `DellAPIService.getProdutosDell()` | HTTP 2xx + array JSON obrigatório; `[]` é lista vazia válida. Status não-2xx, corpo malformado, `null` ou objeto em vez de array viram `IOException`. |
| `DellAPIService.getProdutoDell(id)` | `id>0`; aceita objeto JSON (novo contrato), lista legada ou `null`; 4xx/5xx propagados como erro, sem depender de resposta `List<ProdutoDell>`. |
| `ProdutosDellController /dell` | Só encaminha `getProdutos` ao JSP depois de obter catálogo válido. Erro upstream retorna **502**; interrupção restaura flag e retorna **503**, evitando página parcialmente populada. |
| `index.jsp` | `<c:out>` faz escape de valores de produto/atributos HTML; dados para busca são reconstruídos com `dataset` do DOM, e **não** montados por interpolação de valores de produto em `<script>`. A busca usa `textContent`. |
| Estado/configuração | `Dell/.env` e `fujioka/.env` deixam de ser rastreados no HEAD; exemplos `.env.example` e `.gitignore` raiz documentam campos neutros. Os arquivos não são carregados pelo código atual. |

## Limitações que permanecem
- `JSP` não foi compilada por Jasper/Tomcat nem testada em navegador; a proteção XSS é respaldada por inspeções estáticas, **não** testes reais de template ou segurança.
- A interface ainda tem formulário desativado/simulado e não expõe fluxo real de exclusão/edição. Não apresentar isso como funcionalidade concluída.
- O código Java fixa `localhost:8081`; falta externalização validada de URL, rede/TLS, autenticação/autorização, CSP e análise de vulnerabilidades de bibliotecas e da versão do Tomcat.
- A exclusão dos arquivos do HEAD **não remove conteúdo de commits antigos**. Nesta inspeção os `.env` continham parâmetros de host/porta/caminho, sem senhas/tokens identificados, mas qualquer segredo histórico identificado futuramente deverá ser rotacionado; reescrita de histórico exigirá avaliação própria.
- Testes HTTP de contrato usam servidor loopback, não fazem deploy nem acessam API remota. Falta E2E dos três WARs com Tomcat descartável.

[Plano e evidência](../testes/FUJIOKA_CONTRATOS_JSP.md) · [UML](../uml/04_fujioka_catalogo_seguranca.puml) · [Versão](../controle/VERSIONAMENTO.md).
