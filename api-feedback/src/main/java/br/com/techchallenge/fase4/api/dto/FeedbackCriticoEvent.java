package br.com.techchallenge.fase4.api.dto;

public record FeedbackCriticoEvent(
        String id,
        String descricao,
        String urgencia,
        String dataEnvio
) {
}