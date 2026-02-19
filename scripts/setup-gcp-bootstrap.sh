#!/usr/bin/env bash
set -euo pipefail

PROJECT_ID="${1:-reflecting-node-414916}"
REGION="${2:-southamerica-east1}"
TF_STATE_BUCKET="${3:-${PROJECT_ID}-tfstate-fase4}"
TF_STATE_PREFIX="${4:-fase4_9adjt/envs/dev}"
BILLING_ACCOUNT_ID="${5:-}"

printf "\n[1/8] Configurando projeto e região...\n"
gcloud config set project "${PROJECT_ID}"
gcloud config set compute/region "${REGION}"

printf "\n[2/8] Verificando billing do projeto...\n"
if ! gcloud billing projects describe "${PROJECT_ID}" --format='value(billingEnabled)' | grep -qi true; then
  if [[ -n "${BILLING_ACCOUNT_ID}" ]]; then
    echo "Billing desabilitado. Vinculando projeto ${PROJECT_ID} à conta ${BILLING_ACCOUNT_ID}..."
    gcloud billing projects link "${PROJECT_ID}" --billing-account="${BILLING_ACCOUNT_ID}"
  else
    echo "Billing não está habilitado para ${PROJECT_ID}."
    echo "Execute novamente informando o Billing Account ID no 5º argumento."
    echo "Exemplo: $0 ${PROJECT_ID} ${REGION} ${TF_STATE_BUCKET} ${TF_STATE_PREFIX} 000000-000000-000000"
    exit 1
  fi
fi

printf "\n[3/8] Habilitando APIs necessárias...\n"
gcloud services enable \
  run.googleapis.com \
  cloudfunctions.googleapis.com \
  cloudscheduler.googleapis.com \
  pubsub.googleapis.com \
  firestore.googleapis.com \
  eventarc.googleapis.com \
  artifactregistry.googleapis.com \
  cloudbuild.googleapis.com \
  iam.googleapis.com \
  secretmanager.googleapis.com \
  --project "${PROJECT_ID}"

printf "\n[4/8] Criando bucket remoto de state Terraform (se necessário)...\n"
if ! gcloud storage buckets describe "gs://${TF_STATE_BUCKET}" --project "${PROJECT_ID}" >/dev/null 2>&1; then
  gcloud storage buckets create "gs://${TF_STATE_BUCKET}" \
    --project "${PROJECT_ID}" \
    --location "${REGION}" \
    --uniform-bucket-level-access
fi
gcloud storage buckets update "gs://${TF_STATE_BUCKET}" --project "${PROJECT_ID}" --versioning

printf "\n[5/8] Inicializando Terraform com backend remoto...\n"
pushd infra/terraform >/dev/null
terraform init -reconfigure \
  -backend-config="bucket=${TF_STATE_BUCKET}" \
  -backend-config="prefix=${TF_STATE_PREFIX}"

printf "\n[6/8] Validando Terraform...\n"
terraform fmt -check -recursive
terraform validate

printf "\n[7/8] Aplicando infraestrutura base...\n"
terraform apply -auto-approve \
  -var="project_id=${PROJECT_ID}" \
  -var="region=${REGION}" \
  -var="weekly_function_url="

printf "\n[8/8] Capturando outputs importantes...\n"
terraform output
popd >/dev/null

cat <<EOF

✅ Bootstrap da GCP concluído para ${PROJECT_ID}

Próximo passo (GitHub Actions):
- Repository variables:
  - GCP_PROJECT_ID=${PROJECT_ID}
  - GCP_REGION=${REGION}
  - GCP_TF_STATE_BUCKET=${TF_STATE_BUCKET}
  - GCP_TF_STATE_PREFIX=${TF_STATE_PREFIX}
- Repository secrets:
  - GCP_WORKLOAD_IDENTITY_PROVIDER
  - GCP_DEPLOYER_SERVICE_ACCOUNT

Depois, execute o workflow deploy-gcp no GitHub Actions.
EOF
