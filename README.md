# OS Service

Microsserviço Spring Boot no padrão de camadas do `catalog-service`, com build Maven, imagem Docker e deploy Helm no EKS.

## Estrutura

- `os/src/main/java/com/techchallenge/os`: aplicação organizada em `application`, `domain` e `infrastructure`.
- `os/src/test/java/com/techchallenge/os`: estrutura equivalente para os testes.
- `os/src/main/resources/migration`: migrations próprias do serviço.
- `.github/workflows/pipeline.yml`: build, testes, publicação no ECR e deploy no EKS pela branch `develop`.
- `k8s`: chart Helm para o serviço.

## Configuração de infraestrutura

O chart usa o serviço `os-service`, o NodePort `30082` e o contexto `/v1/os-service`. Configure a rota correspondente no API Gateway/NLB da infraestrutura antes de expor o serviço publicamente. O pipeline mantém o cluster e o RDS compartilhados usados pelos demais serviços.

## Build local

```powershell
cd os
.\mvnw.cmd -B verify
```