package br.com.juno.integration.api.confrateres.model;

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
@Table(name = "tb_con_convencao")
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class Convencao {
	
	@Id
	@Column(name = "con_sq_convencao")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter private Long sqConvencao;
	
	@Column(name = "con_ds_reduzido")
	@Getter @Setter private String dsReduzido;
	
	@Column(name = "con_ds_convencao")
	@Getter @Setter private String dsConvencao;
	
	@Column(name = "con_ds_endereco")
	@Getter @Setter private String dsEndereco;

	@Column(name = "con_ds_bairro")
	@Getter @Setter private String dsBairro;
	
	@Column(name = "con_ds_cidade")
	@Getter @Setter private String dsCidade;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "est_sq_estado")
	@Getter @Setter private Estado estado;
	
	@Column(name = "con_ds_pais")
	@Getter @Setter private String dsPais;
	
	@Column(name = "con_ds_cep")
	@Getter @Setter private String dsCep;
	
	@Column(name = "con_ds_cnpj")
	@Getter @Setter private String dsCnpj;
	
	@Column(name = "con_ds_email")
	@Getter @Setter private String dsEmail;
	
	@Column(name = "con_im_logo")
	@Getter @Setter private String imLogo;
	
	@Column(name = "con_ds_telefones")
	@Getter @Setter private String dsTelefones;
	
	@Column(name = "con_ds_watsapp")
	@Getter @Setter private String dsWatsapp;
	
	@Column(name = "auditoria_data")
	@Getter @Setter private LocalDateTime auditoriaData;
	
	@Column(name = "auditoria_usuario")
	@Getter @Setter private String auditoriaUsuario;
}