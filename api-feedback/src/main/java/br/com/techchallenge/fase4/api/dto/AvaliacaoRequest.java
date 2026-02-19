package br.com.techchallenge.fase4.api.dto;

import br.com.techchallenge.fase4.api.model.Urgencia;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AvaliacaoRequest(
        @NotBlank String descricao,
        @NotNull @Min(0) @Max(10) Integer nota,
        @NotNull Urgencia urgencia
) {
}