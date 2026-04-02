# API-1 — Orquestração, REST e publicação na fila

A **api-1** é o serviço HTTP principal do desafio: expõe os endpoints REST definidos em contrato OpenAPI, consulta a **API FIPE** (ou um mock), publica **uma mensagem por marca** num exchange RabbitMQ **fanout** e lê/atualiza dados já persistidos no PostgreSQL.

## Relação com o desafio (`desafio.txt`)

| Item | O que a api-1 faz |
|------|-------------------|
| **1** | `POST /api/v1/initial-load` — aciona a carga inicial (marcas → fila). |
| **2** | Busca marcas na FIPE via REST Client (`GET /carros/marcas`), mapeando para o domínio e publicando na fila. |
| **3** | Publica cada marca no canal `brands-out` (exchange `vehicle-brands`, fanout). |
| **6** | `GET /api/v1/brands` — lista marcas no banco. |
| **7** | `GET /api/v1/brands/{brandId}/vehicles` — códigos (`fipeModelCode`), modelos (`modelName`) e observações (`notes`) por marca. |
| **8** | `PATCH /api/v1/vehicles/{id}` — atualiza `modelName` e/ou `notes` **na mesma linha** (identificador sempre `id`; o código FIPE do modelo é `fipeModelCode`, não substitui `id` na URL). |

A persistência dos **modelos por marca** (itens 4–5) é feita pela **api-2**, de forma assíncrona após o enfileiramento.

## Contrato e documentação interativa

- Especificação: `src/main/resources/META-INF/openapi.yaml`
- Em **dev**, com a app rodando: [Swagger UI](http://localhost:8080/q/swagger-ui) e OpenAPI em `/q/openapi`

## Pré-requisitos

- **JDK 17+**
- **PostgreSQL** e **RabbitMQ** (recomendado: `infra/docker-compose.yml` na raiz do repositório)
- **API FIPE** acessível **ou** o projeto **`fipe-mock`** (quando a FIPE oficial estiver indisponível — ver README do `fipe-mock`)

## Configuração relevante

Variáveis úteis (todas opcionais; existem defaults em `application.properties`):

| Variável | Descrição |
|----------|-----------|
| `JDBC_URL` | JDBC PostgreSQL (ex.: `jdbc:postgresql://localhost:5432/vehicle`) |
| `DB_USER` / `DB_PASSWORD` | Credenciais do banco |
| `FIPE_API_BASE_URL` | URL base do cliente FIPE (ex.: `http://localhost:8090` para o mock; produção típica: `https://parallelum.com.br/fipe/api/v1`) |
| `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USER` / `RABBITMQ_USERNAME`, `RABBITMQ_PASS` / `RABBITMQ_PASSWORD` | Broker AMQP |

Porta HTTP padrão: **8080**.

## Como executar (local)

1. Subir infra: na raiz do repositório, `cd infra && docker compose up -d`
2. (Se usar mock FIPE) Subir o **`fipe-mock`** e definir `FIPE_API_BASE_URL` como a URL do mock (ver README do `fipe-mock`)
3. Subir a **api-2** antes ou em paralelo com a api-1 se quiser que as mensagens sejam consumidas logo
4. Na pasta `api-1`:

```bash
./mvnw quarkus:dev
```

Windows (PowerShell):

```powershell
.\mvnw.cmd quarkus:dev
```

## Comportamento da carga inicial

- Resposta **`202 Accepted`** com corpo `brandsEnqueued`: a parte síncrona (FIPE + enfileiramento) terminou.
- **Não** garante que os veículos já estejam no banco — isso depende da api-2. Consulte `GET /api/v1/brands` e os veículos por marca quando o processamento assíncrono tiver avançado.

## Testes

```bash
./mvnw test
```

O perfil `%test` usa H2 em memória e conector de mensagens in-memory; o cliente FIPE em testes de integração pode ser substituído por WireMock (ver `src/test/java/.../support/FipeWireMockResource.java`).

## Boas práticas (alinhamento ao enunciado do desafio)

REST, contrato **OpenAPI-first** (pacote `contract`), camadas de aplicação/infraestrutura, portas para FIPE e fila, testes automatizados — conforme referido no `desafio.txt`.
