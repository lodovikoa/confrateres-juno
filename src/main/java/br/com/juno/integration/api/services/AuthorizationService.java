package br.com.juno.integration.api.services;

import static br.com.juno.integration.api.services.JunoApiManager.CONTENT_TYPE_HEADER;

import java.util.HashMap;
import java.util.Map;

import br.com.juno.integration.api.base.Clock;
import br.com.juno.integration.api.confrateres.model.JunoConfig;
import br.com.juno.integration.api.model.AuthorizationToken;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public final class AuthorizationService extends BaseService {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER = "Bearer ";

    public static final String OAUTH_TOKEN_ENDPOINT = "/oauth/token";

    private AuthorizationToken authorizationToken;

    public synchronized String getToken() {   
    	authorizationToken = new AuthorizationToken();
    	authorizationToken.setAccessToken(JunoConfig.getDsAccessToken());
    	authorizationToken.setHoraMillessegundosAtualizacaoToken(JunoConfig.getNnHoraMillessegundosAtualizacaoToken());
    	
    	
    	Long horaMilessegundosAtual = Clock.getTimeInMillis();
    	
        if (authorizationToken.getAccessToken().isEmpty() || authorizationToken.isExpired(JunoApiConfig.cache_timeout, horaMilessegundosAtual)) {
            refresh();
            
            // Guardar a hora em milissegundos no momento que obteve o novo Token de Autorizacao            
            JunoConfig.setDsAccessToken(authorizationToken.getAccessToken());
            JunoConfig.setNnHoraMillessegundosAtualizacaoToken(horaMilessegundosAtual);
            JunoConfig.setFlAccessTokenValido(false);
            
        } else {
        	// AccessToken VÁLIDO
        	JunoConfig.setFlAccessTokenValido(true);
        }

        return authorizationToken.getAccessToken();
    }

    public Map<String, String> getAuthorizationHeader() {
        Map<String, String> authorizationHeader = new HashMap<>();
        authorizationHeader.put(AUTHORIZATION_HEADER, BEARER + getToken());
        return authorizationHeader;
    }

    private void refresh() {
        HttpResponse<AuthorizationToken> response = //
                Unirest.post(JunoApiManager.config().getAuthorizationEndpoint() + OAUTH_TOKEN_ENDPOINT) //
                        .basicAuth(JunoApiManager.config().getClientId(), JunoApiManager.config().getClientSecret()) //
                        .header(CONTENT_TYPE_HEADER, "application/x-www-form-urlencoded") //
                        .field("grant_type", "client_credentials") //
                        .asObject(AuthorizationToken.class);
        
        System.out.println("Novo TOKEN: " + response.getBody().getAccessToken());

        authorizationToken = response.getBody();
    }
}