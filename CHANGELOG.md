# Changelog

Todas as mudanças relevantes deste projeto serão documentadas neste arquivo.

## v0.1.0 - 2026-02-19

### 🎯 Escopo da entrega
- Bootstrap completo da plataforma de feedback para o Tech Challenge Fase 4.
- Estrutura inicial de API, funções serverless, infraestrutura e automação de deploy.

### ✅ O que foi entregue
- API Quarkus com endpoint `POST /avaliacao`.
- Persistência de avaliações no Firestore.
- Publicação de eventos críticos no Pub/Sub.
- Função serverless de notificação crítica (trigger Pub/Sub).
- Função serverless de relatório semanal com consolidação de dados (trigger HTTP/Scheduler).
- IaC com Terraform para Firestore, Pub/Sub, Scheduler e IAM mínimo.
- Backend remoto Terraform em GCS com script de bootstrap.
- Pipeline GitHub Actions com jobs `quality` e `deploy`.
- Gate de qualidade no CI com `terraform fmt -check` e `terraform validate`.

### 📚 Documentação incluída
- Arquitetura da solução (MVP).
- Plano de entrega.
- Instruções operacionais de build, deploy e configuração de variáveis/secrets.

### 🔜 Próximos passos sugeridos
- Adicionar monitoramento com alertas operacionais (Cloud Monitoring).
- Separar ambientes (`dev`, `homolog`, `prod`) com prefixos/state dedicados.
- Publicar release no GitHub com assets e notas de versão.