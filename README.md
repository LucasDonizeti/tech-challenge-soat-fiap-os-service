# OS Service

Microsserviço Spring Boot no padrão de camadas do `catalog-service`, com build Maven, imagem Docker e deploy Helm no EKS.

## Estrutura

- `os/src/main/java/com/techchallenge/os`: aplicação organizada em `application`, `domain` e `infrastructure`.
- `os/src/test/java/com/techchallenge/os`: estrutura equivalente para os testes.
- `os/src/main/resources/db/migration`: migrations próprias do serviço.
- `.github/workflows/pipeline.yml`: build, testes, publicação no ECR e deploy no EKS pela branch `develop`.
- `k8s`: chart Helm para o serviço.

## Configuração de infraestrutura

O chart usa o serviço `os-service`, o NodePort `30082` e o contexto `/v1/os-service`. Configure a rota correspondente no API Gateway/NLB da infraestrutura antes de expor o serviço publicamente. O pipeline mantém o cluster e o RDS compartilhados usados pelos demais serviços, mas `SPRING_DATASOURCE_URL` aponta para o database exclusivo `os_db`, separado do Catalog (`catalog_db`) e do database legado `oficina`. O serviço é proprietário das tabelas `ordens_servico`, `itens_servico` e `itens_mro`; os IDs de clientes, veículos, serviços, MROs e pagamentos são referências externas sem foreign keys entre databases. Use um database dedicado e vazio na primeira inicialização: as migrations criam o schema, mas não transferem registros do database legado `oficina`.

## Build local

```powershell
cd os
.\mvnw.cmd -B verify
```

## Mensageria local

O perfil local usa RabbitMQ (`MESSAGING_TYPE=rabbit`). Suba o broker pelo Compose
central do projeto (`docker compose up -d rabbitmq`) antes de iniciar OS e Billing.
O painel de gerenciamento fica disponível em `http://localhost:15672`.

No EKS, o chart Helm seleciona `MESSAGING_TYPE=aws` para usar SNS/SQS. As filas são
provisionadas pelo Terraform no repositório `k8s-infra`; a estratégia
`SPRING_CLOUD_AWS_SQS_QUEUE_NOT_FOUND_STRATEGY=FAIL` impede que o serviço tente
criá-las se estiverem ausentes. Em ambiente local, o serviço usa RabbitMQ
(`MESSAGING_TYPE=rabbit`), sem criação de filas SQS.
Como a dependência AMQP continua no classpath para uso local, o chart de produção
define `RABBIT_HEALTH_CHECK_ENABLED=false` para não incluir o RabbitMQ inativo no
health geral quando o transporte usado é AWS.
Para essa configuração iniciar, a infraestrutura do `k8s-infra` precisa ter aplicado
o Launch Template dos nodes com IMDSv2 e hop limit 2 e as permissões de mensageria
na `LabRole`. Ela pode publicar em todos os tópicos e enviar/consumir mensagens em
todas as filas SNS/SQS provisionadas pelo módulo. Os pods obtêm credenciais da role
do node; por isso, outros pods no mesmo node também podem herdar essas permissões.