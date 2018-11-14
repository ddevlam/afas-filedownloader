package nl.seiferd.afasfiledownloader.client;

/**
 * Interface behaves like a 'trait'
 */
public interface ConnectorClient {

    String AFAS_TOKEN = "AfasToken";
    String AUTHORIZATION = "Authorization";

    default String authorizationHeader(final String token){
        return String.format("%s %s", AFAS_TOKEN, token);
    }
}
