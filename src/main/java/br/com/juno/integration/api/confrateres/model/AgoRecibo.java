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
@Table(name = "tb_agr_ago_recibo")
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class AgoRecibo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "agr_sq_recibo")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter private Long sqRecibo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "aga_sq_ago", nullable = false)
	@Getter @Setter private Ago ago;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "min_sq_ministro", nullable = false)
	@Getter @Setter private Ministro ministro;

	@Column(name = "agr_dt_recibo")
	@Getter @Setter private LocalDate dtRecibo;
	
	@Column(name = "agr_vl_recibo")
	@Getter @Setter private BigDecimal vlRecibo;
	
	@Column(name = "agr_ds_forma_pagamento")
	@Getter @Setter private String dsFormaPagamento;
	
	@Column(name = "agr_ds_url")
	@Getter @Setter private String dsUrl;
	
	@Column(name = "agr_dt_validado")
	@Getter @Setter private LocalDate dtValidado;
	
	@Column(name = "agr_dt_cancelado")
	@Getter @Setter private LocalDate dtCancelado;
	
	@Column(name = "agr_ds_obs")
	@Getter @Setter private String dsObs;
	
	@Column(name = "auditoria_data")
	@Getter @Setter private LocalDateTime auditoriaData;
	
	@Column(name = "auditoria_usuario")
	@Getter @Setter private String auditoriaUsuario;

}
