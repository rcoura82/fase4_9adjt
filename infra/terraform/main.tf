locals {
  required_apis = toset([
    "run.googleapis.com",
    "cloudfunctions.googleapis.com",
    "cloudscheduler.googleapis.com",
    "pubsub.googleapis.com",
    "firestore.googleapis.com",
    "eventarc.googleapis.com",
    "artifactregistry.googleapis.com",
    "cloudbuild.googleapis.com"
  ])

  api_sa_roles = toset([
    "roles/datastore.user",
    "roles/pubsub.publisher",
    "roles/logging.logWriter"
  ])

  notificacao_sa_roles = toset([
    "roles/logging.logWriter"
  ])

  relatorio_sa_roles = toset([
    "roles/datastore.user",
    "roles/logging.logWriter"
  ])

  scheduler_sa_roles = toset([
    "roles/run.invoker",
    "roles/logging.logWriter"
  ])
}

resource "google_project_service" "required" {
  for_each           = local.required_apis
  project            = var.project_id
  service            = each.value
  disable_on_destroy = false
}

resource "google_firestore_database" "default" {
  project     = var.project_id
  name        = "(default)"
  location_id = var.firestore_location
  type        = "FIRESTORE_NATIVE"

  depends_on = [
    google_project_service.required["firestore.googleapis.com"]
  ]
}

resource "google_pubsub_topic" "feedback_critico" {
  name    = var.pubsub_topic_critico
  project = var.project_id

  depends_on = [
    google_project_service.required["pubsub.googleapis.com"]
  ]
}

resource "google_service_account" "api_feedback" {
  project      = var.project_id
  account_id   = "api-feedback-sa"
  display_name = "Service Account API Feedback"
}

resource "google_service_account" "notificacao_critica" {
  project      = var.project_id
  account_id   = "fn-notificacao-critica-sa"
  display_name = "Service Account Funcao Notificacao Critica"
}

resource "google_service_account" "relatorio_semanal" {
  project      = var.project_id
  account_id   = "fn-relatorio-semanal-sa"
  display_name = "Service Account Funcao Relatorio Semanal"
}

resource "google_service_account" "scheduler" {
  project      = var.project_id
  account_id   = "scheduler-relatorio-sa"
  display_name = "Service Account Cloud Scheduler Relatorio"
}

resource "google_project_iam_member" "api_feedback_roles" {
  for_each = local.api_sa_roles
  project  = var.project_id
  role     = each.value
  member   = "serviceAccount:${google_service_account.api_feedback.email}"
}

resource "google_project_iam_member" "notificacao_roles" {
  for_each = local.notificacao_sa_roles
  project  = var.project_id
  role     = each.value
  member   = "serviceAccount:${google_service_account.notificacao_critica.email}"
}

resource "google_project_iam_member" "relatorio_roles" {
  for_each = local.relatorio_sa_roles
  project  = var.project_id
  role     = each.value
  member   = "serviceAccount:${google_service_account.relatorio_semanal.email}"
}

resource "google_project_iam_member" "scheduler_roles" {
  for_each = local.scheduler_sa_roles
  project  = var.project_id
  role     = each.value
  member   = "serviceAccount:${google_service_account.scheduler.email}"
}

resource "google_cloud_scheduler_job" "relatorio_semanal" {
  count       = trimspace(var.weekly_function_url) == "" ? 0 : 1
  project     = var.project_id
  region      = var.region
  name        = "relatorio-semanal-job"
  description = "Dispara função semanal de relatório"
  schedule    = var.scheduler_cron
  time_zone   = var.scheduler_timezone

  http_target {
    uri         = var.weekly_function_url
    http_method = "POST"

    oidc_token {
      service_account_email = google_service_account.scheduler.email
      audience              = var.weekly_function_url
    }
  }

  depends_on = [
    google_project_service.required["cloudscheduler.googleapis.com"]
  ]
}
