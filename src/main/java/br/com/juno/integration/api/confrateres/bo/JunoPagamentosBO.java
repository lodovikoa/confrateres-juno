package br.com.juno.integration.api.confrateres.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.juno.integration.api.confrateres.dao.JunoPagamentosDAO;
import br.com.juno.integration.api.confrateres.exception.NegocioException;
import br.com.juno.integration.api.confrateres.model.Ago;
import br.com.juno.integration.api.confrateres.model.AgoChargeRecibo;
import br.com.juno.integration.api.confrateres.model.AgoFormaPagamento;
import br.com.juno.integration.api.confrateres.model.AgoInscrito;
import br.com.juno.integration.api.confrateres.model.AgoRecibo;
import br.com.juno.integration.api.confrateres.model.AgoValorInscricao;
import br.com.juno.integration.api.confrateres.model.Boleto;
import br.com.juno.integration.api.confrateres.model.Convencao;
import br.com.juno.integration.api.confrateres.model.JunoConfig;
import br.com.juno.integration.api.confrateres.model.JunoConfiguracao;
import br.com.juno.integration.api.confrateres.model.JunoParcelamentoTO;
import br.com.juno.integration.api.confrateres.model.JunoRegLancamentosTO;
import br.com.juno.integration.api.confrateres.model.Ministro;
import br.com.juno.integration.api.confrateres.model.RegLancamento;
import br.com.juno.integration.api.confrateres.utils.Uteis;
import br.com.juno.integration.api.confrateres.utils.jpa.Transactional;
import br.com.juno.integration.api.services.JunoApiManager;
import br.com.juno.integration.api.services.request.charge.ChargeCreateRequest;
import br.com.juno.integration.api.services.request.charge.ChargeCreateRequest.Billing;
import br.com.juno.integration.api.services.request.charge.ChargeCreateRequest.Billing.Address;
import br.com.juno.integration.api.services.request.charge.ChargeCreateRequest.Charge;
import br.com.juno.integration.api.services.request.charge.ChargeCreateRequest.Charge.PaymentType;

