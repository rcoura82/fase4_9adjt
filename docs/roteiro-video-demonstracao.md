# Roteiro de Vídeo — Demonstração da Solução

Objetivo: demonstrar funcionamento completo da plataforma, funções serverless e configuração em nuvem.

## Estrutura sugerida (4 a 8 minutos)

## 1) Abertura (30s)
- Contexto do problema e objetivo do projeto.
- Stack escolhida: GCP + Java + Quarkus + Serverless.

## 2) Arquitetura (45s)
- Mostrar `docs/arquitetura.md`.
- Explicar fluxo:
  1. `POST /avaliacao` na API.
  2. Persistência no Firestore.
  3. Evento crítico no Pub/Sub.
  4. Função de notificação crítica.
  5. Função de relatório semanal.

## 3) Código e estrutura (45s)
- Mostrar rapidamente:
  - `api-feedback/`
  - `functions/notificacao-critica/`
  - `functions/relatorio-semanal/`
  - `infra/terraform/`
  - `.github/workflows/deploy-gcp.yml`

## 4) Ambiente cloud e segurança (60s)
- Mostrar no Console GCP:
  - Projeto ativo
  - Firestore
  - Pub/Sub tópico
  - Cloud Functions
  - Cloud Run
  - Cloud Scheduler
- Mencionar IAM mínimo e backend remoto do Terraform em GCS.

## 5) Demonstração funcional (90s)
- Enviar uma avaliação com urgência alta.
- Mostrar persistência.
- Mostrar função crítica processando evento.
- Executar/mostrar relatório semanal e resposta consolidada.

## 6) Deploy automatizado (45s)
- Mostrar workflow no GitHub Actions.
- Mostrar jobs `quality` e `deploy`.
- Mostrar uso de Workload Identity Federation.

## 7) Encerramento (30s)
- Recapitular requisitos atendidos.
- Destacar documentação e próximos passos.

---

## Checklist de gravação
- [ ] Áudio claro e tela legível.
- [ ] Fonte do terminal aumentada.
- [ ] Sem segredos/token visíveis.
- [ ] Fluxo completo sem cortes críticos.
- [ ] Link do vídeo pronto para anexar na entrega.
