# Tech Challenge — Fase 4

Plataforma de feedback para aulas on-line com foco em **Cloud Computing**, **Serverless** e **Deploy em nuvem**.

## Objetivo do projeto
Permitir que estudantes enviem feedbacks e que administradores recebam alertas para casos críticos, além de relatórios semanais com métricas de satisfação.

## Stack definida (time)
- **Cloud:** GCP
- **Linguagem:** Java
- **Framework:** Quarkus

## Requisitos funcionais mapeados
- Endpoint de entrada: `POST /avaliacao`
- Persistência dos feedbacks
- Notificação automática para itens críticos
- Relatório semanal com médias e agregações

## Arquitetura proposta (MVP)
Componentes principais:
- **API de Feedback (Quarkus em Cloud Run)**
	- Recebe `POST /avaliacao`
	- Persiste dados
	- Publica evento de criticidade
- **Função serverless de notificação crítica**
	- Consome evento de feedback crítico
	- Envia aviso para administradores
- **Função serverless de relatório semanal**
	- Disparada por agendamento semanal
	- Gera consolidação de feedbacks e média de notas

Serviços GCP sugeridos:
- Cloud Run (API)
- Cloud Functions (funções serverless)
- Pub/Sub (eventos)
- Cloud Scheduler (agendamento semanal)
- Firestore (armazenamento de feedbacks)
- Cloud Monitoring + Logging (observabilidade)
- IAM + Secret Manager (segurança e governança)

## Contrato inicial de entrada
`POST /avaliacao`

```json
{
	"descricao": "string",
	"nota": 0,
	"urgencia": "BAIXA|MEDIA|ALTA"
}
```

Resposta (`201 Created`):

```json
{
	"id": "uuid",
	"dataEnvio": "2026-02-19T23:30:00Z",
	"status": "RECEBIDA",
	"critica": true
}
```

## Estrutura inicial do repositório
- `docs/arquitetura.md`: arquitetura detalhada e fluxo ponta a ponta
- `docs/plano-entrega.md`: plano de execução, checklist de avaliação e vídeo
- `docs/checklist-hardening-gcp.md`: runbook de hardening no Console GCP
- `.github/copilot-instructions.md`: instruções do projeto para agentes AI

## Estrutura de código (scaffold atual)
- `pom.xml`: agregador Maven multi-módulo
- `api-feedback/`: API Quarkus com endpoint `POST /avaliacao`
- `functions/notificacao-critica/`: função serverless Java para eventos críticos (Pub/Sub)
- `functions/relatorio-semanal/`: função serverless Java para relatório semanal (HTTP + Scheduler)
- `infra/terraform/`: IaC para Firestore, Pub/Sub, Scheduler e IAM
- `.github/workflows/deploy-gcp.yml`: pipeline de deploy automatizado

## Infraestrutura como código (Terraform)
Arquivos em `infra/terraform` provisionam:
- Firestore (database default)
- Tópico Pub/Sub (`feedback-critico`)
- Service Accounts de API, funções e Scheduler
- IAM mínimo para cada componente
- Cloud Scheduler semanal (criado após URL da função de relatório existir)

Backend remoto do Terraform:
- `infra/terraform/backend.tf` usa backend `gcs`
- estado remoto em bucket GCS (evita `terraform.tfstate` local no time)
- lock de concorrência gerenciado pelo próprio backend GCS do Terraform

### Bootstrap do bucket de state remoto

```bash
chmod +x infra/terraform/bootstrap-backend.sh
./infra/terraform/bootstrap-backend.sh "$GOOGLE_CLOUD_PROJECT" "SEU_BUCKET_TFSTATE" "southamerica-east1"
```

Bootstrap completo (projeto + billing + Terraform) via script:

```bash
chmod +x scripts/setup-gcp-bootstrap.sh
./scripts/setup-gcp-bootstrap.sh "SEU_PROJECT_ID" "southamerica-east1" "SEU_BUCKET_TFSTATE" "fase4_9adjt/envs/dev" "SEU_BILLING_ACCOUNT_ID"
```

Alternativa manual:

```bash
gcloud storage buckets create gs://SEU_BUCKET_TFSTATE \
	--project "$GOOGLE_CLOUD_PROJECT" \
	--location "southamerica-east1" \
	--uniform-bucket-level-access

gcloud storage buckets update gs://SEU_BUCKET_TFSTATE \
	--project "$GOOGLE_CLOUD_PROJECT" \
	--versioning
```

Comandos locais de IaC:

```bash
cd infra/terraform
cp terraform.tfvars.example terraform.tfvars
terraform init -reconfigure \
	-backend-config="bucket=SEU_BUCKET_TFSTATE" \
	-backend-config="prefix=fase4_9adjt/envs/dev"
terraform plan -var="project_id=$GOOGLE_CLOUD_PROJECT" -var="region=southamerica-east1"
terraform apply -var="project_id=$GOOGLE_CLOUD_PROJECT" -var="region=southamerica-east1"
```

Observação: `weekly_function_url` pode iniciar vazio; o job do Scheduler é criado quando a URL final da função estiver disponível.

## Comandos locais
Pré-requisito: Java 17 + Maven 3.9+

Credenciais GCP (Application Default Credentials):

```bash
gcloud auth application-default login
export GOOGLE_CLOUD_PROJECT="seu-projeto"
```

Config opcional da API (sobrescreve defaults):

```bash
export GCP_PROJECT_ID="seu-projeto"
export GCP_FIRESTORE_COLLECTION="avaliacoes"
export GCP_PUBSUB_TOPIC_CRITICO="feedback-critico"
```

Build de todos os módulos:

```bash
mvn clean package
```

Executar API Quarkus local:

```bash
mvn -pl api-feedback quarkus:dev
```

