# Infraestrutura local (Docker Compose)

Esta pasta reúne o que você precisa para executar **PostgreSQL** e **RabbitMQ** no ambiente de desenvolvimento, alinhado às `application.properties` das apps **api-1** e **api-2**.

> **Atenção:** os usuários e senhas abaixo são **apenas para teste local**. Não use esses valores em produção.

## Arquivos

| Arquivo / pasta | Função |
|-----------------|--------|
| **`docker-compose.yml`** | Apenas **Postgres** e **RabbitMQ** para desenvolvimento manual (subir as apps na IDE ou no terminal). |
| **`sql/001_initial_schema.sql`** | Script SQL inicial: cria as tabelas `brand` e `vehicle` (chaves, índices, unicidade de `fipe_code` por marca e do par `brand_id` + `fipe_model_code` por veículo). |

A **stack completa** (Postgres + Rabbit + mock + api-1 + api-2) está no **`docker-compose.yml` da raiz do repositório** — veja o [`README.md`](../README.md) na raiz.

### Como o SQL é aplicado

A pasta `sql/` é montada somente leitura em `/docker-entrypoint-initdb.d` no container do Postgres. O Postgres executa os arquivos **`.sql`** dessa pasta **em ordem lexicográfica** na **primeira criação** do volume de dados.

- No `docker-compose.yml` simples o volume chama-se `postgres_data`; na stack completa chama-se `postgres_stack_data`. Se o volume **já existir** com dados, esses scripts **não rodam de novo**.
- Para reaplicar do zero: `docker compose down -v` (remove os volumes) e suba de novo, **ou** execute manualmente:  
  `psql -h localhost -U vehicle -d vehicle -f sql/001_initial_schema.sql` (será solicitada a senha indicada abaixo).

## Usuários e credenciais de teste

### PostgreSQL (`postgres`)

| Campo | Valor |
|--------|--------|
| **Usuário** | `vehicle` |
| **Senha** | `vehicle` |
| **Banco de dados** | `vehicle` |
| **Porta (host)** | `5432` |

**JDBC** (exemplo, como nas apps):

`jdbc:postgresql://localhost:5432/vehicle`

As aplicações Quarkus usam por padrão `DB_USER` / `DB_PASSWORD` / `JDBC_URL`; se você não definir nada, os valores coincidem com estes.

### RabbitMQ (`rabbitmq`)

| Campo | Valor |
|--------|--------|
| **Usuário AMQP** | `vehicle` |
| **Senha AMQP** | `vehicle` |
| **Porta AMQP** | `5672` |
| **Interface de gerenciamento (HTTP)** | http://localhost:15672 |
| **Acesso ao painel** | usuário `vehicle`, senha `vehicle` |

O usuário `vehicle` é o usuário **padrão** do broker (`RABBITMQ_DEFAULT_USER` / `RABBITMQ_DEFAULT_PASS`), com permissões de administrador na instância local.

## Subir e parar

Na pasta `infra`:

```bash
docker compose up -d
docker compose ps
docker compose down        # mantém volumes (dados preservados)
docker compose down -v     # remove volumes (apaga dados do Postgres e o estado do RabbitMQ)
```

## Stack completa (um único `docker-compose` na raiz)

Para subir **Postgres**, **Rabbit**, **fipe-mock**, **api-1** e **api-2** com um comando, use a raiz do repositório:

```bash
cd ..
docker compose up -d --build
```

Ou execute **`up-stack.ps1`** / **`up-stack.sh`** nesta pasta — eles mudam para a raiz e rodam o mesmo comando.

Instruções completas, diagrama de arquitetura e tabela de portas: [`README.md`](../README.md).

**Importante:** não rode ao mesmo tempo este `infra/docker-compose.yml` e o compose da raiz (mesmas portas).

## Integração com o restante do projeto

1. Se usar só o **`docker-compose.yml` desta pasta**, suba manualmente **fipe-mock**, **api-2** e **api-1** (recomendado: mock → APIs).  
2. Mais detalhes: `../api-1/README.md`, `../api-2/README.md`, `../fipe-mock/README.md`.
