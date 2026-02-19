# Copilot Instructions — fase4_9adjt

## Contexto do projeto
- Este repositório está em bootstrap para o Tech Challenge da Fase 4.
- A stack do time foi definida como **GCP + Java + Quarkus**.
- O foco funcional é uma plataforma de feedback com ingestão, alerta crítico e relatório semanal.

## Como agir neste repositório (estado atual)
- Trate mudanças como **fundacionais**: priorize criar estrutura mínima e clara antes de adicionar complexidade.
- Antes de implementar features, valide se a estrutura atual já inclui módulos de API, funções e infraestrutura.
- Mantenha coerência com a stack já decidida; não introduza runtime paralelo sem necessidade explícita.

## Padrões e convenções descobertas
- Convenção de documentação: português simples e objetivo.
- Arquitetura alvo (MVP):
  - API de feedback (Quarkus)
  - Função serverless para notificação crítica
  - Função serverless para relatório semanal
- Endpoint de referência: `POST /avaliacao`.

## Workflow do desenvolvedor (descoberto)
- Se não houver automação provisionada ainda, priorize:
  1. Definir contratos (payload e eventos).
  2. Implementar componentes com responsabilidade única.
  3. Documentar comandos de build/deploy no README na mesma alteração.

## Diretrizes para agentes AI ao editar
- Mantenha mudanças pequenas e justificadas pelo estado real do repositório.
- Sempre atualize [README.md](../README.md) ao introduzir estrutura nova (pastas, comandos, runtime).
- Se criar configuração de ferramenta (ex.: `pom.xml`, `Dockerfile`, Terraform), documente o comando principal no README na mesma alteração.
- Para funções serverless, preserve responsabilidade única e explicite trigger, entrada e saída de cada função.
- Não invente padrões “existentes”; declare explicitamente quando uma decisão for nova.

## Checklist rápido antes de concluir
- Há referência explícita aos arquivos realmente existentes?
- Novas decisões de stack e nuvem foram documentadas?
- Cada função serverless tem responsabilidade única descrita?
- O README reflete o estado atual do projeto após a mudança?
