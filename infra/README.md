# Infraestrutura local (Docker Compose)

Esta pasta concentra o que precisas para correr **PostgreSQL** e **RabbitMQ** no ambiente de desenvolvimento, alinhado com as `application.properties` das apps **api-1** e **api-2**.

> **Atenção:** utilizadores e palavras-passe abaixo são **só para teste local**. Não uses estes valores em produção.

## Ficheiros

| Ficheiro / pasta | Função |
|------------------|--------|
| **`docker-compose.yml`** | Define dois serviços: Postgres 16 e RabbitMQ 3.13 (imagem *management*, com consola web), portas expostas, volumes persistentes e *healthchecks*. |
| **`sql/001_initial_schema.sql`** | Script SQL inicial: cria as tabelas `brand` e `vehicle` (chaves, índices, unicidade de `fipe_code` por marca e de par `brand_id` + `fipe_model_code` por veículo). |

### Como o SQL é aplicado

A pasta `sql/` está montada em somente leitura em `/docker-entrypoint-initdb.d` no contentor Postgres. O Postgres executa os ficheiros **`.sql`** dessa pasta **por ordem lexicográfica** na **primeira criação** do volume de dados.

- Se o volume `postgres_data` **já existir** com dados, estes scripts **não voltam a correr**.
- Para reaplicar do zero: `docker compose down -v` (apaga volumes) e volta a subir, **ou** executa manualmente:  
  `psql -h localhost -U vehicle -d vehicle -f sql/001_initial_schema.sql` (pede a password definida abaixo).

## Utilizadores e credenciais de teste

### PostgreSQL (`postgres`)

| Campo | Valor |
|--------|--------|
| **Utilizador** | `vehicle` |
| **Palavra-passe** | `vehicle` |
| **Base de dados** | `vehicle` |
| **Porta (host)** | `5432` |

**JDBC** (exemplo, como nas apps):

`jdbc:postgresql://localhost:5432/vehicle`

As aplicações Quarkus usam por defeito `DB_USER` / `DB_PASSWORD` / `JDBC_URL`; se não definires nada, coincidem com estes valores.

### RabbitMQ (`rabbitmq`)

| Campo | Valor |
|--------|--------|
| **Utilizador AMQP** | `vehicle` |
| **Palavra-passe AMQP** | `vehicle` |
| **Porta AMQP** | `5672` |
| **Consola de gestão HTTP** | http://localhost:15672 |
| **Login da consola** | utilizador `vehicle`, palavra-passe `vehicle` |

O utilizador `vehicle` é o utilizador **predefinido** do broker (`RABBITMQ_DEFAULT_USER` / `RABBITMQ_DEFAULT_PASS`), com permissões de administrador na instância local.

## Subir e parar

Na pasta `infra`:

```bash
docker compose up -d
docker compose ps
docker compose down        # mantém volumes (dados preservados)
docker compose down -v     # remove volumes (apaga dados Postgres e estado RabbitMQ)
```

## Ligação ao resto do projeto

1. Com esta infra a correr, arranca **fipe-mock** (se precisares da FIPE simulada), depois **api-2** e **api-1**.  
2. Detalhes das APIs: `../api-1/README.md`, `../api-2/README.md`, `../fipe-mock/README.md`.