public class JunoPagamentosBO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	JunoPagamentosDAO junoPagamentosDAO;

	@Inject
	private EntityManager manager;

	private static Log log = LogFactory.getLog(JunoPagamentosBO.class);

	// Buscar ministro
	public Ministro buscarMinistro(Ministro ministro) {
		log.info("Ambiente:(" + Uteis.getDsAmbiente() + ") - buscarMinistro(" + ministro.toString() + ") ");
		return junoPagamentosDAO.findByMinistroJuno(ministro);
	}

	// Salvar Ministro para registrar o Segundo e-mail
	@Transactional
	public void salveMinistro(Ministro ministro) {
		log.info("Ambiente:(" + Uteis.getDsAmbiente() + ") - salvarMinistro(" + ministro.toString() + ") ");
		try {
			// Atribuir Usuario de Auditoria
			ministro.setAuditoriaUsuario("confrater_juno");
			junoPagamentosDAO.saveMinistro(ministro);

		} catch (Exception e) {
			throw new NegocioException("Erro ao tentar salvar Segundo E-mail do Ministro: " + e.getMessage(), e);
		}

	}

	// Buscar lancamentos em aberto do ministro
	public List<JunoRegLancamentosTO> buscarLancamentosAbertos(Long sqMinistro) {
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - buscarLancamentosAbertos(sqMinistro = " + sqMinistro + ") ");

		return junoPagamentosDAO.findAllLancamentosAbertos(sqMinistro);
	}


	// Emitir boleto para os registros selecionados pelo ministro
	public AgoChargeRecibo emitirCobranca(Ministro ministro, List<JunoRegLancamentosTO> lancamentosTOSelecionadosList, JunoParcelamentoTO parcelamentoSelecionado, String dsFormaPagamento, String dsTipo, Ago ago) {	
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - emitirCobranca( INICIO ): " + ministro.toString());

		AgoChargeRecibo agoChargeRecibo = new AgoChargeRecibo();
		// Para pagamentos pela Juno
		if (!dsFormaPagamento.equalsIgnoreCase(Uteis._CARTAOCREDITO) && 
				!dsFormaPagamento.equalsIgnoreCase(Uteis._BOLETO) &&
				!dsFormaPagamento.equalsIgnoreCase(Uteis._PIX) &&
				!dsFormaPagamento.equalsIgnoreCase(Uteis._DEPOSITO) &&
				!dsFormaPagamento.equalsIgnoreCase(Uteis._OUTROS)) {

			throw new NegocioException("Forma de Pagamento selecionada não reconhecida. Informe ao tesoureiro Confrateres! " );
		}
		
		if(dsFormaPagamento.equalsIgnoreCase(Uteis._CARTAOCREDITO) || dsFormaPagamento.equalsIgnoreCase(Uteis._BOLETO)) {
			this.validarAccessToken();
		}

		agoChargeRecibo = this.salveBoleto(lancamentosTOSelecionadosList, parcelamentoSelecionado, ministro, ago, dsFormaPagamento, dsTipo);

		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - emitirCobranca( FIM ): " + ministro.toString());

		return agoChargeRecibo;
	}


	// Atribuir as configuracoes para classe JunoConfig final
	private void junoConfiguracaoConfrateres(JunoConfiguracao jc) {
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - junoConfiguracaoConfrateres() ");

		JunoConfig.setSqJunoConfiguracao(jc.getSqJunoConfiguracao());
		JunoConfig.setDsAmbiente(jc.getDsAmbiente());
		JunoConfig.setDsClientSecret(jc.getDsClientSecret());
		JunoConfig.setDsClientId(jc.getDsClientId());
		JunoConfig.setDsAccessToken(jc.getDsAccessToken());
		JunoConfig.setDsResourceToken(jc.getDsResourceToken());
		JunoConfig.setDsPublicToken(jc.getDsPublicToken());
		JunoConfig.setNnHoraMillessegundosAtualizacaoToken(jc.getNnHoraMillessegundosAtualizacaoToken());
		JunoConfig.setNnTokenTimeout(jc.getNnTokenTimeout());
		JunoConfig.setDsAuthorizationServer(jc.getDsAuthorizationServer());
		JunoConfig.setDsResouceServer(jc.getDsResouceServer());
		JunoConfig.setDsCacheTimeout(jc.getDsCacheTimeout());
		JunoConfig.setVlParcelaMinimo(jc.getVlParcelaMinimo());
		JunoConfig.setQtdeMaximaParcelasBoleto(jc.getQtdeMaximaParcelasBoleto());
		JunoConfig.setQtdeMaximaParcelasCartao(jc.getQtdeMaximaParcelasCartao());

	}

	// Validar AccessToken
	public void validarAccessToken() {
		// Obter a configuracao
		JunoConfiguracao jc = this.findByJunoConfiguracao();

		this.junoConfiguracaoConfrateres(jc);

		// Verificar se o token é válido
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - validarAccessToken() - Verificar se AccessToken é VÁLIDO");
		JunoApiManager.getAuthorizationService().getToken();

		if(!JunoConfig.isFlAccessTokenValido()) {
			JunoConfig.setFlAccessTokenValido(true);
			this.saveConfiguracao(jc);
		} else {
			log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - validarAccessToken() - AccessToken VÁLIDO ");
		}
	}

	// Buscar configuracoes no Banco de Dados
	private JunoConfiguracao findByJunoConfiguracao() {
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - findByJunoConfiguracao() Para o ambiente (" + Uteis.getDsAmbiente() + ")");
		if(Uteis.getDsAmbiente() == null || (!Uteis.getDsAmbiente().equals("HML") && !Uteis.getDsAmbiente().equals("PRD"))) {
			throw new NegocioException("Arquivo confrateres.properties não identificado com o tipo de ambiente.");
		}
		return junoPagamentosDAO.findByConfiguracao(Uteis.getDsAmbiente());
	}

	// Salvar Configuracao com novos AccessToken e Hora em milessegundos para identificar vencimento do AccessToken
	@Transactional
	public void saveConfiguracao(JunoConfiguracao jc) {
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - saveConfiguracao()");

		EntityTransaction trx = manager.getTransaction();
		try {	
			trx.begin();

			jc.setDsAccessToken(JunoConfig.getDsAccessToken());
			jc.setNnHoraMillessegundosAtualizacaoToken(JunoConfig.getNnHoraMillessegundosAtualizacaoToken());

			jc.setAuditoriaData(Uteis.DataHoje());
			jc.setAuditoriaUsuario("confrater_juno");


			junoPagamentosDAO.salveConfiguracao(jc);

			trx.commit();
			log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - saveConfiguracao() - COMMIT");
			log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - saveConfiguracao( HoraMileSegundos Atual   : " + jc.getNnHoraMillessegundosAtualizacaoToken() + " AccessToken Atual   : " + jc.getDsAccessToken() + " )");			
			log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - saveConfiguracao() - AccessToken Atualizado");

		} catch (Exception e) {
			trx.rollback();
			log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - saveConfiguracao() - ROLLBACK");
			log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - saveConfiguracao() - ERRO ao tentar atualizar AccessToken");
			throw e;
		}
	}


	// Preparar boleto para registrar na Juno
	private static ChargeCreateRequest createChargeRequest(AgoRecibo agoRecibo, Ministro ministro, List<JunoRegLancamentosTO> lancamentosTOSelecionadosList, JunoParcelamentoTO parcelamentoSelecionado, String dsFormaPagamento) {
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - ChargeCreateRequest() - Preparando boleto");

		// Ordenar a lista para que as inscricoes sejam as primeiras informacoes a serem exibidas no boleto/cartao da Juno
		lancamentosTOSelecionadosList.sort((p1, p2) -> p2.getAgoInscricaoOuAnuidade().getDsDescricao().compareTo(p1.getAgoInscricaoOuAnuidade().getDsDescricao()));

		StringBuilder dsLactos = new StringBuilder();
		int nnControle = 0;

		if(agoRecibo != null && agoRecibo.getSqRecibo() != null) {
			dsLactos.append("Recibo online: " + agoRecibo.getSqRecibo() + " - ");
		}

		for (JunoRegLancamentosTO lc : lancamentosTOSelecionadosList) {
			if(nnControle > 0) {
				dsLactos.append(" \n");
			}
			dsLactos.append(lc.getDsLancamentoTipo());
			dsLactos.append(" R$");
			dsLactos.append(lc.getVlSaldo());
			nnControle ++;
		}

		String dsLancamentosReduzidos = dsLactos.toString();
		if(dsLactos.length() > 400) {
			dsLancamentosReduzidos = dsLactos.substring(0, 370) + "\n Continua...";
		}

		ChargeCreateRequest.Charge charge = new Charge(dsLancamentosReduzidos);
		ChargeCreateRequest.Billing billing = new Billing();
		ChargeCreateRequest.Billing.Address address = new Address();

		charge.setAmount(parcelamentoSelecionado.getVlParcela());
		charge.setDueDate(Uteis.AdcionarDiasNaData(Uteis.DataHoje().toLocalDate(), 5)); 
		charge.setInstallments(parcelamentoSelecionado.getQtdeParcelas());

		if(dsFormaPagamento.equalsIgnoreCase(Uteis._BOLETO)) {
			charge.getPaymentTypes().add(PaymentType.BOLETO);
		} else if(dsFormaPagamento.equalsIgnoreCase(Uteis._CARTAOCREDITO)) {
			charge.getPaymentTypes().add(PaymentType.CREDIT_CARD);
			address.setPostCode(ministro.getDsCep());
			address.setStreet(ministro.getDsEndereco());
			address.setNumber("25");
			address.setNeighborhood(ministro.getDsBairro());
			address.setCity(ministro.getDsCidade());
			address.setState(ministro.getEstado().getDsUf());
		} else {
			throw new NegocioException("ERRO - Forma de Pagamento não identificada!");
		}

		billing.setName(ministro.getCdCodigo() + " - " + ministro.getNmNome());
		billing.setDocument(ministro.getDsCpf());
		billing.setEmail(ministro.getDsEmail());
		billing.setSecondaryEmail(ministro.getDsSegundoEmail());
		billing.setNotify(true);

		billing.setAddress(address);

		ChargeCreateRequest request = new ChargeCreateRequest(charge, billing);
		return request;
	}

	// Salvar Boleto obtido da Juno para os registros selecionados pelo Ministro
	@Transactional
	private AgoChargeRecibo salveBoleto(List<JunoRegLancamentosTO> regList, JunoParcelamentoTO parcelamentoSelecionado, Ministro ministro, Ago ago, String dsFormaPagamento, String dsTipo) {
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - salveBoleto() - INICIO");

		String dsBoletoEmitido = "Boleto não foi emitido!";
		EntityTransaction trx = manager.getTransaction();

		AgoRecibo agoRecibo = new AgoRecibo();
		AgoChargeRecibo agoChargeRecibo = new AgoChargeRecibo();

		try {
			trx.begin();		
			//Obter o número do Recibo antecipadamente 
			agoRecibo.setSqRecibo(0L);
			agoRecibo.setAgo(ago);
			agoRecibo.setDtRecibo(Uteis.DataHoje().toLocalDate());
			agoRecibo.setAuditoriaData(Uteis.DataHoje());
			agoRecibo.setAuditoriaUsuario("confrateres");
			agoRecibo = junoPagamentosDAO.agoSaveRecibo(agoRecibo);

			if(dsFormaPagamento.equalsIgnoreCase(Uteis._CARTAOCREDITO) || dsFormaPagamento.equalsIgnoreCase(Uteis._BOLETO)) {
				agoChargeRecibo = junoRegistrarBoleto(regList, parcelamentoSelecionado, ministro, dsFormaPagamento, agoRecibo);	
				dsBoletoEmitido = "Mas o boleto foi emitido: Consulte seu e-mail!";
			} 

			//Se for Inscricao AGO fazer os registros do recibo e dos inscritos
			if(dsTipo.equalsIgnoreCase("ago")) {
				agoRecibo = this.agoGravarRecibo(agoRecibo, ago, ministro, dsFormaPagamento, agoChargeRecibo.getCharge(), sumValorToalLancamentos(regList));

				agoChargeRecibo.setAgoRecibo(agoRecibo);

				// Registrar os inscritos na AGO
				AgoInscrito agoInscrito;
				for (JunoRegLancamentosTO inscricao : regList) {
					// if(inscricao.getAgoInscricaoOuAnuidade().equals(AgoInscricaoOuAnuidade.INSCRICAO)) {
					// Buscar Ministro
					Ministro m = null;
					if(inscricao.getSqMinistro() != null) {
						m = new Ministro();
						m = junoPagamentosDAO.findMinistroPrimaryKey(inscricao.getSqMinistro());
					} 

					agoInscrito = new AgoInscrito();
					agoInscrito.setSqInscrito(0L);
					agoInscrito.setAgoRecibo(agoRecibo);
					agoInscrito.setMinistro(m);  // Aqui deve ser o ministro inscrito e não o ministro que fez a inscricao
					agoInscrito.setNmOutrosInscrito(inscricao.getNmEsposaOutros()); // Para inscricão de NÃO ministros. Colocar o nome do inscrito
					agoInscrito.setVlInscricao(inscricao.getVlSaldo());
					agoInscrito.setDsInscAnuidade(inscricao.getAgoInscricaoOuAnuidade().toString());
					agoInscrito.setDsDescricaoLancamento(inscricao.getDsLancamentoTipo());
					agoInscrito.setVlValorLancamento(inscricao.getVlLancamento());
					agoInscrito.setVlValorSaldoDevedor(inscricao.getVlSaldo());
					agoInscrito.setAuditoriaData(Uteis.DataHoje());
					agoInscrito.setAuditoriaUsuario("confrateres");

					// Salvar inscrito na AGO
					junoPagamentosDAO.agoSaveInscricaoSolicitada(agoInscrito);
				}
				//	}

			}

			//---------------------------------------------------------------
			// Verificar se trata de inclusao ou alteracao de Boleto
			Boleto boleto;
			for (JunoRegLancamentosTO reg : regList) {
				// Definido reg.getSqRegLancamento() = 0, quando é para incluir somente Inscrição OUTROS
				if(reg.getSqRegLancamento() != 0) {
					boleto = junoPagamentosDAO.findBoletoPorRegLancamento(reg.getSqRegLancamento());
					if(boleto != null) {
						log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - salveBoleto() - Alterar boleto existente para o RegLancamento: " + reg.getSqRegLancamento());

						boleto.setDsUrl(agoChargeRecibo.getCharge() != null? agoChargeRecibo.getCharge().getCheckoutUrl(): dsFormaPagamento);
						boleto.getRegLancamento().setSqRegLancamento(reg.getSqRegLancamento());
						boleto.setAgoRecibo(agoRecibo);
						junoPagamentosDAO.saveBoleto(boleto);
					} else {
						log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - salveBoleto() - Registrar novo boleto para o RegLancamento: " + reg.getSqRegLancamento());

						boleto = new Boleto();
						boleto.setRegLancamento(new RegLancamento());

						boleto.setDsUrl(agoChargeRecibo.getCharge().getCheckoutUrl());
						boleto.getRegLancamento().setSqRegLancamento(reg.getSqRegLancamento());
						boleto.setAgoRecibo(agoRecibo);
						junoPagamentosDAO.saveBoleto(boleto);
					}
				} else {
					log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - salveBoleto() - Registrar novo boleto somente para INSCRICAO de OUTROS: " + reg.getSqRegLancamento());

					boleto = new Boleto();
					boleto.setDsUrl(agoChargeRecibo.getCharge() != null? agoChargeRecibo.getCharge().getLink(): dsFormaPagamento);
					boleto.setRegLancamento(null);
					boleto.setAgoRecibo(agoRecibo);
					junoPagamentosDAO.saveBoleto(boleto);
				}
			}

			trx.commit();
			log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - salveBoleto() - COMMIT");
		}catch (Exception e) {
			trx.rollback();
			log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - salveBoleto() - ROLLBACK");
			throw new NegocioException("Falha ao tentar registrar boleto na base de dados da CONFRATERES. " + dsBoletoEmitido);
		}

		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - salveBoleto() - FIM");

		return agoChargeRecibo;
	}

	// Registrar Cobranca na Juno
	private AgoChargeRecibo junoRegistrarBoleto(List<JunoRegLancamentosTO> regList,
			JunoParcelamentoTO parcelamentoSelecionado, Ministro ministro, String dsFormaPagamento,
			AgoRecibo agoRecibo) {

		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - junoRegistrarBoleto()");

		AgoChargeRecibo agoChargeRecibo = new AgoChargeRecibo();

		// Validar Token
		// this.validarAccessToken();

		// Emitir Boleto
		ChargeCreateRequest request = createChargeRequest(agoRecibo, ministro, regList, parcelamentoSelecionado, dsFormaPagamento);
		List<br.com.juno.integration.api.model.Charge> createdCharges = JunoApiManager.getChargeService().create(request);
		br.com.juno.integration.api.model.Charge createdCharge = createdCharges.get(0);

		agoChargeRecibo.setCharge(createdCharge);
		return agoChargeRecibo;
	}

	// Implementacões para AGO
	public AgoRecibo agoGravarRecibo(AgoRecibo agoRecibo, Ago ago, Ministro ministro, String dsFormaPagamento, br.com.juno.integration.api.model.Charge charge, BigDecimal vlTotal) {
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - agoGravarRecibo()");
		//		AgoRecibo agoRecibo = new AgoRecibo();
		//		agoRecibo.setSqRecibo(0L);
		agoRecibo.setAgo(ago);
		agoRecibo.setMinistro(ministro);
		agoRecibo.setDtRecibo(Uteis.DataHoje().toLocalDate());
		agoRecibo.setVlRecibo(vlTotal);
		agoRecibo.setDsFormaPagamento(dsFormaPagamento); 
		agoRecibo.setDsUrl(charge != null? charge.getCheckoutUrl(): dsFormaPagamento);
		agoRecibo.setAuditoriaData(Uteis.DataHoje());
		agoRecibo.setAuditoriaUsuario("confrateres");

		return junoPagamentosDAO.agoSaveRecibo(agoRecibo);

	}



	// Buscar formas de pagamento
	public List<AgoFormaPagamento> agoBuscarFormasPagamento() {
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - agoBuscarFormasPagamento()");

		return junoPagamentosDAO.findAllFormasPagamento();
	}


	// Buscar anuidades em aberto
	public List<JunoRegLancamentosTO> agoBuscarAnuidadesAberto(Long sqMinistro) {
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - agoBuscarAnuidadesAberto(sqMinistro = " + sqMinistro + ") ");

		return junoPagamentosDAO.agoAnuidadesAbertos(sqMinistro);
	}

	public BigDecimal AgoBuscarValorInscricao(String dsTipo) {
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - AgoBuscarValorInscricao(" + dsTipo + ") ");

		return junoPagamentosDAO.agoValorInscricao(dsTipo);
	}

	public Ago findAgoAtiva() {
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - findAgoAtiva() ");
		
		// Preparar retorno
		Ago agoAtiva = null;
		int nnControle = 0;
		
		// Buscar registros que está na situacão ativo
		List<Ago> agoAtivas = junoPagamentosDAO.agoAtivas();
		
		// Verificar se as Agos Ativas deveriam estar ativas mesmo, caso contrário desativa-las.
		List<AgoValorInscricao> inscricaoList;
		for (Ago ago : agoAtivas) {
			inscricaoList = junoPagamentosDAO.agoValorInscricaoAtivos(ago);
			
			if(inscricaoList == null || inscricaoList.size() == 0) {
				agoAtivaDesativar(ago);
			} else {
				agoAtiva = ago;
				nnControle ++;
			}
		}
		
		if(nnControle == 0) {
			log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - findAgoAtiva() --- NÃO HÁ AGO ATIVA ---");
		} else if(nnControle == 1){
			log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - findAgoAtiva() Selecionada AGO "+ agoAtiva.getCdAgo());
		} else {
			log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - findAgoAtiva() ----------- HÁ MAIS DE UMA AGO ATIVA ---- Foi selecionada AGO "+ agoAtiva.getCdAgo());
		}

		return agoAtiva;
	}
	
	// Desativar AGO que estava ativa
	public void agoAtivaDesativar(Ago ago) {
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - agoAtivaDesativar(" + ago.getCdAgo() + ") ");
		
		EntityTransaction trx = manager.getTransaction();
		
		try {	
			trx.begin();
			ago.setFlAtivo(false);
			junoPagamentosDAO.agoSaveAgo(ago);
			
			trx.commit();
		}catch (Exception e) {
			trx.rollback();
			log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - agoAtivaDesativar(" + ago.getSqAgo() + ") - ROLLBACK");
			throw new NegocioException("Falha ao tentar desativar AGO. " + ago.getCdAgo());
		}
		
	}

	public List<Ministro> agoBuscarMinistroOutros(Ministro agoMinistroOutrosTemp) {
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - agoBuscarMinistroOutros(" + agoMinistroOutrosTemp.toString() + ") ");

		if(agoMinistroOutrosTemp.getCdCodigo() == null && agoMinistroOutrosTemp.getNmNome().trim().length() == 0) {
			throw new NegocioException("Preencha o Registro confrateres ou o Nome do ministro");
		} else if(agoMinistroOutrosTemp.getCdCodigo() == null && agoMinistroOutrosTemp.getNmNome().trim().length() < 3) {
			throw new NegocioException("Preencha o Nome do ministro com pelo menos 3 caracteres");
		}
		
		
		List<Ministro> ministro = junoPagamentosDAO.agoMinistroOutros(agoMinistroOutrosTemp);
		
		if(ministro == null || ministro.size() == 0) {
			throw new NegocioException("Ministro não localizado com os argumentos informados!");
		}
		
		return ministro;
	}

	private BigDecimal sumValorToalLancamentos(List<JunoRegLancamentosTO> regList) {
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - sumValorToalLancamentos() ");

		return regList.stream().map(item->item.getVlSaldo()).reduce(BigDecimal.ZERO,BigDecimal::add);
	}

	public List<JunoRegLancamentosTO> agoBuscarLancamentosDeUmRecibo(AgoRecibo agoRecibo) {
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - agoBuscarLancamentosDeUmRecibo( Recibo: " + agoRecibo.getSqRecibo() + ") ");

		return junoPagamentosDAO.agoBuscarLancamentosDeUmRecibo(agoRecibo.getSqRecibo());

	}
	
	public List<AgoRecibo> finAllReciboInscricoesDoMinistro(Long sqMinistro) {
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - finAllReciboInscricoesDoMinistro( sqMinistro: " + sqMinistro + ") ");
		
		return junoPagamentosDAO.finAllReciboInscricoesDoMinistro(sqMinistro);
	}

	public Convencao buscarConvencao(long l) {
		log.info("Ambiente: (" + Uteis.getDsAmbiente() + ") - buscarConvencao( sqConvencao: " + l + ") ");
		return junoPagamentosDAO.findConvencaoPrimayKey(l);
	}
}
