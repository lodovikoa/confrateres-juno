package br.com.juno.integration.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.juno.integration.api.base.exception.Assert;
import lombok.Getter;
import lombok.Setter;

public class AuthorizationToken implements Serializable {

    private static final long serialVersionUID = -8061124453931068922L;

    // Para testes, após token vencido, precisa atualizar os dois campos abaixo.
    private String accessToken; // = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJsb2Rvdmlrb0BnbWFpbC5jb20iLCJzY29wZSI6WyJhbGwiXSwiZXhwIjoxNjI2NDM1ODAzLCJqdGkiOiItNXVpYTRDTzhWellqUEdicmZZd3N6bi1Fd00iLCJjbGllbnRfaWQiOiJnQ0lwVWpHOThXckd4Z293In0.jAJspG_VQIbolhwqSm51FIK6vyFTya6l4FxKFvryhT1ACUuir26QIDtcrJSI-tO7g1nReKGfh86KKndXUQn-RvkXlGziXmmk2TiPaNwfglB5bgVfBlg-2Z2uPm97YhHLTtB8J_DGWShwc6wqU1Ona5uuwadjg2FyAU2w2q4zvVpDydmE3F7LKZNSftFYd2liZNJDhK2X7h3z1YuhD745EeVlpRzgtUO-UhMEuWC0mhSKVyyUmcP_VY6-bNcW2hKxMJyjjhnGqp6DuHiiS1BWrFTVVQO74x6NxlomJNEpzmP_gd2D5EVt4iVI96TjKy0nhBhQ74KroIImaYKIwHQuyg";
    @Getter @Setter private Long horaMillessegundosAtualizacaoToken; // = 1626349404615L;
    //// Para testes, após token vencido, precisa atualizar os dois campos acima.
    
    
    private String tokenType;
    private Long expiresIn = 86399L;
    private String scope;
    private String userName;
    private String jti;
    

    public boolean isExpired() {
        return isExpired(0L, 0L);
    }

    public boolean isExpired(Long minTimeAmount, Long horaMilessegundosAtual) {
        Assert.notNull(expiresIn, "expiredIn was not initialized");
        
       boolean retorno = horaMilessegundosAtual - horaMillessegundosAtualizacaoToken > minTimeAmount;
       return retorno;

    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public Long getExpiresInMillis() {
        return expiresIn == null ? null : expiresIn * 1000;
    }

    public String getScope() {
        return scope;
    }

    public String getUserName() {
        return userName;
    }

    public String getJti() {
        return jti;
    }

    @JsonProperty("access_token")
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonProperty("token_type")
    void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    @JsonProperty("expires_in")
    void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @JsonProperty("scope")
    void setScope(String scope) {
        this.scope = scope;
    }

    @JsonProperty("user_name")
    void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonProperty("jti")
    void setJti(String jti) {
        this.jti = jti;
    }
}