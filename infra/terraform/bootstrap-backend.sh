#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 3 ]]; then
  echo "Uso: $0 <PROJECT_ID> <BUCKET_NAME> <REGION>"
  echo "Exemplo: $0 meu-projeto tfstate-fase4-123 southamerica-east1"
  exit 1
fi

PROJECT_ID="$1"
BUCKET_NAME="$2"
REGION="$3"

# Cria bucket para estado remoto do Terraform
if ! gcloud storage buckets describe "gs://${BUCKET_NAME}" --project "${PROJECT_ID}" >/dev/null 2>&1; then
  gcloud storage buckets create "gs://${BUCKET_NAME}" \
    --project "${PROJECT_ID}" \
    --location "${REGION}" \
    --uniform-bucket-level-access
fi

# Habilita versionamento para histórico/rollback de estado
if ! gcloud storage buckets describe "gs://${BUCKET_NAME}" --project "${PROJECT_ID}" --format='value(versioning.enabled)' | grep -qi true; then
  gcloud storage buckets update "gs://${BUCKET_NAME}" \
    --project "${PROJECT_ID}" \
    --versioning
fi

echo "Bucket de estado remoto pronto: gs://${BUCKET_NAME}"
echo "Agora rode em infra/terraform:"
echo "terraform init -reconfigure -backend-config=\"bucket=${BUCKET_NAME}\" -backend-config=\"prefix=fase4_9adjt/envs/dev\""
