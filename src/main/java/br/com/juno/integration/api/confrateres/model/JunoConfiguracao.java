package br.com.juno.integration.api.confrateres.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "tb_jco_juno_configuracao")
public class JunoConfiguracao implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "jco_sq_juno_configuracao")
	@Getter @Setter private Long sqJunoConfiguracao;
	
	@Column(name = "jco_ds_ambiente")
	@Getter @Setter private String dsAmbiente;
	
	@Column(name = "jco_ds_client_secret")
	@Getter @Setter private String dsClientSecret; 
	
	@Column(name = "jco_ds_client_id")
	@Getter @Setter private String dsClientId;
	
	@Column(name = "jco_ds_access_token")
	@Getter @Setter private String dsAccessToken;
	
	@Column(name = "jco_ds_resource_token")
	@Getter @Setter private String dsResourceToken;
	
	@Column(name = "jco_ds_public_token")
	@Getter @Setter private String dsPublicToken;
	
	@Column(name = "jco_nn_mileseg_atualiza_token")
	@Getter @Setter private Long nnHoraMillessegundosAtualizacaoToken;
	
	@Column(name = "jco_nn_token_timeout")
	@Getter @Setter private Long nnTokenTimeout;
	
	@Column(name = "jco_ds_autorization_server")
	@Getter @Setter private String dsAuthorizationServer; // = "https://sandbox.boletobancario.com/authorization-server";
	
	@Column(name = "jco_ds_resource_server")
	@Getter @Setter private String dsResouceServer; // = "https://sandbox.boletobancario.com/api-integration";
	
	@Column(name = "jco_ds_cache_timeout")
	@Getter @Setter private Long dsCacheTimeout;
	
	@Column(name = "jco_vl_parcela_minimo")
	@Getter @Setter private BigDecimal vlParcelaMinimo;
	
	@Column(name = "jco_nn_qtde_maxima_parcelas")
	@Getter @Setter private int qtdeMaximaParcelas;

	@Column(name="auditoria_data")
	@Getter @Setter private LocalDateTime auditoriaData;
	
	@Column(name="auditoria_usuario", length=50)
	@Getter @Setter private String auditoriaUsuario;
}
