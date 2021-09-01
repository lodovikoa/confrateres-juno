package br.com.juno.integration.api.confrateres.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "tb_cgo_cargo")
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class Cargo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "cgo_sq_cargo")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter private Long sqCargo;
	
	@NotBlank
	@Size(max = 50, message = "tamanho máximo de 50 caracteres")
	@Column(name = "cgo_ds_cargo", nullable=false, length = 50)
	@Getter @Setter private String dsCargo;
	
	@Size(max = 5, message = "Tamanho máximo de 5 caracteres")
	@Column(name = "cgo_ds_titulo", nullable = false, length = 5)
	@Getter @Setter  private String dsTitulo;
	
}
