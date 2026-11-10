# Plano de testes — API-Fabricante-Revendedor

1. CT-01 Verificar documentação e sintaxe delimitada do UML.
2. CT-02 Executar `cd ProdutosAPI && mvn test` em ambiente apropriado.
3. CT-03 Executar os testes funcionais do(s) projeto(s) com dependências.
4. CT-04 Simular entrada inválida e indisponibilidade de serviço.
5. CT-05 Revisar credenciais, transporte e dados versionados.

Teste documental (apenas CT-01): `python -m unittest discover -s tests -p test_documentacao.py -v`.
