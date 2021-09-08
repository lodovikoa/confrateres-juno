package br.com.juno.integration.api.confrateres.model;

import java.io.Serializable;

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
@Table(name = "tb_est_estado")
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class Estado implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "est_sq_estado")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter private Long sqEstado;
	
	@Column(name = "est_ds_uf")
	@Getter @Setter private String dsUf;
	
	@Column(name = "est_ds_nome")
	@Getter @Setter private String dsNome;

}
