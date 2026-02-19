# Plano de Entrega — Tech Challenge Fase 4

## Escopo mínimo avaliado
- Aplicação em nuvem rodando
- Duas funções serverless com responsabilidade única
- Componentes de suporte configurados (banco/mensageria)
- Deploy automatizado de componentes atualizáveis
- Monitoramento configurado
- Notificação para problemas críticos
- Relatório semanal com média de avaliações

## Plano de execução sugerido
1. **Contratos e domínio**
   - Definir payload de `POST /avaliacao`
   - Definir eventos (`feedback-critico`, `relatorio-semanal`)
2. **Implementação da API**
   - Endpoint de ingestão
   - Persistência e publicação de evento crítico
3. **Funções serverless**
   - Função notificação crítica
   - Função relatório semanal
4. **Infraestrutura e segurança**
   - Serviços GCP, IAM, segredos, agendamentos
5. **Observabilidade**
   - Logs, métricas e alertas
6. **Automação e demonstração**
   - Pipeline de deploy
   - Script de demonstração para vídeo

## Checklist para vídeo de demonstração
- Mostrar API recebendo `POST /avaliacao`
- Mostrar função crítica disparando para caso `ALTA`
- Mostrar execução da função semanal (manual/forçada e agendada)
- Mostrar dashboard/monitoramento e logs
- Mostrar configuração de segurança (IAM/segredos)

## Evidências no repositório
- README com arquitetura e instruções
- Documentação de deploy e operação
- Código da API e funções
- Arquivos de infraestrutura (quando adicionados)
