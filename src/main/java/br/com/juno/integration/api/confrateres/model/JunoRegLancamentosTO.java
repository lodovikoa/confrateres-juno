package br.com.juno.integration.api.confrateres.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class JunoRegLancamentosTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter @Setter Long sqRegLancamento;
	@Getter @Setter private LocalDate dtVencimento; 
	@Getter @Setter private BigDecimal vlLancamento; 
	@Getter @Setter private BigDecimal vlSaldo;   
	@Getter @Setter private String dsLancamentoTipo;
	@Getter @Setter private String dsUrl;
	@Getter @Setter boolean flLancamentoSelecionado;

}
