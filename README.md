# Baozi Store – API REST

Trabalho da disciplina **Desenvolvimento Web Back-End**.
API REST de uma pequena loja de pão chinês (baozi) para controlar **clientes**, **produtos** e **pedidos**.

**Tecnologias:** Java 21 · Spring Boot 3.5 · Spring Data JPA · H2 (padrão) ou MySQL · Postman

## Estrutura (MVC)

```
src/main/java/com/baozistore
├── BaoziStoreApplication.java
├── model/        Cliente, Produto, Pedido (entidades JPA)
├── repository/   ClienteRepository, ProdutoRepository, PedidoRepository
├── controller/   ClienteController, ProdutoController, PedidoController, ErroHandler
└── dto/          PedidoRequest (JSON de entrada do pedido)
```

## Como rodar

Requer Java 21. O Maven não precisa estar instalado (o projeto usa o Maven Wrapper).

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`. Por padrão usa **H2 em arquivo** (`./data/`), então os dados continuam salvos entre execuções.
Console do banco: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:file:./data/baozistore`, usuário `sa`, sem senha).

### Usando MySQL

Com um MySQL rodando em `localhost:3306` (o banco `baozistore` é criado automaticamente):

```bash
DB_USER=root DB_PASSWORD=suasenha ./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

## Entidades

| Entidade | Campos |
|---|---|
| **Cliente** | `id` (Long), `nome` (String), `clienteDesde` (LocalDate) |
| **Produto** | `id` (Long), `nome` (String), `preco` (BigDecimal), `estoque` (Boolean) |
| **Pedido** | `id` (Long), `cliente` (→ `cliente_id`), `produto` (→ `produto_id`), `quantidade` (Integer) |

Relacionamentos: um Cliente tem vários Pedidos; um Produto aparece em vários Pedidos (N:1 nos dois casos).

## Endpoints

| Método | Rota | Descrição | Resposta |
|---|---|---|---|
| POST | `/clientes` | Cadastra cliente | 201 |
| GET | `/clientes` | Lista clientes | 200 |
| GET | `/clientes/{id}` | Consulta cliente | 200 / 404 |
| PUT | `/clientes/{id}` | Atualiza cliente | 200 / 404 |
| DELETE | `/clientes/{id}` | Apaga cliente | 204 / 404 / 409 |
| POST | `/produtos` | Cadastra produto | 201 |
| GET | `/produtos` | Lista produtos | 200 |
| GET | `/produtos/{id}` | Consulta produto | 200 / 404 |
| PUT | `/produtos/{id}` | Atualiza produto | 200 / 404 |
| DELETE | `/produtos/{id}` | Apaga produto | 204 / 404 / 409 |
| POST | `/pedidos` | Registra pedido | 201 / 400 |
| GET | `/pedidos` | Lista pedidos | 200 |
| GET | `/pedidos/{id}` | Consulta pedido | 200 / 404 |
| PUT | `/pedidos/{id}` | Atualiza pedido | 200 / 400 / 404 |
| DELETE | `/pedidos/{id}` | Apaga pedido | 204 / 404 |

409 = o cliente/produto possui pedidos e não pode ser apagado.

### Exemplos de JSON

```json
// POST /clientes
{ "nome": "Luciane123456", "clienteDesde": "2026-10-12" }

// POST /produtos
{ "nome": "Baozi de Porco", "preco": 8.50, "estoque": true }

// POST /pedidos
{ "clienteId": 1, "produtoId": 1, "quantidade": 6 }
```

Resposta de `GET /pedidos/1`:

```json
{
  "id": 1,
  "cliente": { "id": 1, "nome": "Luciane123456", "clienteDesde": "2026-10-12" },
  "produto": { "id": 1, "nome": "Baozi de Porco", "preco": 8.50, "estoque": true },
  "quantidade": 6
}
```

## Postman

Importe `postman/BaoziStore.postman_collection.json`. Na aba **Variables** da coleção, troque `nomeCliente` pelo seu Nome+RU.
Os POSTs salvam automaticamente o `id` criado nas variáveis `clienteId`, `produtoId` e `pedidoId`.

## Diagrama de caso de uso

`docs/caso-de-uso.png` (fonte PlantUML em `docs/caso-de-uso.puml`).

## Testes

```bash
./mvnw test
```
