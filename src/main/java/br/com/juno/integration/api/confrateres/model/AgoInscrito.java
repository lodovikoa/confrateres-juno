package br.com.juno.integration.api.confrateres.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tb_agt_ago_inscrito")
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class AgoInscrito implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "agt_sq_inscrito")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter private Long sqInscrito;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "agr_sq_recibo", nullable = false)
	@Getter @Setter private AgoRecibo agoRecibo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "min_sq_ministro", nullable = false)
	@Getter @Setter private Ministro ministro;
	
	@Column(name = "agt_nm_outros_inscrito")
	@Getter @Setter private String nmOutrosInscrito;
	
	@Column(name = "agt_vl_inscricao")
	@Getter @Setter private BigDecimal vlInscricao;
	
	@Column(name = "agt_ds_insc_anuidade")
	@Getter @Setter private String dsInscAnuidade;
	
	@Column(name = "agt_ds_descricao_lancamento")
	@Getter @Setter private String dsDescricaoLancamento;
	
	@Column(name = "agt_vl_valor_lancamento")
	@Getter @Setter private BigDecimal vlValorLancamento;
	
	@Column(name = "agt_vl_valor_saldo_devedor")
	@Getter @Setter private BigDecimal vlValorSaldoDevedor;
	
	@Column(name = "auditoria_data")
	@Getter @Setter private LocalDateTime auditoriaData;
	
	@Column(name = "auditoria_usuario")
	@Getter @Setter private String auditoriaUsuario;

}