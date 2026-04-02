# fipe-mock — substituto local da API FIPE HTTP

O serviço oficial da FIPE (`https://parallelum.com.br/fipe/api/v1`, referência também em `fipeapi.json` na raiz do repositório) **pode ficar indisponível**. Este módulo sobe um **WireMock** mínimo que implementa **apenas os endpoints** que as aplicações **api-1** e **api-2** utilizam, com **formato alinhado** ao OpenAPI descrito em `fipeapi.json` (schemas `Marca`, `Modelo`, `ModelosResponse`).

**Não** substitui a FIPE para consulta de anos ou valores de veículo — só o necessário para o pipeline do desafio.

## Endpoints simulados

| Método | Caminho | Resposta |
|--------|---------|----------|
| `GET` | `/carros/marcas` | Array de `{ "codigo": string, "nome": string }` |
| `GET` | `/carros/marcas/{codigoMarca}/modelos` | `{ "modelos": [ { "codigo": number, "nome": string }, ... ], "anos": [] }` |

Os corpos vêm dos ficheiros em `src/main/resources/fipe-contract/` (`marcas.json`, `modelos-response.json`). Podes editá-los para cenários locais.

## Pré-requisitos

- **JDK 17+**
- Maven (ou usar o wrapper se existir no projeto; caso contrário `mvn` global)

## Como executar

Na pasta `fipe-mock`:

```bash
mvn -q exec:java
```

Windows:

```powershell
mvn -q exec:java
```

Porta padrão: **8090**. Para outra porta:

```bash
export FIPE_MOCK_PORT=8099   # Linux/macOS
set FIPE_MOCK_PORT=8099      # Windows CMD
```

## Ligar api-1 e api-2 ao mock

Definir a **mesma** URL base em ambas (sem path `/carros/...` — isso vem do cliente):

```text
FIPE_API_BASE_URL=http://localhost:8090
```

Ou em `application.properties` / variáveis de ambiente do IDE antes de `quarkus:dev`.

Ordem recomendada ao desenvolver: **fipe-mock** → **infra** (Postgres + Rabbit) → **api-2** → **api-1**.

## Verificação rápida

- Navegador ou curl: `http://localhost:8090/carros/marcas`  
- `http://localhost:8090/carros/marcas/59/modelos`

## Empacotar e correr só com `java` (opcional)

```bash
mvn -q package
cd target && java -jar fipe-mock-1.0.0-SNAPSHOT.jar
```

O `MANIFEST.MF` referencia `lib/*` **relativamente à pasta `target/`** (onde o `maven-dependency-plugin` copia as dependências).

## Nota sobre o desafio

O enunciado (`desafio.txt`) exige integração com a FIPE; em avaliação ou produção usa-se a URL real quando está estável. O **fipe-mock** existe para **desenvolvimento e demonstração** quando o serviço externo falha, mantendo o contrato HTTP compatível com `fipeapi.json` para as operações usadas no projeto.
