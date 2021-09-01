package br.com.juno.integration.api.confrateres.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class JunoParcelamentoTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter @Setter int qtdeParcelas;
	@Getter @Setter private BigDecimal vlParcela;
	@Getter @Setter private boolean flSelecionada;

}
