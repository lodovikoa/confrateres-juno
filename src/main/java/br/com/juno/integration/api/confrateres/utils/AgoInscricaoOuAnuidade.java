package br.com.juno.integration.api.confrateres.utils;

import lombok.Getter;

public enum AgoInscricaoOuAnuidade {
	INSCRICAO("Inscrição"),
	ANUIDADE("Anuidade");
	
	@Getter private String dsDescricao;

	private AgoInscricaoOuAnuidade(String dsDescricao) {
		this.dsDescricao = dsDescricao;
	}
	
	
}
