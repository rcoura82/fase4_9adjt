package br.com.techchallenge.fase4.api.service;

import br.com.techchallenge.fase4.api.dto.AvaliacaoRequest;
import br.com.techchallenge.fase4.api.dto.AvaliacaoResponse;

public interface AvaliacaoService {

    AvaliacaoResponse registrar(AvaliacaoRequest request);
}