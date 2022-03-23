package br.com.juno.integration.api.confrateres.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.juno.integration.api.confrateres.utils.AgoInscricaoOuAnuidade;
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
	
	@Getter @Setter private Long sqRegLancamento;
	@Getter @Setter private Long sqMinistro;
	@Getter @Setter private String nmEsposaOutros;
	@Getter @Setter private LocalDate dtVencimento; 
	@Getter @Setter private BigDecimal vlLancamento; 
	@Getter @Setter private BigDecimal vlSaldo;   
	@Getter @Setter private String dsLancamentoTipo;
	@Getter @Setter private String dsUrl;
	@Getter @Setter boolean flLancamentoSelecionado;
	@Getter @Setter AgoInscricaoOuAnuidade agoInscricaoOuAnuidade;  // Identificar se é ANUIDADE em atraso ou INSCRICAO. Para uso somente para INSCRICAO de AGO
	@Getter @Setter String dsFormaPagamento;
	@Getter @Setter String dsCor = "color: #000000;";
}
