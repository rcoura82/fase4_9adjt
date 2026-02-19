package br.com.techchallenge.fase4.api.dto;

public record AvaliacaoResponse(
        String id,
        String dataEnvio,
        String status,
        boolean critica
) {
}