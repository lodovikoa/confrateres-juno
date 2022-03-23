package br.com.juno.integration.api.confrateres.model;

import java.io.Serializable;
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
@Table(name = "tb_aga_ago")
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class Ago implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "aga_sq_ago")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter Long sqAgo;
	
	@Column(name = "aga_cd_ago")
	@Getter @Setter private String cdAgo;
	
	@Column(name = "aga_ds_descricao")
	@Getter @Setter private String dsDescricao;
	
	@Column(name = "aga_dt_ago")
	@Getter @Setter private LocalDate dtAgo;
	
	@Column(name = "aga_fl_ativo")
	@Getter @Setter private boolean flAtivo;
	
	@Column(name = "aga_ds_endereco_evento")
	@Getter @Setter private String dsEnderecoEvento;
	
	@Column(name = "aga_ds_endereco_cpl")
	@Getter @Setter private String dsEnderecoCpl;
	
	@Column(name = "aga_ds_bairro_evento")
	@Getter @Setter private String dsBairroEvento;
	
	@Column(name = "aga_ds_cidade_evento")
	@Getter @Setter private String dsCidadeEvento;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "est_sq_estado", nullable = false)
	@Getter @Setter private Estado estado;
	
	@Column(name = "aga_ds_cep_evento")
	@Getter @Setter private String dsCepEvento;
	
	@Column(name = "aga_ds_telefones_evento")
	@Getter @Setter private String dsTelefonesEvento;
	
	@Column(name = "aga_ds_whatsapp_evento")
	@Getter @Setter private String dsWhatsappEvento;
	
	@Column(name = "aga_ds_email_evento")
	@Getter @Setter private String dsEmailEvento;
	
	@Column(name = "aga_ds_observacoes")
	@Getter @Setter private String dsObservacoes;
	
	@Column(name = "auditoria_data")
	@Getter @Setter private LocalDateTime auditoriaData;
	
	@Column(name = "auditoria_usuario")
	@Getter @Setter private String auditoriaUsuario;

}
