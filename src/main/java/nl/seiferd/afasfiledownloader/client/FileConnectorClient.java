package nl.seiferd.afasfiledownloader.client;

import nl.seiferd.afasfiledownloader.model.AfasFile;
import nl.seiferd.afasfiledownloader.model.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

public class FileConnectorClient implements ConnectorClient {

    private static final Logger LOG = LoggerFactory.getLogger(FileConnectorClient.class);

    private static final String CONNECTOR_URL = "https://%s.afasonlineconnector.nl/profitrestservices/fileconnector/%s/%s";

    private final String abboId;
    private final String token;

    public FileConnectorClient(final String abboId, final String token) {
        this.abboId = abboId;
        this.token = token;
    }

    public Mono<AfasFile> downloadFile(final Row row) {
        LOG.info("Trying to get file {} for dossier {}", row.getBijlagenaam(), row.getDossieritem());

        return toWebClient(row.getBijlageID(), row.getBijlagenaam())
                .get()
                .header(AUTHORIZATION, authorizationHeader(token))
                .retrieve()
                .bodyToMono(AfasFile.class)
                .map(result -> result.withDossieritem(row.getDossieritem()))
                .onErrorResume(error -> {
                    LOG.error("Error downloading file for {}", row.getDossieritem());
                    return Mono.just(new AfasFile().withDossieritem(row.getDossieritem()).withFilename(row.getBijlagenaam()));
                });
    }

    private WebClient toWebClient(final String bijlageID, final String bijlageNaam) {
        return WebClient.builder()
                .baseUrl(buildUrl(bijlageID, bijlageNaam))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private String buildUrl(final String fileId, final String fileName) {
        final String sanitizedFileName = fileName.replaceAll("_", "_5f");
        final String encodedFileName = UriUtils.encode(sanitizedFileName, StandardCharsets.UTF_8.toString());

        return String.format(CONNECTOR_URL, abboId, fileId, encodedFileName);
    }

}
