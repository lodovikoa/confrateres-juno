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
@Table(name = "tb_rgl_reglancamento")
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class RegLancamento implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "rgl_sq_reglancamento")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter private Long sqRegLancamento;
	
	@Column(name = "rgl_cd_origem")
	@Getter @Setter private Integer cdOrigem; 
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "min_sq_ministro", nullable = false)
	@Getter @Setter private Ministro ministro;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tpl_sq_tipolancamento", nullable = false)
	@Getter @Setter private TipoLancamento tipoLancamento;
	
	@Column(name = "rgl_dt_registro")
	@Getter @Setter private LocalDateTime dtRegistro; 
	
	@Column(name = "rgl_dt_vencimento")
	@Getter @Setter private LocalDate dtVencimento;  
	
	@Column(name = "rgl_vl_lancamento")
	@Getter @Setter private BigDecimal vlLancamento; 
	
	@Column(name = "rgl_dt_cancelado")
	@Getter @Setter private LocalDateTime dtCancelado; 
	
	@Column(name = "auditoria_data")
	@Getter @Setter private LocalDateTime auditoriaData;
	
	@Column(name = "auditoria_usuario")
	@Getter @Setter private String auditoriaUsuario;
	
}
