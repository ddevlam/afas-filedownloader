package nl.seiferd.afasfiledownloader.client;

import nl.seiferd.afasfiledownloader.model.Batch;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public class CustomConnectorClient implements ConnectorClient {

    private static final String CONNECTOR_URL = "https://%s.afasonlineconnector.nl/profitrestservices/connectors/%s?skip=%d&take=%d";

    private final String abboId;
    private final String token;
    private final String connectorName;

    public CustomConnectorClient(final String abboId, final String token, final String connectorName) {
        this.abboId = abboId;
        this.token = token;
        this.connectorName = connectorName;
    }

    public Batch getBatch(final long skip, final long itemsToTake){
        return toWebClient(skip, itemsToTake).get()
                .header(AUTHORIZATION, authorizationHeader(token))
                .retrieve()
                .bodyToMono(Batch.class)
                .block();
    }

    private WebClient toWebClient(final long skip, final long itemsToTake) {
        return WebClient.builder()
                .baseUrl(buildUrl(skip, itemsToTake))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private String buildUrl(final long skip, final long itemsToTake) {
        return String.format(CONNECTOR_URL, abboId, connectorName, skip, itemsToTake);
    }
}
