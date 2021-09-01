package br.com.juno.integration.api.confrateres.bo;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.juno.integration.api.confrateres.dao.JunoPagamentosDAO;
import br.com.juno.integration.api.confrateres.exception.NegocioException;
import br.com.juno.integration.api.confrateres.model.Boleto;
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

public class JunoPagamentosBO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	JunoPagamentosDAO junoPagamentosDAO;

	@Inject
	private EntityManager manager;

	private static Log log = LogFactory.getLog(JunoPagamentosBO.class);

	// Buscar ministro
	public Ministro buscarMinistro(Ministro ministro) {
		log.info("buscarMinistro(" + ministro.toString() + ") ");
		return junoPagamentosDAO.findByMinistroJuno(ministro);
	}

	// Salvar Ministro para registrar o Segundo e-mail
	@Transactional
	public void salveMinistro(Ministro ministro) {
		log.info("salvarMinistro(" + ministro.toString() + ") ");
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
		log.info("buscarLancamentosAbertos(sqMinistro = " + sqMinistro + ") ");

		return junoPagamentosDAO.findAllLancamentosAbertos(sqMinistro);
	}


	// Emitir boleto para os registros selecionados pelo ministro
	public br.com.juno.integration.api.model.Charge emitirCobranca(Ministro ministro, List<JunoRegLancamentosTO> lancamentosTOSelecionadosList, JunoParcelamentoTO parcelamentoSelecionado) {	
		log.info("emitirCobranca( INICIO ): " + ministro.toString());

		// Emitir Boleto
		ChargeCreateRequest request = createChargeRequest(ministro, lancamentosTOSelecionadosList, parcelamentoSelecionado);

		List<br.com.juno.integration.api.model.Charge> createdCharges = JunoApiManager.getChargeService().create(request);

		br.com.juno.integration.api.model.Charge createdCharge = createdCharges.get(0);

		//TODO Gravar dados do boleto no banco de dados
		this.salveBoleto(lancamentosTOSelecionadosList, createdCharge);
		

		log.info("emitirCobranca( FIM ): " + createdCharge.toString());

		return createdCharge;
	}


	// Atribuir as configuracoes para classe JunoConfig final
	private void junoConfiguracaoConfrateres(JunoConfiguracao jc) {
		log.info("junoConfiguracaoConfrateres() ");
 
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
		JunoConfig.setQtdeMaximaParcelas(jc.getQtdeMaximaParcelas());

	}

	// Validar AccessToken
	public void validarAccessToken() {
		// Obter a configuracao
		JunoConfiguracao jc = this.findByJunoConfiguracao();

		this.junoConfiguracaoConfrateres(jc);

		// Verificar se o token é válido
		log.info("validarAccessToken() - Verificar se AccessToken é VÁLIDO");
		JunoApiManager.getAuthorizationService().getToken();

		if(!JunoConfig.isFlAccessTokenValido()) {
			JunoConfig.setFlAccessTokenValido(true);
			this.saveConfiguracao(jc);
		} else {
			log.info("validarAccessToken() - AccessToken VÁLIDO ");
		}
	}

	// Buscar configuracoes no Banco de Dados
	private JunoConfiguracao findByJunoConfiguracao() {
		log.info("findByJunoConfiguracao() Para o ambiente (" + Uteis.getDsAmbiente() + ")");
		if(Uteis.getDsAmbiente() == null || (!Uteis.getDsAmbiente().equals("HML") && !Uteis.getDsAmbiente().equals("PRD"))) {
			throw new NegocioException("Arquivo confrateres.properties não identificado com o tipo de ambiente.");
		}
		return junoPagamentosDAO.findByConfiguracao(Uteis.getDsAmbiente());
	}

	// Salvar Configuracao com novos AccessToken e Hora em milessegundos para identificar vencimento do AccessToken
	@Transactional
	public void saveConfiguracao(JunoConfiguracao jc) {
		log.info("saveConfiguracao()");

		EntityTransaction trx = manager.getTransaction();
		try {	
			trx.begin();

			jc.setDsAccessToken(JunoConfig.getDsAccessToken());
			jc.setNnHoraMillessegundosAtualizacaoToken(JunoConfig.getNnHoraMillessegundosAtualizacaoToken());

			jc.setAuditoriaData(Uteis.DataHoje());
			jc.setAuditoriaUsuario("confrater_juno");


			junoPagamentosDAO.salveConfiguracao(jc);

			trx.commit();
			log.info("saveConfiguracao() - COMMIT");
			log.info("saveConfiguracao( HoraMileSegundos Atual   : " + jc.getNnHoraMillessegundosAtualizacaoToken() + " AccessToken Atual   : " + jc.getDsAccessToken() + " )");			
			log.info("saveConfiguracao() - AccessToken Atualizado");

		} catch (Exception e) {
			trx.rollback();
			log.info("saveConfiguracao() - ROLLBACK");
			log.info("saveConfiguracao() - ERRO ao tentar atualizar AccessToken");
			throw e;
		}
	}


	// Preparar boleto para registrar na Juno
	private static ChargeCreateRequest createChargeRequest(Ministro ministro, List<JunoRegLancamentosTO> lancamentosTOSelecionadosList, JunoParcelamentoTO parcelamentoSelecionado) {
		log.info("ChargeCreateRequest() - Preparando boleto");

		StringBuilder dsLactos = new StringBuilder();
		int nnControle = 0;
		for (JunoRegLancamentosTO lc : lancamentosTOSelecionadosList) {
			if(nnControle > 0) 
				dsLactos.append(" \n");

			nnControle ++;

			dsLactos.append(lc.getDsLancamentoTipo());
			dsLactos.append(" R$");
			dsLactos.append(lc.getVlSaldo());
		}

		ChargeCreateRequest.Charge charge = new Charge(dsLactos.toString());
		charge.setAmount(parcelamentoSelecionado.getVlParcela());
		charge.setDueDate(Uteis.AdcionarDiasNaData(Uteis.DataHoje().toLocalDate(), 5)); 
		charge.setInstallments(parcelamentoSelecionado.getQtdeParcelas());

		ChargeCreateRequest.Billing billing = new Billing();
		billing.setName(ministro.getCdCodigo() + " - " + ministro.getNmNome());
		billing.setDocument(ministro.getDsCpf());
		billing.setEmail(ministro.getDsEmail());
		billing.setSecondaryEmail(ministro.getDsSegundoEmail());
		billing.setNotify(true);
		ChargeCreateRequest.Billing.Address address = new Address();
		billing.setAddress(address);

		ChargeCreateRequest request = new ChargeCreateRequest(charge, billing);
		return request;
	}
	
	// Salvar Boleto obtido da Juno para os registros selecionados pelo Ministro
	@Transactional
	private void salveBoleto(List<JunoRegLancamentosTO> regList, br.com.juno.integration.api.model.Charge charge) {
		log.info("salveBoleto() - INICIO");
		
		EntityTransaction trx = manager.getTransaction();
		
		try {
			trx.begin();
			
			// Verificar se trata de inclusao ou alteracao de Boleto
			Boleto boleto;
			for (JunoRegLancamentosTO reg : regList) {
				// Definido reg.getSqRegLancamento() = 0, quando é para incluir somente Inscrição OUTROS
				if(reg.getSqRegLancamento() != 0) {
					boleto = junoPagamentosDAO.findBoletoPorRegLancamento(reg.getSqRegLancamento());
					if(boleto != null) {
						log.info("salveBoleto() - Alterar boleto existente para o RegLancamento: " + reg.getSqRegLancamento());
						
						boleto.setDsUrl(charge.getLink());
						boleto.getRegLancamento().setSqRegLancamento(reg.getSqRegLancamento());
						junoPagamentosDAO.saveBoleto(boleto);
					} else {
						log.info("salveBoleto() - Registrar novo boleto para o RegLancamento: " + reg.getSqRegLancamento());
						
						boleto = new Boleto();
						boleto.setRegLancamento(new RegLancamento());
						
						boleto.setDsUrl(charge.getLink());
						boleto.getRegLancamento().setSqRegLancamento(reg.getSqRegLancamento());
						junoPagamentosDAO.saveBoleto(boleto);
					}
				} else {
					log.info("salveBoleto() - Registrar novo boleto somente para INSCRICAO de OUTROS: " + reg.getSqRegLancamento());
					
					boleto = new Boleto();
					boleto.setDsUrl(charge.getLink());
					boleto.setRegLancamento(null);
					junoPagamentosDAO.saveBoleto(boleto);
				}
			}
			
			trx.commit();
			log.info("salveBoleto() - COMMIT");
			
		}catch (Exception e) {
			trx.rollback();
			log.info("saveConfiguracao() - ROLLBACK");
			throw new NegocioException("Falha ao tentar registrar boleto na base de dados da CONFRATERES. Mas o boleto foi emitido: Consulte seu e-mail!");
		}
		log.info("salveBoleto() - FIM");
	}
}
