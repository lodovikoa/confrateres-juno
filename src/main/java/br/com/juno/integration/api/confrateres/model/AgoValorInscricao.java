package br.com.juno.integration.api.confrateres.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "tb_agi_ago_valor_inscricao")
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class AgoValorInscricao implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "agi_sq_valor_inscricao")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter private Long sqValorInscricao;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "aga_sq_ago", nullable = false)
	@Getter @Setter private Ago ago;
	
	@Column(name = "agi_cd_ordem")
	@Getter @Setter private Integer cdOrdem;
	
	@Column(name = "agi_cd_tipo")
	@Getter @Setter private String cdTipo;
	
	@Column(name = "agi_vl_valor_inscricao")
	@Getter @Setter private BigDecimal vlValorInscricao;
	
	@Column(name = "agi_dt_periodo_inicio")
	@Getter @Setter private LocalDate dtPeriodoInicio;
	
	@Column(name = "agi_dt_periodo_fim")
	@Getter @Setter private LocalDate dtPeriodoFim;
	
	@Column(name = "auditoria_data")
	@Getter @Setter private LocalDateTime auditoriaData;
	
	@Column(name = "auditoria_usuario")
	@Getter @Setter private String auditoriaUsuario;

}
