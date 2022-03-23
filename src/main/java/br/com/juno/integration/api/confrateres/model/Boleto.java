package br.com.juno.integration.api.confrateres.model;

import java.io.Serializable;

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
@Table(name = "tb_bol_boleto")
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class Boleto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "bol_sq_boleto")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter private Long sqBoleto;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "agr_sq_recibo")
	@Getter @Setter private AgoRecibo agoRecibo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rgl_sq_reglancamento")
	@Getter @Setter private RegLancamento regLancamento;
	
	
	
	@Column(name = "bol_ds_url")
	@Getter @Setter private String dsUrl;
	
}
