package br.com.juno.integration.api.confrateres.bean;


import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.juno.integration.api.confrateres.bo.JunoPagamentosBO;
import br.com.juno.integration.api.confrateres.exception.NegocioException;
import br.com.juno.integration.api.confrateres.model.JunoConfig;
import br.com.juno.integration.api.confrateres.model.JunoParcelamentoTO;
import br.com.juno.integration.api.confrateres.model.JunoRegLancamentosTO;
import br.com.juno.integration.api.confrateres.model.Ministro;
import br.com.juno.integration.api.confrateres.utils.Uteis;
import br.com.juno.integration.api.model.Charge;
import lombok.Getter;
import lombok.Setter;

@Named(value = "junoPagamentosBean")
@ViewScoped
public class JunoPagamentosBean implements Serializable{

	private static final long serialVersionUID = 1L;

	@Inject
	private JunoPagamentosBO junoPagamentosBO;

	@Getter @Setter private Ministro ministroTemp;
	@Getter @Setter private Ministro ministro;
	@Getter @Setter private List<JunoRegLancamentosTO> lancamentosTOList;
	@Getter @Setter private List<JunoRegLancamentosTO> lancamentosTOSelecionadosList;

	@Getter @Setter private List<JunoParcelamentoTO> parcelamentoTOList;
	@Getter @Setter private JunoParcelamentoTO parcelamentoSelecionado = new JunoParcelamentoTO();

	@Getter @Setter private BigDecimal vlTotalSaldoDevedor;
	@Getter @Setter private BigDecimal vlTotalSelecionado;

	@Getter @Setter private String dsSegundoEmail;

	@Getter @Setter private Charge charge;

	@Getter @Setter private String dsFormaPagamento; //Valores válidos: "BOLETO" ou "CARTAOCREDITO"
	private String dsFormaPagamentoReter;
	
	@Getter @Setter private boolean flCaptha;

	public void inicializar() {
		// Inicializar MinistroTemp
		if(ministroTemp == null) {
			ministroTemp = new Ministro();
			this.flCaptha = true;
			this.dsFormaPagamento = "";
		}

		// Identificar ambiente 1=HML 2=PRD
		Uteis.buscarAmbiente();
		
	}


	public void listarPendenciasMinistro() {

		// Limpar charge
		this.charge = null;

		// Validar AccesToken
		junoPagamentosBO.validarAccessToken();

		this.atualizarPendenciasMinistro();

	}

	private void atualizarPendenciasMinistro() {

		// Limpar variáveis
		this.vlTotalSaldoDevedor = BigDecimal.ZERO;
		this.vlTotalSelecionado = BigDecimal.ZERO;

		this.parcelamentoTOList = new ArrayList<JunoParcelamentoTO>();

		// Buscar Ministro
		ministro = junoPagamentosBO.buscarMinistro(ministroTemp);

		if(ministro == null) {
			// Limpar lista de lancamentos
			this.lancamentosTOList = null;
			
			throw new NegocioException("Ministro não localizado!");
		}

		// Para exibir segundo e-mail
		this.ministroTemp.setDsSegundoEmail(ministro.getDsSegundoEmail());
		// this.dsSegundoEmail = ministro.getDsSegundoEmail();

		// Buscar Lancamentos em aberto do ministro
		this.lancamentosTOList = junoPagamentosBO.buscarLancamentosAbertos(ministro.getSqMinistro());

		// Obter o valor total do saldo devedor do ministro
		for (JunoRegLancamentosTO lan : lancamentosTOList) {
			this.vlTotalSaldoDevedor = this.vlTotalSaldoDevedor.add(lan.getVlSaldo());
		}
		
		// Inibir Capcha
		this.flCaptha = false;

	}

