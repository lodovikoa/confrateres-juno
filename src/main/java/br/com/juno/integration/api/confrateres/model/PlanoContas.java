package br.com.juno.integration.api.confrateres.model;

import java.io.Serializable;
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
@Table(name = "tb_plc_plano_contas")
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class PlanoContas implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "plc_sq_plano_contas")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter private Long sqPlanoContas; 
	
	@Column(name = "plc_cd_conta")
	@Getter @Setter private Integer cdConta; 
	
	@Column(name = "plc_ds_conta")
	@Getter @Setter private String dsConta; 
	
	@Column(name = "plc_tp_conta")
	@Getter @Setter private String tpConta; 
	
	@Column(name = "auditoria_data")
	@Getter @Setter private LocalDateTime auditoriaData; 
	
	@Column(name = "auditoria_usuario")
	@Getter @Setter private String auditoriaUsuario; 

}
