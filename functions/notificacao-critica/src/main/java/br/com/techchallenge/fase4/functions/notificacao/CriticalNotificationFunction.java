package br.com.techchallenge.fase4.functions.notificacao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

public class CriticalNotificationFunction implements BackgroundFunction<PubSubMessage> {

    private static final Logger LOGGER = Logger.getLogger(CriticalNotificationFunction.class.getName());
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void accept(PubSubMessage message, Context context) throws Exception {
        if (message == null || message.getData() == null) {
            LOGGER.warning("Evento recebido sem payload para notificação crítica.");
            return;
        }

        String json = new String(Base64.getDecoder().decode(message.getData()), StandardCharsets.UTF_8);
        FeedbackCriticoEvent event = OBJECT_MAPPER.readValue(json, FeedbackCriticoEvent.class);

        LOGGER.info(() -> "Notificacao critica processada: id='" + event.getId()
            + "', descricao='" + event.getDescricao()
                + "', urgencia='" + event.getUrgencia()
                + "', dataEnvio='" + event.getDataEnvio() + "'.");
    }
}