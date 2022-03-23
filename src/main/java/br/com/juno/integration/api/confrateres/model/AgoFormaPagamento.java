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

@Entity
@Table(name = "tb_agp_ago_forma_pagamento")
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class AgoFormaPagamento implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "agp_sq_forma_pagamento")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter private Long sqFormaPagamento;
	
	@Column(name = "agp_cd_ordem")
	@Getter @Setter private int cdOrdem;
	
	@Column(name = "agp_ds_forma_pagamento")
	@Getter @Setter private String dsFormaPagamento;
	
	@Column(name = "agp_ds_forma_pagamento_reduzido")
	@Getter @Setter private String dsFormaPagamentoReduzido;
	
	@Column(name = "agp_ds_descricao")
	@Getter @Setter private String dsDescricao;
	
	@Column(name = "agp_vl_parcela_minimo")
	@Getter @Setter private BigDecimal vlParcelaMinimo;
	
	@Column(name = "agp_nn_qtde_parcelas")
	@Getter @Setter private int nnQtdeParcelas;
	
	@Column(name = "auditoria_data")
	@Getter @Setter private LocalDateTime auditoriaData;
	
	@Column(name = "auditoria_usuario")
	@Getter @Setter private String auditoriaUsuario;
	
}
