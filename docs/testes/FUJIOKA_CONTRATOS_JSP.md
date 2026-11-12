# Fujioka — matriz de testes HTTP, resposta controlada e segurança da JSP

**FR-FUJ-TST v1.2.0 proposta · 12/11/2026**

| Caso | Camada | Aceitação |
|---|---|---|
| FUJ-01 | JUnit + HttpServer | GET 200 contendo array e Unicode/aspas vira `ProdutoDell` correto |
| FUJ-02 | JUnit + HttpServer | GET 200 com `[]` vira lista vazia |
| FUJ-03 | JUnit + HttpServer | GET 503 não vira lista vazia/sucesso nem expõe body com dados internos |
| FUJ-04 | JUnit + HttpServer | JSON malformado ou não-array falha com `IOException` |
| FUJ-05 | JUnit + HttpServer | GET ID responde objeto, lista legada ou `null`, sem quebrar |
| FUJ-06 | JUnit + HttpServer | ID não positivo e HTTP 404 são recusados |
| FUJ-07 | Python estático | Nome/categoria/preço/ID escapados em atributos e texto `<c:out>` |
| FUJ-08 | Python estático | Dados de fornecedor não são interpolados em literais JS; DOM `dataset` e `textContent` |
| FUJ-09 | Python estático | Arquivos `.env` não existem no HEAD e `.env.example` está disponível |
| FUJ-10 | Git no CI | `git ls-files -- Dell/.env fujioka/.env` devolve vazio |
| FUJ-11 | Maven | `mvn -B -ntp -f fujioka/pom.xml clean verify` compila WAR e executa testes sem acesso a serviços reais |
| FUJ-12 | Pendente E2E | Tomcat/Servlet `/dell` com upstream 2xx/502/503, render Jasper/JSTL e escape no navegador |
| FUJ-13 | Pendente segurança | Auth, TLS, CSP e teste XSS end-to-end com entradas adversariais |
| FUJ-14 | Pendente operação | Expor configuração por variável segura em lugar de constantes Java |

### Execução reproduzível
```sh
mvn -B -ntp -f ProdutosAPI/pom.xml clean verify
mvn -B -ntp -f Dell/pom.xml clean verify
mvn -B -ntp -f fujioka/pom.xml clean verify
python3 -m unittest discover -s tests -p 'test_*.py' -v
test -z "$(git ls-files -- Dell/.env fujioka/.env)"
```

O workflow `.github/workflows/produtos-api-ci.yml` deve reportar os três WARs (6+6+6 testes Java) e os testes documentais/JSP. **Não afirmar PASS do novo HEAD antes de consultar GitHub Actions**. Os cenários FUJ-12..FUJ-14 não são cobertos por essas suites.
