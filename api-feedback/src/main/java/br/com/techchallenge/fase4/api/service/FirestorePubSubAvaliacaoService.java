package br.com.techchallenge.fase4.api.service;

import br.com.techchallenge.fase4.api.dto.AvaliacaoRequest;
import br.com.techchallenge.fase4.api.dto.AvaliacaoResponse;
import br.com.techchallenge.fase4.api.dto.FeedbackCriticoEvent;
import br.com.techchallenge.fase4.api.model.Urgencia;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@ApplicationScoped
public class FirestorePubSubAvaliacaoService implements AvaliacaoService {

    private static final Logger LOGGER = Logger.getLogger(FirestorePubSubAvaliacaoService.class.getName());
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Inject
    GcpConfig gcpConfig;

    private Firestore firestore;
    private Publisher criticalPublisher;

    @PostConstruct
    void init() throws Exception {
        String projectId = resolveProjectId();

        FirestoreOptions.Builder firestoreBuilder = FirestoreOptions.getDefaultInstance().toBuilder();
        firestoreBuilder.setProjectId(projectId);
        firestore = firestoreBuilder.build().getService();

        ProjectTopicName topicName = ProjectTopicName.of(projectId, gcpConfig.pubsub().topicCritico());
        criticalPublisher = Publisher.newBuilder(topicName).build();

        LOGGER.info(() -> "Integracao GCP inicializada. projectId=" + projectId
                + ", collection=" + gcpConfig.firestore().collection()
                + ", topic=" + gcpConfig.pubsub().topicCritico());
    }

    @Override
    public AvaliacaoResponse registrar(AvaliacaoRequest request) {
        try {
            String id = UUID.randomUUID().toString();
            Instant dataEnvio = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            boolean critica = request.urgencia() == Urgencia.ALTA;

            Map<String, Object> doc = new LinkedHashMap<>();
            doc.put("id", id);
            doc.put("descricao", request.descricao());
            doc.put("nota", request.nota());
            doc.put("urgencia", request.urgencia().name());
            doc.put("dataEnvio", dataEnvio.toString());

            firestore.collection(gcpConfig.firestore().collection()).document(id).set(doc).get();

            if (critica) {
                publishCriticalEvent(new FeedbackCriticoEvent(
                        id,
                        request.descricao(),
                        request.urgencia().name(),
                        dataEnvio.toString()
                ));
            }

            return new AvaliacaoResponse(id, dataEnvio.toString(), "RECEBIDA", critica);
        } catch (Exception exception) {
            throw new IllegalStateException("Falha ao registrar avaliacao no Firestore/PubSub.", exception);
        }
    }

    private void publishCriticalEvent(FeedbackCriticoEvent event) throws Exception {
        String json = OBJECT_MAPPER.writeValueAsString(event);
        PubsubMessage message = PubsubMessage.newBuilder()
                .setData(ByteString.copyFromUtf8(json))
                .putAttributes("urgencia", event.urgencia())
                .putAttributes("avaliacaoId", event.id())
                .build();

        criticalPublisher.publish(message).get();
        LOGGER.info(() -> "Evento critico publicado no Pub/Sub para avaliacaoId=" + event.id());
    }

    private String resolveProjectId() {
        String configured = gcpConfig.projectId().orElse("").trim();
        if (!configured.isBlank()) {
            return configured;
        }

        String envProject = Optional.ofNullable(System.getenv("GOOGLE_CLOUD_PROJECT")).orElse("").trim();
        if (!envProject.isBlank()) {
            return envProject;
        }

        throw new IllegalStateException("Defina gcp.project-id ou a variavel GOOGLE_CLOUD_PROJECT.");
    }

    @PreDestroy
    void shutdown() {
        if (criticalPublisher != null) {
            criticalPublisher.shutdown();
            try {
                criticalPublisher.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
        }

        if (firestore != null) {
            try {
                firestore.close();
            } catch (Exception exception) {
                LOGGER.warning("Falha ao encerrar cliente Firestore: " + exception.getMessage());
            }
        }
    }

    @ConfigMapping(prefix = "gcp")
    public interface GcpConfig {
        Optional<String> projectId();

        FirestoreConfig firestore();

        PubSubConfig pubsub();
    }

    public interface FirestoreConfig {
        @WithDefault("avaliacoes")
        String collection();
    }

    public interface PubSubConfig {
        @WithDefault("feedback-critico")
        String topicCritico();
    }
}