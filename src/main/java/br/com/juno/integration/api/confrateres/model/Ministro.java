package br.com.juno.integration.api.confrateres.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tb_min_ministro")
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class Ministro implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "min_sq_ministro")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter private Long sqMinistro;

	@Column(name = "min_cd_codigo")
	@Getter @Setter private Long cdCodigo;

	@Column(name = "min_nm_nome", nullable = false)
	@Getter @Setter private String nmNome;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cgo_sq_cargo", nullable = false)
	@Getter @Setter private Cargo cargo; 

	@Column(name = "min_dt_nascimento")
	@Getter @Setter private LocalDate dtNascimento; 

	@Column(name = "min_ds_cpf")
	@Getter @Setter private String dsCpf; 

	@Column(name = "min_ds_email")
	@Getter @Setter private String dsEmail;
	
	@Column(name = "min_ds_email2")
	@Getter @Setter private String dsSegundoEmail;
	
	@Transient
	@Getter @Setter private boolean flAtualizaSegundoEmail;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "igr_sq_igreja", nullable = false)
	@Getter @Setter private Igreja igreja; 

	@Column(name = "min_ds_foto")
	@Getter @Setter private String dsFoto;
	
	@Column(name = "min_ds_cep")
	@Getter @Setter private String dsCep;
	
	@Column(name = "min_ds_endereco")
	@Getter @Setter private String dsEndereco;
	
	@Column(name = "min_ds_bairro")
	@Getter @Setter private String dsBairro;
	
	@Column(name = "min_ds_cidade")
	@Getter @Setter private String dsCidade;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "est_sq_estado", nullable = false)
	@Getter @Setter private Estado estado;
	
	@Column(name = "auditoria_usuario")
	@Getter @Setter private String auditoriaUsuario;

}
