# CAT-GETID-01 — Identidade do produto recebido de fornecedor

14/11/2026 · Módulos: Dell (cliente) e Fujioka (revenda).

## Incidente potencial
GET por ID aceitava o primeiro elemento de um array legado sem conferir o ID retornado. Um upstream desatualizado, mal filtrado ou incompatível poderia exibir o produto errado (inclusive preço incorreto) ao usuário da revenda.

## Contrato
- Resposta HTTP 2xx não comprova identidade do item: se o objeto retornado possuir ID diferente do pedido, lançar IOException explícita.
- Array legado vazio continua significando não encontrado; array de exatamente um elemento é tolerado se o ID coincidir; array com mais de um registro é ambíguo e falha.
- Testes com HttpServer em loopback reproduzem respostas 200 com IDs trocados e arrays múltiplos, em ambos os módulos.

## Exclusões
Sem integração a serviços Dell reais, banco de produção, deploy em servidor de aplicação ou mudança no contrato POST/PUT/DELETE.
