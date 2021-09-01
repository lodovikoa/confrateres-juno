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
@Table(name = "tb_tpl_tipolancamento")
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class TipoLancamento implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "tpl_sq_tipolancamento")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter private Long sqTipoLancamento; 
	
	@Column(name = "tpl_ds_tipolancamento")
	@Getter @Setter private String dsTipoLancamento; 
	
	@Column(name = "tpl_vl_tipolancamento")
	@Getter @Setter private BigDecimal vlTipoLancamento;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plc_sq_plano_contas", nullable = false)
	@Getter @Setter private PlanoContas planoContas; 
	
	@Column(name = "auditoria_data")
	@Getter @Setter private LocalDateTime auditoriaData;
	
	@Column(name = "auditoria_usuario")
	@Getter @Setter private String auditoriaUsuario; 

}
