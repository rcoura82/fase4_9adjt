# Matriz de Avaliação — Tech Challenge Fase 4

Use esta matriz para garantir aderência total aos critérios da avaliação.

## Critério 1 — Modelo de cloud e componentes
Status: [ ]

Evidências:
- [ ] Stack declarada no README.
- [ ] Arquitetura com componentes GCP documentada.
- [ ] Justificativa do modelo serverless apresentada no vídeo.

Onde mostrar:
- `README.md`
- `docs/arquitetura.md`
- Vídeo (seção de arquitetura)

## Critério 2 — Funcionamento correto da aplicação
Status: [ ]

Evidências:
- [ ] API recebe avaliações.
- [ ] Persistência ocorre corretamente.
- [ ] Notificação crítica é acionada.
- [ ] Relatório semanal é gerado.

Onde mostrar:
- Vídeo (demonstração ponta a ponta)
- Logs/saídas no terminal e/ou Console GCP

## Critério 3 — Qualidade do código e documentação
Status: [ ]

Evidências:
- [ ] Código organizado por responsabilidade.
- [ ] Estrutura de módulos coerente.
- [ ] Documentação atualizada.

Onde mostrar:
- `api-feedback/`
- `functions/`
- `infra/terraform/`
- `README.md`

## Critério 4 — Descrição do projeto (arquitetura, deploy, monitoramento, funções)
Status: [ ]

Evidências:
- [ ] Arquitetura documentada.
- [ ] Instruções de deploy documentadas.
- [ ] Monitoramento configurado e explicado.
- [ ] Documentação das funções criada.

Onde mostrar:
- `docs/arquitetura.md`
- `README.md`
- `docs/checklist-hardening-gcp.md`

## Critério 5 — Configuração cloud e segurança
Status: [ ]

Evidências:
- [ ] Ambiente cloud ativo no projeto correto.
- [ ] Funções serverless configuradas.
- [ ] IAM e segurança explicados.
- [ ] Pipeline com autenticação segura (WIF).

Onde mostrar:
- Console GCP no vídeo
- `.github/workflows/deploy-gcp.yml`
- `infra/terraform/main.tf`

---

## Resultado final
- [ ] Todos os critérios marcados como atendidos.
- [ ] Link do vídeo adicionado no README.
- [ ] Revisão final feita por pelo menos 1 membro do grupo.
