# API-2 — Consumo da fila, FIPE (modelos) e persistência

A **api-2** **não expõe** a API REST do desafio para o utilizador final. O seu papel é **consumir mensagens** com dados de marca vindos da api-1, **chamar a FIPE** para obter **códigos e nomes de modelos** por marca, e **gravar** marca e veículos no **mesmo PostgreSQL** usado pela api-1.

## Relação com o desafio (`desafio.txt`)

| Item | O que a api-2 faz |
|------|-------------------|
| **3** | Consome a fila (canal `brands-in`), **uma mensagem de cada vez** (`@Blocking`), em fila dedicada ligada ao exchange fanout `vehicle-brands`. |
| **4** | Para cada mensagem, chama a FIPE: `GET /carros/marcas/{codigoMarca}/modelos`. |
| **5** | Persiste **marca** (se necessário) e **veículos** com `fipeModelCode` (código do modelo na FIPE), `modelName` e associação à marca; evita duplicar o mesmo par marca + código de modelo. |

Os itens 1, 2, 6, 7 e 8 são responsabilidade da **api-1**.

## Mensagem na fila

Payload JSON (exemplo):

```json
{ "fipeCode": "59", "name": "Toyota" }
```

- `fipeCode`: código da marca na FIPE (`Marca.codigo`)
- `name`: nome da marca (`Marca.nome`)

## Pré-requisitos

- **JDK 17+**
- **PostgreSQL** e **RabbitMQ** (`infra/docker-compose.yml`)
- **FIPE** ou **`fipe-mock`** (mesma ideia que na api-1: `FIPE_API_BASE_URL`)

## Configuração relevante

| Variável | Descrição |
|----------|-----------|
| `JDBC_URL`, `DB_USER`, `DB_PASSWORD` | Mesma base que a api-1 (schema `brand` / `vehicle`) |
| `FIPE_API_BASE_URL` | Base do REST Client (ex.: mock em `http://localhost:8090`) |
| `RABBITMQ_*` | Broker; exchange `vehicle-brands` (fanout), fila `vehicle-brands-api2` |

Porta HTTP padrão: **8081** (apenas utilitária; em dev pode usar `/q/dev-ui` do Quarkus).

## Como executar (local)

1. `cd infra && docker compose up -d`
2. Subir **fipe-mock** se a FIPE real não estiver disponível
3. Na pasta `api-2`:

```bash
./mvnw quarkus:dev
```

Confirme nos logs linhas do tipo: conexão RabbitMQ e *receiver* à fila `vehicle-brands-api2`.

## Fluxo típico de validação

1. api-2 a correr  
2. api-1: `POST /api/v1/initial-load`  
3. api-2 processa mensagens e preenche a base  
4. api-1: `GET /api/v1/brands` e `GET /api/v1/brands/{id}/vehicles` para ver o resultado

## Testes

```bash
./mvnw test
```

O perfil `%test` usa H2 em memória e canal de entrada **in-memory** (requer a dependência `smallrye-reactive-messaging-in-memory` no `pom.xml`).

## Boas práticas

Processamento assíncrono isolado, REST Client para FIPE, transações ao persistir, separação entre consumidor de mensagens (`BrandIngestConsumer`) e caso de uso (`BrandIngestProcessor`), alinhado ao enunciado (SOLID, Clean Architecture, etc.).