	public void calcularSelecionados() {
		// Montar array com registros selecionados
		this.lancamentosTOSelecionadosList = new ArrayList<JunoRegLancamentosTO>();
		for (JunoRegLancamentosTO selecionado : lancamentosTOList) {
			if(selecionado.isFlLancamentoSelecionado())
				this.lancamentosTOSelecionadosList.add(selecionado);
		}

		// Atualizar valor total dos registros selecionados
		this.vlTotalSelecionado = BigDecimal.ZERO;
		for (JunoRegLancamentosTO selecionado : this.lancamentosTOSelecionadosList) {
			this.vlTotalSelecionado = this.vlTotalSelecionado.add(selecionado.getVlSaldo());
		}

		// Montar quantidade de parcelas para que o ministro escolha como fazer o pagamento
		this.parcelamentoTOList = new ArrayList<JunoParcelamentoTO>();

		if(this.vlTotalSelecionado.compareTo(BigDecimal.ZERO) == 1 ) {
			JunoParcelamentoTO parcela = new JunoParcelamentoTO();
			int qtdeParcelas = 1;
			parcela.setQtdeParcelas(qtdeParcelas);
			parcela.setVlParcela(this.vlTotalSelecionado);
			this.parcelamentoTOList.add(parcela);

			BigDecimal vlParcelaMinimo = JunoConfig.getVlParcelaMinimo();


			int qtdeMaximaParcelas;
			if(this.dsFormaPagamento.equalsIgnoreCase("BOLETO")) {
				qtdeMaximaParcelas = JunoConfig.getQtdeMaximaParcelasBoleto();
			} else if(this.dsFormaPagamento.equalsIgnoreCase("CARTAOCREDITO")) {
				qtdeMaximaParcelas = JunoConfig.getQtdeMaximaParcelasCartao();
			} else {
				qtdeMaximaParcelas = 0;
			}


			// Verificar se o valor da parcela é maior/menor que o valor configurado para minimo de parcela
			while((parcela.getVlParcela().compareTo(vlParcelaMinimo) == 1) && 
					(qtdeParcelas < qtdeMaximaParcelas)) {
				qtdeParcelas ++;
				parcela = new JunoParcelamentoTO();
				parcela.setQtdeParcelas(qtdeParcelas);
				parcela.setVlParcela(this.vlTotalSelecionado.divide(BigDecimal.valueOf(qtdeParcelas), 2, RoundingMode.CEILING));

				if(parcela.getVlParcela().compareTo(vlParcelaMinimo) == 1)
					this.parcelamentoTOList.add(parcela);
				else
					break;

			}
		}

	}
	
	public void formaPagamento() {
	//	this.dsFormaPagamentoReter = this.dsFormaPagamento;
		this.calcularSelecionados();
	}

	public String emitirCobranca() {
		if(this.parcelamentoSelecionado == null) {
			throw new NegocioException("Selecione os Pagamentos e o parcelamento desejado!");
		} else if(this.dsFormaPagamento.isEmpty()) {
			throw new NegocioException("Selecione a FORMA DE PAGAMENTO.");
		}


		// Verificar se o Segundo e-mail foi alterado e fazer update no cadastro do Ministro
		if(!this.ministroTemp.getDsSegundoEmail().trim().equals(ministro.getDsSegundoEmail().trim())
				&& (!this.ministroTemp.getDsSegundoEmail().trim().equals(ministro.getDsEmail().trim()))) {

			this.ministro.setDsSegundoEmail(this.ministroTemp.getDsSegundoEmail().trim());

			junoPagamentosBO.salveMinistro(this.ministro);
		} 

		charge = junoPagamentosBO.emitirCobranca(this.ministro, this.lancamentosTOSelecionadosList, this.parcelamentoSelecionado, this.dsFormaPagamento);

		this.dsFormaPagamentoReter = this.dsFormaPagamento;
		
		this.atualizarPendenciasMinistro();

		return charge.getCheckoutUrl();
	}

	public void exibirCobrancaHistorico(String dsUrl) {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(dsUrl);
		} catch (IOException e) {
			throw new NegocioException("Erro ao tentar reemitir cobrança: Copie e cole a URL no navegador: " + dsUrl);
		}
	}

	public void exibirBoletoAtual() {
		try {
			if(dsFormaPagamentoReter.equalsIgnoreCase("BOLETO")) {
				FacesContext.getCurrentInstance().getExternalContext().redirect(charge.getLink());
			} else if(dsFormaPagamentoReter.equalsIgnoreCase("CARTAOCREDITO")) {
				FacesContext.getCurrentInstance().getExternalContext().redirect(charge.getCheckoutUrl());
			} else {
				FacesContext.getCurrentInstance().getExternalContext().redirect(charge.getCheckoutUrl());
			}
		} catch (IOException e) {
			throw new NegocioException("Erro ao tentar emitir cobrança: Copie e cole a URL no navegador: " + charge.getCheckoutUrl());
		}
	}
}