Executar função de notificação local (porta 8081):

```bash
mvn -pl functions/notificacao-critica function:run
```

Executar função de relatório local (porta 8082):

```bash
mvn -pl functions/relatorio-semanal function:run
```

Config da função de relatório (opcional):

```bash
export FIRESTORE_COLLECTION="avaliacoes"
```

Exemplo de retorno consolidado do relatório semanal:

```json
{
	"status": "RELATORIO_GERADO",
	"periodoInicio": "2026-02-12T08:00:00Z",
	"periodoFim": "2026-02-19T08:00:00Z",
	"mediaAvaliacoes": 7.8,
	"quantidadeAvaliacoesPorDia": {
		"2026-02-17": 3,
		"2026-02-18": 5
	},
	"quantidadeAvaliacoesPorUrgencia": {
		"BAIXA": 2,
		"MEDIA": 4,
		"ALTA": 2
	},
	"itens": [
		{
			"descricao": "Demora no suporte",
			"urgencia": "ALTA",
			"dataEnvio": "2026-02-18T21:00:00Z"
		}
	]
}
```

## Deploy base no GCP (referência inicial)
Defina variáveis de ambiente:

```bash
export PROJECT_ID="seu-projeto"
export REGION="southamerica-east1"
```

Deploy da API (Cloud Run):

```bash
gcloud run deploy api-feedback \
	--source api-feedback \
	--region "$REGION" \
	--project "$PROJECT_ID" \
	--no-allow-unauthenticated
```

Para expor publicamente de forma intencional, troque para `--allow-unauthenticated`.

Deploy função de notificação crítica (Cloud Functions Gen2, Pub/Sub):

```bash
gcloud functions deploy notificacao-critica \
	--gen2 \
	--runtime java17 \
	--region "$REGION" \
	--project "$PROJECT_ID" \
	--source functions/notificacao-critica \
	--entry-point br.com.techchallenge.fase4.functions.notificacao.CriticalNotificationFunction \
	--trigger-topic feedback-critico
```

Deploy função de relatório semanal (Cloud Functions Gen2, HTTP):

```bash
gcloud functions deploy relatorio-semanal \
	--gen2 \
	--runtime java17 \
	--region "$REGION" \
	--project "$PROJECT_ID" \
	--source functions/relatorio-semanal \
	--entry-point br.com.techchallenge.fase4.functions.relatorio.WeeklyReportFunction \
	--trigger-http
```

Agendamento semanal (Cloud Scheduler):

```bash
gcloud scheduler jobs create http relatorio-semanal-job \
	--project "$PROJECT_ID" \
	--location "$REGION" \
	--schedule "0 8 * * 1" \
	--uri "URL_DA_FUNCAO_RELATORIO" \
	--http-method POST
```

## Deploy automatizado (GitHub Actions)
Workflow: `.github/workflows/deploy-gcp.yml`

Jobs:
- `quality`: `terraform init` (backend remoto), `terraform fmt -check`, `terraform validate`
- `deploy`: executa apenas com `needs: quality`

Fluxo do pipeline:
1. Job `quality`: `terraform init` com backend remoto (GCS)
2. Job `quality`: `terraform fmt -check` e `terraform validate`
3. Job `deploy` (somente se quality passou): build Maven dos módulos
4. Job `deploy`: `terraform apply` da base de infraestrutura
5. Job `deploy`: Deploy da API (Cloud Run)
6. Job `deploy`: Deploy da função de notificação crítica (Gen2/PubSub)
7. Job `deploy`: Deploy da função de relatório semanal (Gen2/HTTP)
8. Job `deploy`: Leitura da URL final da função semanal
9. Job `deploy`: `terraform apply` final para criar/atualizar o Cloud Scheduler

Configure em **Settings > Secrets and variables > Actions**:

Variáveis (`Repository variables`):
- `GCP_PROJECT_ID`
- `GCP_REGION` (ex.: `southamerica-east1`)
- `GCP_TF_STATE_BUCKET` (bucket GCS do state remoto)
- `GCP_TF_STATE_PREFIX` (ex.: `fase4_9adjt/envs/prod`)
- `GCP_RUN_ALLOW_UNAUTHENTICATED` (`true` para API pública; padrão recomendado: `false`)

Segredos (`Repository secrets`):
- `GCP_WORKLOAD_IDENTITY_PROVIDER`
- `GCP_DEPLOYER_SERVICE_ACCOUNT`

Recomendação: usar Workload Identity Federation e evitar chave JSON estática no repositório.

## Fluxo de desenvolvimento nesta fase
1. Detalhar contratos e modelo de dados
2. Implementar API de ingestão (Quarkus)
3. Implementar 2 funções serverless com responsabilidade única
4. Provisionar infraestrutura na GCP
5. Configurar monitoramento, alertas e segurança
6. Automatizar deploy dos componentes

## Entregáveis da fase
- Repositório aberto com código-fonte
- Documentação com arquitetura, deploy, monitoramento e funções
- Vídeo demonstrando a solução funcionando em nuvem

## Padrão de release notes
- Arquivo oficial de histórico: `CHANGELOG.md`
- Template para novas versões: `.github/release-notes-template.md`
- Estrutura padronizada: **Features**, **Fixes**, **Infra** e **Breaking Changes**
- Próxima versão já preparada no changelog: `v0.1.1 - Em preparação`

## Kit de entrega (fase 4)
- Checklist de entregáveis: `docs/entregaveis-tech-challenge-fase4.md`
- Roteiro de vídeo: `docs/roteiro-video-demonstracao.md`
- Matriz de avaliação: `docs/matriz-avaliacao-fase4.md`
- Hardening GCP: `docs/checklist-hardening-gcp.md`
