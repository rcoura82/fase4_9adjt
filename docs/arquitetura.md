# Arquitetura da Solução (MVP)

## Visão geral
A solução é composta por uma API de ingestão de feedback, duas funções serverless com responsabilidades separadas e componentes de suporte para armazenamento, mensageria, agendamento, segurança e observabilidade.

## Componentes
- **API Feedback (Quarkus / Cloud Run)**
  - Endpoint `POST /avaliacao`
  - Valida payload
  - Persiste feedback no Firestore (`avaliacoes`)
  - Publica evento de criticidade no tópico Pub/Sub `feedback-critico`
- **Função 1 — Notificação Crítica (Cloud Functions)**
  - Trigger: Pub/Sub (`feedback-critico`)
  - Responsabilidade: notificar administradores
- **Função 2 — Relatório Semanal (Cloud Functions)**
  - Trigger: Cloud Scheduler (1x por semana)
  - Responsabilidade: consultar Firestore e gerar resumo semanal consolidado

## Dados principais
Entrada (`POST /avaliacao`):
- `descricao` (string)
- `nota` (int 0 a 10)
- `urgencia` (`BAIXA`, `MEDIA`, `ALTA`)
- `dataEnvio` (timestamp gerado no backend)

Saída da notificação crítica:
- Descrição
- Urgência
- Data de envio

Saída do relatório semanal:
- Descrição
- Urgência
- Data de envio
- Quantidade de avaliações por dia
- Quantidade de avaliações por urgência
- Média de avaliações

## Fluxo ponta a ponta
1. Estudante envia feedback para `POST /avaliacao`.
2. API valida e grava no banco.
3. Se urgência for crítica (ex.: `ALTA`), API publica evento no tópico Pub/Sub.
4. Função de notificação consome evento e envia alerta para administradores.
5. Semanalmente, Cloud Scheduler dispara função de relatório.
6. Função agrega dados da semana e gera relatório para administração.

## Segurança e governança
- IAM com princípio do menor privilégio por serviço.
- Secrets em Secret Manager.
- Logs de auditoria e acesso habilitados.
- Separação de ambientes (dev/homolog/prod) quando disponível.

Implementação atual de IAM via Terraform:
- `api-feedback-sa`: `roles/datastore.user`, `roles/pubsub.publisher`, `roles/logging.logWriter`
- `fn-notificacao-critica-sa`: `roles/logging.logWriter`
- `fn-relatorio-semanal-sa`: `roles/datastore.user`, `roles/logging.logWriter`
- `scheduler-relatorio-sa`: `roles/run.invoker`, `roles/logging.logWriter`

## Observabilidade
- Logs estruturados por componente.
- Métricas de requisição, latência e falhas.
- Alertas para:
  - erro na API
  - falha nas funções
  - ausência de execução do job semanal

## Decisões de responsabilidade única
- API: ingestão e roteamento de eventos, sem lógica de envio de e-mail.
- Função crítica: somente notificação de urgência.
- Função semanal: somente agregação e relatório periódico.

## Infra e deploy automatizado
- Infra provisionada por Terraform em `infra/terraform`.
- Estado Terraform remoto em GCS (`backend "gcs"`) para colaboração de equipe.
- Deploy automatizado por GitHub Actions em `.github/workflows/deploy-gcp.yml`.
- Pipeline aplica IaC base, publica API/funções e reaplica IaC para configurar Scheduler com URL real da função semanal.
