package br.com.juno.integration.api.confrateres.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@EqualsAndHashCode
@ToString
public final class JunoConfig implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Getter @Setter private static Long sqJunoConfiguracao;
	@Getter @Setter private static String dsAmbiente;
	@Getter @Setter private static String dsClientSecret; 
	@Getter @Setter private static String dsClientId;
	@Getter @Setter private static String dsAccessToken;
	@Getter @Setter private static String dsResourceToken;
	@Getter @Setter private static String dsPublicToken;
	@Getter @Setter private static Long nnHoraMillessegundosAtualizacaoToken;
	@Getter @Setter private static Long nnTokenTimeout;
	@Getter @Setter private static String dsAuthorizationServer; // = "https://sandbox.boletobancario.com/authorization-server";
	@Getter @Setter private static String dsResouceServer; // = "https://sandbox.boletobancario.com/api-integration";
	@Getter @Setter private static Long dsCacheTimeout;
	@Getter @Setter private static BigDecimal vlParcelaMinimo;
	@Getter @Setter private static int qtdeMaximaParcelasBoleto;
	@Getter @Setter private static int qtdeMaximaParcelasCartao;
	
	@Getter @Setter static boolean flAccessTokenValido;  // Se true = Válido, Se False = Precisa atualizar
		

}
