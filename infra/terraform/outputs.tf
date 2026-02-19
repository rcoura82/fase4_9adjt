output "pubsub_topic_critico" {
  description = "Nome do tópico de feedback crítico."
  value       = google_pubsub_topic.feedback_critico.name
}

output "firestore_collection" {
  description = "Coleção usada para armazenar avaliações."
  value       = var.firestore_collection
}

output "api_feedback_service_account_email" {
  description = "Service account da API de feedback."
  value       = google_service_account.api_feedback.email
}

output "notificacao_service_account_email" {
  description = "Service account da função de notificação crítica."
  value       = google_service_account.notificacao_critica.email
}

output "relatorio_service_account_email" {
  description = "Service account da função de relatório semanal."
  value       = google_service_account.relatorio_semanal.email
}

output "scheduler_service_account_email" {
  description = "Service account do Cloud Scheduler."
  value       = google_service_account.scheduler.email
}
