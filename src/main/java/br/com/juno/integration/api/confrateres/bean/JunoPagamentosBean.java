package br.com.juno.integration.api.confrateres.bean;


import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import br.com.juno.integration.api.confrateres.bo.JunoPagamentosBO;
import br.com.juno.integration.api.confrateres.exception.NegocioException;
import br.com.juno.integration.api.confrateres.model.Ago;
import br.com.juno.integration.api.confrateres.model.AgoChargeRecibo;
import br.com.juno.integration.api.confrateres.model.AgoFormaPagamento;
import br.com.juno.integration.api.confrateres.model.AgoOrientacoes;
import br.com.juno.integration.api.confrateres.model.AgoRecibo;
import br.com.juno.integration.api.confrateres.model.Convencao;
import br.com.juno.integration.api.confrateres.model.JunoParcelamentoTO;
import br.com.juno.integration.api.confrateres.model.JunoRegLancamentosTO;
import br.com.juno.integration.api.confrateres.model.Ministro;
import br.com.juno.integration.api.confrateres.utils.AgoInscricaoOuAnuidade;
import br.com.juno.integration.api.confrateres.utils.Uteis;
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
	@Getter @Setter private Ministro agoMinistroOutrosTemp;
	@Getter @Setter private List<Ministro> agoMinistroOutrosTempTOList;
	@Getter @Setter private List<JunoRegLancamentosTO> lancamentosTOList;
	@Getter @Setter private List<JunoRegLancamentosTO> agoLancamentosTOListTemp;
	@Getter @Setter private List<JunoRegLancamentosTO> lancamentosTOSelecionadosList;
	@Getter @Setter private List<AgoFormaPagamento> agoFormasPagamento;
	@Getter @Setter private List <AgoOrientacoes> agoOrientacoes;
	@Getter @Setter private List<AgoRecibo> agoReciboList;
	@Getter @Setter private AgoRecibo agoRecibo;
	@Getter @Setter private AgoFormaPagamento agoFormaPagamentoSelecionada;


	@Getter @Setter private List<JunoParcelamentoTO> parcelamentoTOList;
	@Getter @Setter private JunoParcelamentoTO parcelamentoSelecionado = new JunoParcelamentoTO();

	@Getter @Setter private BigDecimal vlTotalSaldoDevedor;
	@Getter @Setter private BigDecimal vlTotalSelecionado;

	@Getter @Setter private String dsSegundoEmail;

	@Getter @Setter private AgoChargeRecibo agoChargeRecibo;

	@Getter @Setter private Ago agoAtiva;
	
	@Getter @Setter private Convencao convencao;
	@Getter @Setter private String dsEmailAtivo;
	@Getter @Setter private String dsWatsappAtivo;

	private String dsFormaPagamentoReter;

	@Getter @Setter private boolean flExibirCaptcha;
	@Getter @Setter private boolean flAgo;
	@Getter @Setter private boolean flBotoesInscricaoAgo;
	@Getter @Setter private boolean flExibirBotaoExcluirInscricaoAgo;
	@Getter @Setter private boolean flCheckBoxRealizarPagamento;
	@Getter @Setter private boolean flExibirFormasPagamento;
	@Getter @Setter private boolean flExibirParcelamento;
	@Getter @Setter private boolean flExibirBotaoGerarPagamento;
	@Getter @Setter private boolean flExibirBotaoMinhaInscricao;
	@Getter @Setter private boolean flExibirBotaoOutroMinistro;
	@Getter @Setter private boolean flExibirBotaoOutros;
	@Getter @Setter private boolean flExibirHistorico;
	@Getter @Setter private boolean flExibirInformacoesPagamentoOutros;

	public void inicializarRedirect() {
		
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest(); 
		
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(request.getRequestURL() + Uteis._URL_COMPLEMENTO);
		} catch (IOException e) {
			throw new NegocioException("Erro obtendo URL:" + request.getRequestURL() + Uteis._URL_COMPLEMENTO);
		}
	}

	public void inicializar() {
		// Inicializar MinistroTemp
		if(ministroTemp == null) {
			ministroTemp = new Ministro();
			this.flBotoesInscricaoAgo = false;
			this.flExibirBotaoExcluirInscricaoAgo = false;
			this.flExibirBotaoGerarPagamento = false;
			this.flCheckBoxRealizarPagamento = false;
			this.buscarAgoFormasPagamento();
			this.agoChargeRecibo = new AgoChargeRecibo();
			this.flExibirCaptcha = true;
			this.flExibirFormasPagamento = false;
			this.flExibirParcelamento = false;

			this.flExibirBotaoMinhaInscricao = true;
			this.flExibirBotaoOutroMinistro = true;
			this.flExibirBotaoOutros = true;
			this.flExibirHistorico = false;

			this.flExibirInformacoesPagamentoOutros = false;
		}

		// Identificar ambiente 1=HML 2=PRD
		Uteis.buscarAmbiente();

		// Verificar se há AGO ativa
		this.agoAtiva = junoPagamentosBO.findAgoAtiva();
		if(this.agoAtiva != null) {
			this.flAgo = true;
			this.dsEmailAtivo = agoAtiva.getDsEmailEvento();
			this.dsWatsappAtivo = agoAtiva.getDsWhatsappEvento();
		} else {
			this.flAgo = false;
			this.convencao = junoPagamentosBO.buscarConvencao(1L);
			this.dsEmailAtivo = this.convencao.getDsEmail();
			this.dsWatsappAtivo = this.convencao.getDsWatsapp();
		}

	}

	public void inicializarAgoOutroMinistro() {
		this.agoMinistroOutrosTemp = new Ministro();
		this.agoMinistroOutrosTempTOList = new ArrayList<Ministro>();
		this.flExibirBotaoExcluirInscricaoAgo = true;

		if(flExibirHistorico) {
			this.flExibirHistorico = false;
			this.lancamentosTOList = new ArrayList<JunoRegLancamentosTO>();
		}
	}

	public void inicializarAgoEsposaOutros() {	
		this.agoMinistroOutrosTemp = new Ministro();
		this.flExibirBotaoExcluirInscricaoAgo = true;

		if(flExibirHistorico) {
			this.flExibirHistorico = false;
			this.lancamentosTOList = new ArrayList<JunoRegLancamentosTO>();
		}
	}

	public void inicializarInscricoesDoMinistro() {
		this.agoReciboList = junoPagamentosBO.finAllReciboInscricoesDoMinistro(this.ministro.getSqMinistro());
		this.flExibirInformacoesPagamentoOutros = false;
	}

	// dsTipo vem da chamada pelo usuário da tela: Listar Pendências='pendencias' - INSCRICÃO AGO='ago'
	public void listarPendenciasMinistro(String dsTipo) {

		// Limpar charge
		this.agoChargeRecibo.setCharge(null);

		// Inibir botoes de inscricao
		this.flBotoesInscricaoAgo = false;
		this.flExibirBotaoExcluirInscricaoAgo = false;
		this.flCheckBoxRealizarPagamento = false;

		this.validarMinistro(dsTipo);

	}

	// dsTipo vem da chamada pelo usuário da tela: Listar Pendências='pendencias' - INSCRICAO AGO='ago'
	public void inscricaoAgo(String dsTipo) {
		// Limpar charge
		this.agoChargeRecibo.setCharge(null);

		this.validarMinistro(dsTipo);
		this.flBotoesInscricaoAgo = true;
		this.flExibirBotaoExcluirInscricaoAgo = true;
		this.flCheckBoxRealizarPagamento = true;
		this.flExibirFormasPagamento = false;
		this.flExibirParcelamento = false;
		this.flExibirBotaoGerarPagamento = false;
		this.flExibirBotaoMinhaInscricao = true;
		this.flExibirBotaoOutroMinistro = true;
		this.flExibirBotaoOutros = true;
		this.flExibirInformacoesPagamentoOutros = false;
	}


	private void validarMinistro(String dsTipo) {

		// Limpar lista de lancamentos
		this.lancamentosTOList = new ArrayList<JunoRegLancamentosTO>();

		// Limpar variáveis
		this.vlTotalSaldoDevedor = BigDecimal.ZERO;
		this.vlTotalSelecionado = BigDecimal.ZERO;


		// Buscar Ministro
		ministro = junoPagamentosBO.buscarMinistro(ministroTemp);

		if(ministro == null) {
			throw new NegocioException("Ministro não localizado!");
		}

		// Para exibir segundo e-mail
		this.ministroTemp.setDsSegundoEmail(ministro.getDsSegundoEmail());

		// Listar pendëncias do Ministro
		if(dsTipo.equalsIgnoreCase("pendencias"))
			this.atualizarPendenciasMinistro(dsTipo, ministro, null);

		// Inibir Capcha
		this.flExibirCaptcha = false;
	}

	private void atualizarPendenciasMinistro(String dsTipo, Ministro m, AgoRecibo agoRecibo) {
		this.parcelamentoTOList = new ArrayList<JunoParcelamentoTO>();

		// Buscar Lancamentos em aberto do ministro para pendências OU os lancamentos feitos na inscricao AGO
		if(dsTipo.equalsIgnoreCase("pendencias")) {
			this.lancamentosTOList = junoPagamentosBO.buscarLancamentosAbertos(ministro.getSqMinistro());
		} else if(dsTipo.equalsIgnoreCase("ago")) {
			this.lancamentosTOList = junoPagamentosBO.agoBuscarLancamentosDeUmRecibo(agoRecibo);
		}
		
		if(this.lancamentosTOList.size() > 0) {
			this.flExibirFormasPagamento = true;
			this.flExibirParcelamento = true;
		}

		// Obter o valor total do saldo devedor do ministro
		this.vlTotalSaldoDevedor  = this.somaTotalSaldo(lancamentosTOList);  // lancamentosTOList.stream().map(item->item.getVlSaldo()).reduce(BigDecimal.ZERO,BigDecimal::add);

	}

	// dsTipo vem da chamada pelo usuário da tela: Listar Pendências='pendencias' - INSCRICAO AGO='ago'
	public void buscarPendenciasInscricao(String dsTipo) {
		if(this.flExibirHistorico) {
			this.flExibirHistorico = false;
			this.lancamentosTOList = new ArrayList<JunoRegLancamentosTO>();
		}
		this.listarPendenciasInscricao(dsTipo, this.ministro);
		this.flExibirBotaoExcluirInscricaoAgo = true;
	}

	// Buscar pendencias que impedem a inscricão (Só consegue pagar a inscricão se pagar também as pendencias)
	private void listarPendenciasInscricao(String dsTipo, Ministro agoMinistroPendencias) {

		// Verificar se o ministro já está na lista, se sim não fazer nova inclusão do mesmo ministro
		List<JunoRegLancamentosTO> result = lancamentosTOList.stream()
				.filter(item -> item.getSqMinistro() != null && item.getSqMinistro().equals(agoMinistroPendencias.getSqMinistro()))
				.collect(Collectors.toList());

		if(result.size() > 0) {
			throw new NegocioException("Inscricão já está na lista para o Ministro: " + agoMinistroPendencias.getCdCodigo() + "-" + agoMinistroPendencias.getNmNome());
		}

		// Limpar variáveis
		this.vlTotalSelecionado = BigDecimal.ZERO;

		// Buscar anuidades em aberto do ministro (para fazer a inscricão não pode ter anuidades em aberto)
		this.agoLancamentosTOListTemp = junoPagamentosBO.agoBuscarAnuidadesAberto(agoMinistroPendencias.getSqMinistro());

		// Buscar valor da Inscricão e adicionar aos lancamentos, parametro "01"=Ministro Confrateres - "02"=Esposas de ministros e outros que Não ministro
		BigDecimal vlInscricao = junoPagamentosBO.AgoBuscarValorInscricao("01");

		// Preparar para inserir o valor da inscricao na lista de inscritos
		JunoRegLancamentosTO regLancamentoInscricao = new JunoRegLancamentosTO(); 
		regLancamentoInscricao.setSqRegLancamento(0L);
		regLancamentoInscricao.setSqMinistro(agoMinistroPendencias.getSqMinistro());
		regLancamentoInscricao.setDsLancamentoTipo("Inscrição AGO");
		regLancamentoInscricao.setVlLancamento(vlInscricao);
		regLancamentoInscricao.setVlSaldo(vlInscricao);
		regLancamentoInscricao.setFlLancamentoSelecionado(true);
		regLancamentoInscricao.setDsLancamentoTipo(regLancamentoInscricao.getDsLancamentoTipo() + " (" + agoMinistroPendencias.getCdCodigo() + "-" + agoMinistroPendencias.getNmNome() + ")");
		regLancamentoInscricao.setAgoInscricaoOuAnuidade(AgoInscricaoOuAnuidade.INSCRICAO);
		regLancamentoInscricao.setDsCor("color: #0000FF;");


		// Adicionar a identificacão do ministro e identificar anuidade em atraso.
		for (JunoRegLancamentosTO to : agoLancamentosTOListTemp) {
			to.setSqMinistro(agoMinistroPendencias.getSqMinistro());
			to.setFlLancamentoSelecionado(true);
			to.setDsLancamentoTipo(to.getDsLancamentoTipo() + " (" + agoMinistroPendencias.getCdCodigo() + "-" + agoMinistroPendencias.getNmNome() + ")");

			to.setAgoInscricaoOuAnuidade(AgoInscricaoOuAnuidade.ANUIDADE);
		}

		// Adicionar o valor da inscricão na lista temporária
		this.agoLancamentosTOListTemp.add(regLancamentoInscricao);

		// Adicionar a lista Temporária na lista Definitiva
		for(int i = 0 ; i < agoLancamentosTOListTemp.size(); i++) {
			this.lancamentosTOList.add(agoLancamentosTOListTemp.get(i));
		}

		// Obter o valor total do saldo devedor do ministro
		this.obterValorToltalLista();
		this.calcularSelecionados();
	}

	// Obter o valor total do saldo devedor do ministro
	private void obterValorToltalLista() {

		// Zerar variáveis
		this.vlTotalSelecionado = BigDecimal.ZERO;

		this.vlTotalSaldoDevedor = this.somaTotalSaldo(this.lancamentosTOList);

	}

	public void calcularSelecionados() {
		// Montar array com registros selecionados
		this.lancamentosTOSelecionadosList = new ArrayList<JunoRegLancamentosTO>();
		for (JunoRegLancamentosTO selecionado : lancamentosTOList) {
			if(selecionado.isFlLancamentoSelecionado()) {
				if(!this.flAgo) {
					selecionado.setAgoInscricaoOuAnuidade(AgoInscricaoOuAnuidade.ANUIDADE);
				}
				this.lancamentosTOSelecionadosList.add(selecionado);
			}
		}

		// Atualizar valor total dos registros selecionados, e verifica se será permitido parcelamento		
		this.vlTotalSelecionado = this.somaTotalSaldo(lancamentosTOSelecionadosList); // lancamentosTOSelecionadosList.stream().map(item->item.getVlSaldo()).reduce(BigDecimal.ZERO,BigDecimal::add);

		// ** Marcar se o total pode ser parcelado
		boolean flPermiteParcelar = false;
		
		// **
		if(this.flAgo) {
			List<JunoRegLancamentosTO> selecionadoAnuidade = new ArrayList<JunoRegLancamentosTO>();
			for (JunoRegLancamentosTO selecionado : this.lancamentosTOSelecionadosList) {
				//	this.vlTotalSelecionado = this.vlTotalSelecionado.add(selecionado.getVlSaldo());
				if(selecionado.getAgoInscricaoOuAnuidade().equals(AgoInscricaoOuAnuidade.ANUIDADE)) {
					selecionadoAnuidade.add(selecionado);
				}
			}


			// ** Simular parcela de somente Anuidades para verificar se o total pode ser parcelado
			BigDecimal vlTotalAnuidades = selecionadoAnuidade.stream().map(item->item.getVlSaldo()).reduce(BigDecimal.ZERO,BigDecimal::add);
			if(agoFormaPagamentoSelecionada != null) {
				BigDecimal vlParcelaSimulada = vlTotalAnuidades.divide(BigDecimal.valueOf(2), 2, RoundingMode.CEILING);

				if(vlParcelaSimulada.compareTo(agoFormaPagamentoSelecionada.getVlParcelaMinimo()) == 1) {
					flPermiteParcelar = true;
				}
			}
		} else {
			flPermiteParcelar = true;
		}

		// Montar quantidade de parcelas para que o ministro escolha como fazer o pagamento
		this.parcelamentoTOList = new ArrayList<JunoParcelamentoTO>();

		if(this.vlTotalSelecionado.compareTo(BigDecimal.ZERO) == 1 ) {
			JunoParcelamentoTO parcela = new JunoParcelamentoTO();
			int qtdeParcelas = 1;
			parcela.setQtdeParcelas(qtdeParcelas);
			parcela.setVlParcela(this.vlTotalSelecionado);
			this.parcelamentoTOList.add(parcela);
// **
			if(flPermiteParcelar) {
				BigDecimal vlParcelaMinimo = BigDecimal.ZERO;
				int qtdeMaximaParcelas = 1;

				// Verificar se a forma de pagamento já foi selecionada
				if(agoFormaPagamentoSelecionada != null) {
					vlParcelaMinimo = agoFormaPagamentoSelecionada.getVlParcelaMinimo();
					qtdeMaximaParcelas = agoFormaPagamentoSelecionada.getNnQtdeParcelas();
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

		// Exibir lista e botões
		this.flExibirFormasPagamento = true;
		this.flExibirParcelamento = true;
		this.flExibirBotaoGerarPagamento = true;

	}

	// Calcular valores dos registros selecionados de acordo com a forma de pagamento
	public void formaPagamento() {
		this.calcularSelecionados();
	}

	// Fazer a emissão da cobranca
	public String emitirCobranca(String dsTipo) {
		if(this.parcelamentoSelecionado == null) {
			throw new NegocioException("Selecione os Pagamentos, forma de pagamento e o parcelamento desejado!");
		} else if(this.agoFormaPagamentoSelecionada == null) {
			throw new NegocioException("Selecione a FORMA DE PAGAMENTO.");
		}

		// Verificar se o Segundo e-mail foi alterado e fazer update no cadastro do Ministro
		if(ministro.getDsSegundoEmail() == null) ministro.setDsSegundoEmail("");
		if(ministro.getDsEmail() == null) ministro.setDsEmail("");
		if(!this.ministroTemp.getDsSegundoEmail().trim().equals(ministro.getDsSegundoEmail().trim())
				&& (!this.ministroTemp.getDsSegundoEmail().trim().equals(ministro.getDsEmail().trim()))) {

			this.ministro.setDsSegundoEmail(this.ministroTemp.getDsSegundoEmail().trim());

			junoPagamentosBO.salveMinistro(this.ministro);
		} 

		// Emitir cobranca
		this.agoChargeRecibo = junoPagamentosBO.emitirCobranca(this.ministro, this.lancamentosTOSelecionadosList, this.parcelamentoSelecionado, this.agoFormaPagamentoSelecionada.getDsFormaPagamentoReduzido(), dsTipo, this.agoAtiva);

		this.dsFormaPagamentoReter = this.agoFormaPagamentoSelecionada.getDsFormaPagamentoReduzido();


		this.atualizarPendenciasMinistro(dsTipo, ministro, this.agoChargeRecibo.getAgoRecibo());

		this.flExibirBotaoExcluirInscricaoAgo = false;
		this.flExibirFormasPagamento = false;
		this.flExibirParcelamento = false;
		this.flExibirBotaoGerarPagamento = false;
		this.flExibirBotaoMinhaInscricao = false;
		this.flExibirBotaoOutroMinistro = false;
		this.flExibirBotaoOutros = false;

		if(agoFormaPagamentoSelecionada.getDsFormaPagamentoReduzido().equalsIgnoreCase(Uteis._DEPOSITO) || 
				agoFormaPagamentoSelecionada.getDsFormaPagamentoReduzido().equalsIgnoreCase(Uteis._PIX) ||
				agoFormaPagamentoSelecionada.getDsFormaPagamentoReduzido().equalsIgnoreCase(Uteis._OUTROS)) {
			this.flExibirInformacoesPagamentoOutros = true;
		}

		return agoChargeRecibo.getCharge() != null? agoChargeRecibo.getCharge().getCheckoutUrl(): null;
	}

	// Exibir em tela separada a cobranca se emitida pela Juno
	public void exibirCobrancaHistorico(String dsUrl) {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(dsUrl);
		} catch (IOException e) {
			throw new NegocioException("Erro ao tentar reemitir cobrança: Copie e cole a URL no navegador: " + dsUrl);
		}
	}

	// Exibir cobranca gerada no momento, se emitida pela Juno
	public void exibirBoletoAtual() {
		try {
			if(dsFormaPagamentoReter.equalsIgnoreCase(Uteis._BOLETO)) {
				FacesContext.getCurrentInstance().getExternalContext().redirect(this.agoChargeRecibo.getCharge().getLink());
			} else if(dsFormaPagamentoReter.equalsIgnoreCase(Uteis._CARTAOCREDITO)) {
				FacesContext.getCurrentInstance().getExternalContext().redirect(this.agoChargeRecibo.getCharge().getCheckoutUrl());
			} else {
				FacesContext.getCurrentInstance().getExternalContext().redirect(this.agoChargeRecibo.getCharge().getCheckoutUrl());
			}
		} catch (IOException e) {
			throw new NegocioException("Erro ao tentar emitir cobrança: Copie e cole a URL no navegador: " + this.agoChargeRecibo.getCharge().getCheckoutUrl());
		}
	}

	// Buscar outro Ministro para fazer a inscricao da AGO
	public void AgoPesquisarMinistroOutros(String dsTipo) {		
		this.agoMinistroOutrosTempTOList = new ArrayList<Ministro>();
		this.agoMinistroOutrosTempTOList = junoPagamentosBO.agoBuscarMinistroOutros(this.agoMinistroOutrosTemp);
	}

	// Inserir Ministros na Lista de Inscricoes para AGO (Outros ministros, não o logado)
	public void agoMinistroOutrosSelecionado(Ministro agoMinistroSelecionado) {
		this.listarPendenciasInscricao("ago", agoMinistroSelecionado);	

		// Fechar sub-tela
		Uteis.fecharDialogoPrimeFaces(true);
	}

	// Excluir a inscricao de um ministro que anteriormente foi incluido na lista de inscricoes
	public void AgoExcluirInscricao(String dsTipo, JunoRegLancamentosTO lancamentoTO) {		
		this.lancamentosTOList.removeIf(p -> (p.getSqMinistro() != null? p.getSqMinistro().equals(lancamentoTO.getSqMinistro()): p.equals(lancamentoTO)));

		// Atualizar o valor total da lista
		this.obterValorToltalLista();
		this.calcularSelecionados();
	}

	// Incluir pessoas que não são ministros da confrateres, exemplo: Esposa de Ministro e outros 
	public void agoEsposaOutrosSelecionado(Ministro m) {	
		if(m.getNmNome() == null || m.getNmNome().trim().length() == 0) {
			throw new NegocioException("Informe o nome da pessoa para fazer a inscricão na AGO");
		} else if(m.getNmNome().trim().length() < 4) {
			throw new NegocioException("Informe o nome mais completo, por favor!");
		} else {
			String[] textoSeparado = m.getNmNome().trim().split(" ");
			int nnQtde = 0;
			for (String st : textoSeparado) {
				if(st.trim().length() > 0) nnQtde ++;
			}

			if(nnQtde < 2) {
				throw new NegocioException("Informe o nome completo, por favor!");
			}
		}

		// Buscar valor da inscricão para Esposas e Outros
		BigDecimal vlInscricao = junoPagamentosBO.AgoBuscarValorInscricao("02");

		// Preparar para inserir o valor da inscricao na lista de inscritos
		JunoRegLancamentosTO regLancamentoInscricao = new JunoRegLancamentosTO(); 
		regLancamentoInscricao.setSqRegLancamento(0L);
		regLancamentoInscricao.setNmEsposaOutros(m.getNmNome());
		regLancamentoInscricao.setDsLancamentoTipo("Inscrição AGO (" + m.getNmNome() + ")");
		regLancamentoInscricao.setVlLancamento(vlInscricao);
		regLancamentoInscricao.setVlSaldo(vlInscricao);
		regLancamentoInscricao.setFlLancamentoSelecionado(true);
		regLancamentoInscricao.setAgoInscricaoOuAnuidade(AgoInscricaoOuAnuidade.INSCRICAO);
		regLancamentoInscricao.setDsCor("color: #0000FF;");

		// Adicionar informacoes na lista temporária
		this.agoLancamentosTOListTemp = new ArrayList<JunoRegLancamentosTO>();
		this.agoLancamentosTOListTemp.add(regLancamentoInscricao);

		// Adicionar a lista Temporária na lista Definitiva
		for(int i = 0 ; i < agoLancamentosTOListTemp.size(); i++) {
			this.lancamentosTOList.add(agoLancamentosTOListTemp.get(i));
		}

		// Atualizar o valolr total da lista
		this.obterValorToltalLista();
		this.calcularSelecionados();

		// Fechar sub-tela
		Uteis.fecharDialogoPrimeFaces(true);
	}

	// Buscar Formas de Pagamento
	public void buscarAgoFormasPagamento() {
		this.agoFormasPagamento = junoPagamentosBO.agoBuscarFormasPagamento();
	}

	// Preparar Orientacoes para pagamentos em outros tipos
	public void agoOrientacoes() {

		AgoOrientacoes orientacoes;
		this.agoOrientacoes = new ArrayList<AgoOrientacoes>();
		
		for (AgoFormaPagamento fp : agoFormasPagamento) {
			if(!fp.getDsFormaPagamentoReduzido().equalsIgnoreCase(Uteis._BOLETO) && !fp.getDsFormaPagamentoReduzido().equalsIgnoreCase(Uteis._CARTAOCREDITO)) {
				orientacoes = new AgoOrientacoes();
				orientacoes.setDsTipoPagamento(fp.getDsFormaPagamento());
				orientacoes.setDsOrientacoes(fp.getDsDescricao() + " - Encaminhar Email com o comprovante de pagamento para: " + this.dsEmailAtivo + " - O pagamento será confirmado após o recebimento do Email com o comprovante de pagamento.");

				this.agoOrientacoes.add(orientacoes);
			}
		}
	}

	public void buscarInscricoesMinistro(AgoRecibo agoReciboTemp) {

		this.agoRecibo = agoReciboTemp;
		this.lancamentosTOList = junoPagamentosBO.agoBuscarLancamentosDeUmRecibo(agoReciboTemp);

		this.vlTotalSaldoDevedor = somaTotalSaldo(lancamentosTOList);
		this.vlTotalSelecionado = BigDecimal.ZERO;

		this.flExibirBotaoExcluirInscricaoAgo = false;
		this.flExibirFormasPagamento = false;
		this.flExibirParcelamento = false;
		this.flExibirBotaoGerarPagamento = false;
		this.flExibirHistorico = true;

		this.agoChargeRecibo = new AgoChargeRecibo();

		// Fechar sub-tela
		Uteis.fecharDialogoPrimeFaces(true);
	}

	private BigDecimal somaTotalSaldo(List<JunoRegLancamentosTO> lancamentos) {
		return lancamentos.stream().map(item->item.getVlSaldo()).reduce(BigDecimal.ZERO,BigDecimal::add);
	}

}
