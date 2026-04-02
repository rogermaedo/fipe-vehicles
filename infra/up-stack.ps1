$ErrorActionPreference = "Stop"
Set-Location (Split-Path $PSScriptRoot -Parent)
docker compose up -d --build
Write-Host "Stack na raiz do repo. api-1: http://localhost:8080  |  api-2 (dev UI): http://localhost:8081/q/dev-ui  |  RabbitMQ: http://localhost:15672"
