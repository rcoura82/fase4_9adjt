package br.com.techchallenge.fase4.functions.relatorio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class WeeklyReportFunction implements HttpFunction {

    private static final Logger LOGGER = Logger.getLogger(WeeklyReportFunction.class.getName());
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String DEFAULT_COLLECTION = "avaliacoes";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        LOGGER.info("Geracao de relatorio semanal iniciada.");

        String projectId = resolveProjectId();
        String collection = resolveCollection();
        Instant agora = Instant.now();
        Instant inicioJanela = agora.minus(7, ChronoUnit.DAYS);

        Firestore firestore = FirestoreOptions.getDefaultInstance().toBuilder()
                .setProjectId(projectId)
                .build()
                .getService();

        List<QueryDocumentSnapshot> docs;
        try {
            docs = firestore.collection(collection)
                    .whereGreaterThanOrEqualTo("dataEnvio", inicioJanela.toString())
                    .get()
                    .get()
                    .getDocuments();
        } finally {
            firestore.close();
        }

        List<Map<String, Object>> itens = new ArrayList<>();
        Map<String, Integer> porDia = new LinkedHashMap<>();
        Map<String, Integer> porUrgencia = new LinkedHashMap<>();
        porUrgencia.put("BAIXA", 0);
        porUrgencia.put("MEDIA", 0);
        porUrgencia.put("ALTA", 0);

        int somaNotas = 0;
        int totalAvaliacoes = 0;

        for (DocumentSnapshot doc : docs) {
            String descricao = safeString(doc.getString("descricao"));
            String urgencia = safeUrgencia(doc.getString("urgencia"));
            String dataEnvio = safeString(doc.getString("dataEnvio"));
            Integer nota = doc.getLong("nota") != null ? doc.getLong("nota").intValue() : 0;

            Map<String, Object> item = new HashMap<>();
            item.put("descricao", descricao);
            item.put("urgencia", urgencia);
            item.put("dataEnvio", dataEnvio);
            itens.add(item);

            totalAvaliacoes++;
            somaNotas += nota;

            porUrgencia.put(urgencia, porUrgencia.getOrDefault(urgencia, 0) + 1);

            String dia = toDateKey(dataEnvio);
            porDia.put(dia, porDia.getOrDefault(dia, 0) + 1);
        }

        itens.sort(Comparator.comparing(map -> String.valueOf(map.get("dataEnvio"))));

        double media = totalAvaliacoes == 0 ? 0.0 : (double) somaNotas / totalAvaliacoes;

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("status", "RELATORIO_GERADO");
        payload.put("periodoInicio", inicioJanela.toString());
        payload.put("periodoFim", agora.toString());
        payload.put("mediaAvaliacoes", Math.round(media * 100.0) / 100.0);
        payload.put("quantidadeAvaliacoesPorDia", porDia);
        payload.put("quantidadeAvaliacoesPorUrgencia", porUrgencia);
        payload.put("itens", itens);

        LOGGER.info("Relatorio semanal concluido. totalAvaliacoes=" + totalAvaliacoes);

        response.setStatusCode(200);
        response.appendHeader("Content-Type", "application/json");
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(payload));
    }

    private String resolveProjectId() {
        String envProject = Optional.ofNullable(System.getenv("GOOGLE_CLOUD_PROJECT")).orElse("").trim();
        if (!envProject.isBlank()) {
            return envProject;
        }

        throw new IllegalStateException("Defina a variavel GOOGLE_CLOUD_PROJECT para gerar relatorio semanal.");
    }

    private String resolveCollection() {
        String envCollection = Optional.ofNullable(System.getenv("FIRESTORE_COLLECTION")).orElse("").trim();
        return envCollection.isBlank() ? DEFAULT_COLLECTION : envCollection;
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private String safeUrgencia(String urgencia) {
        if (urgencia == null || urgencia.isBlank()) {
            return "BAIXA";
        }
        return urgencia;
    }

    private String toDateKey(String isoDate) {
        if (isoDate == null || isoDate.isBlank()) {
            return LocalDate.now(ZoneOffset.UTC).toString();
        }

        try {
            return Instant.parse(isoDate).atZone(ZoneOffset.UTC).toLocalDate().toString();
        } catch (Exception ignored) {
            return LocalDate.now(ZoneOffset.UTC).toString();
        }
    }
}