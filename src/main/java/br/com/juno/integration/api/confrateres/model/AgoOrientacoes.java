package br.com.juno.integration.api.confrateres.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class AgoOrientacoes implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Getter @Setter private String dsTipoPagamento;
	@Getter @Setter private String dsOrientacoes;

}
