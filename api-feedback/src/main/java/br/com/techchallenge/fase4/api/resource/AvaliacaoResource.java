package br.com.techchallenge.fase4.api.resource;

import br.com.techchallenge.fase4.api.dto.AvaliacaoRequest;
import br.com.techchallenge.fase4.api.dto.AvaliacaoResponse;
import br.com.techchallenge.fase4.api.service.AvaliacaoService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/avaliacao")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AvaliacaoResource {

    @Inject
    AvaliacaoService avaliacaoService;

    @POST
    public Response criar(@Valid AvaliacaoRequest request) {
        AvaliacaoResponse response = avaliacaoService.registrar(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
}