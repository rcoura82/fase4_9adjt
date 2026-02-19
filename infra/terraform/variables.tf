variable "project_id" {
  description = "ID do projeto GCP."
  type        = string
}

variable "region" {
  description = "Região principal para Cloud Run/Functions/Scheduler."
  type        = string
  default     = "southamerica-east1"
}

variable "firestore_location" {
  description = "Localização do Firestore."
  type        = string
  default     = "southamerica-east1"
}

variable "firestore_collection" {
  description = "Nome da coleção de avaliações no Firestore."
  type        = string
  default     = "avaliacoes"
}

variable "pubsub_topic_critico" {
  description = "Nome do tópico Pub/Sub para feedback crítico."
  type        = string
  default     = "feedback-critico"
}

variable "scheduler_cron" {
  description = "Cron do relatório semanal."
  type        = string
  default     = "0 8 * * 1"
}

variable "scheduler_timezone" {
  description = "Timezone do Cloud Scheduler."
  type        = string
  default     = "America/Sao_Paulo"
}

variable "weekly_function_url" {
  description = "URL HTTP da função de relatório semanal (Gen2). Quando vazio, o job não é criado."
  type        = string
  default     = ""
}
