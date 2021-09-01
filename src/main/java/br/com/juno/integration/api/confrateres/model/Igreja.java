package br.com.juno.integration.api.confrateres.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "tb_igr_igreja")
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class Igreja implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "igr_sq_igreja")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter private Long sqIgreja;
	
	@Size(max = 100, message = "tamanho máximo de 100 caracteres")
	@Column(name = "igr_ds_igreja", nullable=false)
	@Getter @Setter private String dsIgreja;

}
