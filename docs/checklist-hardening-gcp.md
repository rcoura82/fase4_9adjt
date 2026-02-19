# Runbook — Hardening no Console GCP

Projeto alvo (padrão): `reflecting-node-414916`

Objetivo: reduzir risco de custo indevido, abuso de recursos e exposição de dados, com um checklist curto e executável pelo time.

## Como usar este runbook
- Execute os itens na ordem.
- Marque status (`[ ]` pendente, `[x]` concluído).
- Registre evidências (print/link) no PR ou issue da sprint.

---

## 1) Budget e alertas de custo
**Caminho:** Billing > Budgets & alerts

- [ ] Criar budget mensal para o projeto.
- [ ] Configurar alertas em 50%, 80% e 100%.
- [ ] Habilitar alerta por custo previsto (forecast).
- [ ] Confirmar recebimento de e-mail de alerta.

**Evidência sugerida:** print da regra de budget e dos thresholds.

---

## 2) Alertas operacionais mínimos
**Caminho:** Monitoring > Alerting > Create policy

Criar políticas para:
- [ ] Taxa de erro 5xx da API Cloud Run (`api-feedback`) acima do limite definido.
- [ ] Falha de execução das Cloud Functions.
- [ ] Falha/ausência de execução do Cloud Scheduler (`relatorio-semanal-job`).

**Evidência sugerida:** lista de policies criadas e canais de notificação.

---

## 3) Quotas para conter abuso/custos
**Caminho:** IAM & Admin > Quotas

- [ ] Revisar quotas de Cloud Run.
- [ ] Revisar quotas de Cloud Functions.
- [ ] Revisar quotas de Pub/Sub.
- [ ] Revisar quotas de Firestore.
- [ ] Ajustar limites para patamar compatível com MVP.

**Evidência sugerida:** print dos limites principais ajustados.

---

## 4) Audit Logs habilitados
**Caminho:** IAM & Admin > Audit Logs

Para serviços críticos, garantir:
- [ ] Admin Read habilitado.
- [ ] Data Read habilitado.
- [ ] Data Write habilitado.

Serviços mínimos:
- [ ] Cloud Run
- [ ] Cloud Functions
- [ ] Firestore
- [ ] Pub/Sub
- [ ] Cloud Scheduler
- [ ] IAM

**Evidência sugerida:** print de configuração por serviço.

---

## 5) Retenção e exportação de logs
**Caminho:** Logging > Log Router

- [ ] Confirmar retenção adequada no Cloud Logging.
- [ ] Criar sink para BigQuery ou Cloud Storage (recomendado).
- [ ] Validar destino recebendo eventos.

**Evidência sugerida:** sink ativo com destino e filtro.

---

## 6) Revisão de IAM (mínimo privilégio)
**Caminho:** IAM & Admin > IAM

- [ ] Confirmar ausência de papéis amplos (`Owner`/`Editor`) em contas de runtime.
- [ ] Validar service accounts do projeto com papéis mínimos.
- [ ] Revisar membros externos e remover acessos não necessários.

Referência técnica no repositório:
- `infra/terraform/main.tf`

**Evidência sugerida:** lista de service accounts e roles.

---

## 7) Segurança de credenciais
**Caminho:** IAM & Admin > Service Accounts

- [ ] Verificar se existem chaves JSON ativas desnecessárias.
- [ ] Revogar/rotacionar chaves não utilizadas.
- [ ] Confirmar uso de Workload Identity Federation no GitHub Actions.

Referência técnica no repositório:
- `.github/workflows/deploy-gcp.yml`

**Evidência sugerida:** inventário de chaves e status de rotação.

---

## 8) Exposição pública da API
**Caminho:** Cloud Run > Services > api-feedback

- [ ] Confirmar se o serviço deve ser público.
- [ ] Se privado, manter sem `allow-unauthenticated`.
- [ ] Se público, documentar justificativa e controles compensatórios.

Referência técnica no repositório:
- `.github/workflows/deploy-gcp.yml`
- `README.md`

**Evidência sugerida:** configuração de autenticação do serviço.

---

## 9) Validação de cobrança anômala
**Caminho:** Billing > Reports

- [ ] Habilitar visualização por serviço e SKU.
- [ ] Revisar tendência semanal de custo.
- [ ] Definir responsável por revisão periódica.

**Evidência sugerida:** print do relatório por serviço.

---

## 10) Teste de incidente controlado
**Objetivo:** validar monitoramento ponta a ponta.

- [ ] Forçar falha controlada em ambiente de desenvolvimento.
- [ ] Confirmar geração de logs.
- [ ] Confirmar disparo de alerta.
- [ ] Confirmar evento auditável em trilha de auditoria.

**Evidência sugerida:** timeline curta do teste (erro -> log -> alerta).

---

## Critério de pronto (DoD)
- [ ] 10/10 itens concluídos.
- [ ] Evidências anexadas em issue/PR da sprint.
- [ ] Pendências remanescentes com responsável e prazo.

## Frequência recomendada
- Revisão completa: mensal.
- Revisão parcial (custos + alertas + IAM): semanal.
