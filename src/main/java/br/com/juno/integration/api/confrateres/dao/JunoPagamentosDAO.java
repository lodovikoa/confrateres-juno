package br.com.juno.integration.api.confrateres.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import br.com.juno.integration.api.confrateres.model.Ago;
import br.com.juno.integration.api.confrateres.model.AgoFormaPagamento;
import br.com.juno.integration.api.confrateres.model.AgoInscrito;
import br.com.juno.integration.api.confrateres.model.AgoRecibo;
import br.com.juno.integration.api.confrateres.model.AgoValorInscricao;
import br.com.juno.integration.api.confrateres.model.Boleto;
import br.com.juno.integration.api.confrateres.model.Convencao;
import br.com.juno.integration.api.confrateres.model.JunoConfiguracao;
import br.com.juno.integration.api.confrateres.model.JunoRegLancamentosTO;
import br.com.juno.integration.api.confrateres.model.Ministro;
import br.com.juno.integration.api.confrateres.utils.AgoInscricaoOuAnuidade;

public class JunoPagamentosDAO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	// Buscar Configuracoes
	public JunoConfiguracao findByConfiguracao(String dsAmbiente) {
		JunoConfiguracao configuracao = null;
		try{			
			configuracao =  manager.createQuery("from JunoConfiguracao d where d.dsAmbiente = :dsAmbiente", JunoConfiguracao.class)
					.setParameter("dsAmbiente", dsAmbiente)
					.getSingleResult();
			return configuracao;

		}catch(NoResultException e){
			return configuracao;
		}
	}

	// Buscar formas de pagamento
	public List<AgoFormaPagamento> findAllFormasPagamento() {
		try {
			List<AgoFormaPagamento> formasPagamento = new ArrayList<AgoFormaPagamento>();

			formasPagamento =  manager.createQuery("from AgoFormaPagamento p order by p.cdOrdem", AgoFormaPagamento.class)
					.getResultList();

			return formasPagamento;

		} catch (NoResultException e) {
			return null;
		}
	}

	// Buscar Ministro
	public Ministro findByMinistroJuno(Ministro ministroParam) {
		try {
			Ministro ministro = null;

			// Preparar para buscar o nome informado do ministro em qualquer parte do nome e em qualquer ordem informada
			String nmNomeTemp[] = ministroParam.getNmNome().split(" ");

			StringBuilder strNome = new StringBuilder();
			for(int i = 0; i < nmNomeTemp.length; i++){
				if(nmNomeTemp[i].trim().length() > 0){
					strNome.append(" and");
					strNome.append(" ministro.nmNome like ");
					strNome.append("'%" +nmNomeTemp[i].trim() + "%'");
				}
			}

			// Buscar ministro no banco de dados
			StringBuilder jpql = new StringBuilder();

			jpql.append("from Ministro ministro ")
			.append("left join fetch ministro.cargo ")
			.append("left join fetch ministro.igreja ")
			.append("lett join fetch ministro.estado ")
			.append("where ministro.cdCodigo = :codigoMinistro and ministro.dsCpf = :cpfMinistro and ministro.dtNascimento = :dtNascimentoMinistro ")
			.append(strNome.toString());

			ministro = manager.createQuery(jpql.toString(), Ministro.class)
					.setParameter("codigoMinistro", ministroParam.getCdCodigo())
					.setParameter("cpfMinistro", ministroParam.getDsCpf())
					.setParameter("dtNascimentoMinistro", ministroParam.getDtNascimento())
					.getSingleResult();



			return ministro;

		} catch(NoResultException e) {
			return null;
		}
	}

	// Buscar Lancamentos em aberto de um ministro
	public List<JunoRegLancamentosTO> findAllLancamentosAbertos(Long sqMinistro) {

		StringBuilder sql = new StringBuilder();
		sql.append("select rgl.rgl_sq_reglancamento as sqRegLancamento ") 
		.append(",rgl.rgl_dt_vencimento as dtVencimento ") 
		.append(",rgl.rgl_vl_lancamento as vlLancamento ")
		.append(",if(vlPagamentos.vlTotalPago is null, rgl.rgl_vl_lancamento, (rgl.rgl_vl_lancamento - vlPagamentos.vlTotalPago) ) as vlSaldo ")
		.append(",tpl.tpl_ds_tipolancamento as dsLancamentoTipo ")
		.append(",ifnull(bol.bol_ds_url, '') as dsUrl ")
		.append(",ifnull(agr.agr_ds_forma_pagamento, '') as dsFormaPagamento ")
		.append("from tb_rgl_reglancamento rgl ")
		.append("left join( ") 
		.append("select sum(lan.lan_vl_pagamento) as vlTotalPago, lan.rgl_sq_reglancamento as sqRegLancamento ") 
		.append("from tb_lan_lancamento lan ")
		.append("left join tb_rcb_recibo rcb on rcb.rcb_sq_recibo = lan.rcb_sq_recibo ")
		.append("where rcb.rcb_dt_cancelado is null group by sqRegLancamento) as vlPagamentos on (sqRegLancamento = rgl.rgl_sq_reglancamento) ")
		.append("left join tb_min_ministro min on rgl.min_sq_ministro = min.min_sq_ministro ")
		.append("left join tb_tpl_tipolancamento tpl on rgl.tpl_sq_tipolancamento = tpl.tpl_sq_tipolancamento ")
		.append("left join tb_dpt_departamento dpt on min.dpt_sq_departamento = dpt.dpt_sq_departamento ")
		.append("left join tb_bol_boleto bol on bol.rgl_sq_reglancamento = rgl.rgl_sq_reglancamento ")
		.append("left join tb_agr_ago_recibo agr on agr.agr_sq_recibo = bol.agr_sq_recibo ")
		.append("where ((rgl.rgl_vl_lancamento - vlPagamentos.vlTotalPago) is null or	(rgl.rgl_vl_lancamento - vlPagamentos.vlTotalPago) >= 0.01) ")
		.append("and rgl.rgl_dt_cancelado is null ")
		.append("and min.min_dt_excluido is null ")
		.append("and min.dpt_sq_departamento = 1 ")
		.append("and rgl.min_sq_ministro = :sqMinistro");

		// Fazer consulta de lançamentos pendentes
		@SuppressWarnings("unchecked")
		List<Object[]> resultObject = manager.createNativeQuery(sql.toString())
		.setParameter("sqMinistro", sqMinistro)
		.getResultList();

		// Criar lista vazia para armazenar retorno da consulta
		List<JunoRegLancamentosTO> regLancamentosTO = new ArrayList<JunoRegLancamentosTO>();

		// Popular a lista que será retornada
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		for(Object ob[] : resultObject) {
			JunoRegLancamentosTO regDTO = new JunoRegLancamentosTO();

			regDTO.setSqRegLancamento(Long.parseLong(ob[0].toString()));
			regDTO.setDtVencimento(LocalDate.parse(ob[1].toString(), formatter));
			regDTO.setVlLancamento(new BigDecimal(ob[2].toString()));
			regDTO.setVlSaldo(new BigDecimal(ob[3].toString()));
			regDTO.setDsLancamentoTipo(ob[4].toString());
			regDTO.setDsUrl(ob[5].toString());
			regDTO.setDsFormaPagamento(ob[6].toString());

			regLancamentosTO.add(regDTO);
		}

		return regLancamentosTO;
	}

	public JunoConfiguracao salveConfiguracao(JunoConfiguracao jc) {
		try {
			return manager.merge(jc);
		}catch(Exception e){
			throw e;
		}
	}

	public Ministro saveMinistro(Ministro ministro) {
		try{
			return manager.merge(ministro);
		}catch(Exception e){
			throw e;
		}
	}

	public Boleto saveBoleto(Boleto boleto) {
		try {
			return manager.merge(boleto);
		}catch(Exception e) {
			throw e;
		}

	}

	public Boleto findBoletoPorRegLancamento(Long sqRegLancamento) {
		Boleto boleto = null;
		try{			
			boleto =  manager.createQuery("from Boleto d where d.regLancamento.sqRegLancamento = :sqRegLancamento", Boleto.class)
					.setParameter("sqRegLancamento", sqRegLancamento)
					.getSingleResult();
			return boleto;

		}catch(NoResultException e){
			return boleto;
		}
	}

	// Implementacões para AGO
	// Buscar anuidades em aberto de um ministro
	public List<JunoRegLancamentosTO> agoAnuidadesAbertos(Long sqMinistro) {
		try {
			// Cria variável para armazenar a consulta SQL
			StringBuilder sql = new StringBuilder();
			sql.append("select rgl.rgl_sq_reglancamento as sqRegLancamento ") 
			.append(",rgl.rgl_dt_vencimento as dtVencimento ") 
			.append(",rgl.rgl_vl_lancamento as vlLancamento ")
			.append(",if(vlPagamentos.vlTotalPago is null, rgl.rgl_vl_lancamento, (rgl.rgl_vl_lancamento - vlPagamentos.vlTotalPago) ) as vlSaldo ")
			.append(",tpl.tpl_ds_tipolancamento as dsLancamentoTipo ")
			.append(",ifnull(bol.bol_ds_url, '') as dsUrl ")
			.append(", agr.agr_ds_forma_pagamento as dsFormaPagamento ")
			.append("from tb_rgl_reglancamento rgl ")
			.append("left join( ") 
			.append("select sum(lan.lan_vl_pagamento) as vlTotalPago, lan.rgl_sq_reglancamento as sqRegLancamento ") 
			.append("from tb_lan_lancamento lan ")
			.append("left join tb_rcb_recibo rcb on rcb.rcb_sq_recibo = lan.rcb_sq_recibo ")
			.append("where rcb.rcb_dt_cancelado is null group by sqRegLancamento) as vlPagamentos on (sqRegLancamento = rgl.rgl_sq_reglancamento) ")
			.append("left join tb_min_ministro min on rgl.min_sq_ministro = min.min_sq_ministro ")
			.append("left join tb_tpl_tipolancamento tpl on rgl.tpl_sq_tipolancamento = tpl.tpl_sq_tipolancamento ")
			.append("left join tb_dpt_departamento dpt on min.dpt_sq_departamento = dpt.dpt_sq_departamento ")
			.append("left join tb_bol_boleto bol on bol.rgl_sq_reglancamento = rgl.rgl_sq_reglancamento ")
			.append("left join tb_plc_plano_contas plc on plc.plc_sq_plano_contas = tpl.plc_sq_plano_contas ")
			.append("left join tb_agr_ago_recibo agr on agr.agr_sq_recibo = bol.agr_sq_recibo ")
			.append("where ((rgl.rgl_vl_lancamento - vlPagamentos.vlTotalPago) is null or	(rgl.rgl_vl_lancamento - vlPagamentos.vlTotalPago) >= 0.01) ")
			.append("and rgl.rgl_dt_cancelado is null ")
			.append("and min.min_dt_excluido is null ")
			.append("and plc.plc_sq_plano_contas = 1 ") // Somente anuidades
			.append("and tpl.tpl_sq_tipolancamento != 14 ") // Desconsiderar parcelamento de anuidades
			.append("and min.dpt_sq_departamento = 1 ")
			.append("and rgl.min_sq_ministro = :sqMinistro");

			// Fazer consulta de lançamentos pendentes
			@SuppressWarnings("unchecked")
			List<Object[]> resultObject = manager.createNativeQuery(sql.toString())
			.setParameter("sqMinistro", sqMinistro)
			.getResultList();

			// Criar lista vazia para armazenar retorno da consulta
			List<JunoRegLancamentosTO> regLancamentosTO = new ArrayList<JunoRegLancamentosTO>();

			// Popular a lista que será retornada
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			for(Object ob[] : resultObject) {
				JunoRegLancamentosTO regDTO = new JunoRegLancamentosTO();

				regDTO.setSqRegLancamento(Long.parseLong(ob[0].toString()));
				regDTO.setDtVencimento(LocalDate.parse(ob[1].toString(), formatter));
				regDTO.setVlLancamento(new BigDecimal(ob[2].toString()));
				regDTO.setVlSaldo(new BigDecimal(ob[3].toString()));
				regDTO.setDsLancamentoTipo(ob[4].toString());
				regDTO.setDsUrl(ob[5].toString());
				regDTO.setDsFormaPagamento(ob[6] != null? ob[6].toString(): "");

				regLancamentosTO.add(regDTO);
			}

			return regLancamentosTO;
		}catch (NoResultException e) {
			return null;
		}
	}

	public BigDecimal agoValorInscricao(String dsTipo) {
		try {
			// dsTipo='01' é para Ministros da Confrateres - dsTipo='02' é para Esposas de Ministros e outros 
			StringBuilder sql = new StringBuilder();
			sql.append("select agi.agi_vl_valor_inscricao from tb_agi_ago_valor_inscricao agi ")
			.append("left join tb_aga_ago aga on aga.aga_sq_ago = agi.aga_sq_ago and aga.aga_fl_ativo is true ")
			.append("where CURDATE() between agi.agi_dt_periodo_inicio and agi.agi_dt_periodo_fim ")
			.append("and agi.agi_cd_tipo = :dsTipo");

			// Fazer consulta de lançamentos pendentes
			Object resultObject = manager.createNativeQuery(sql.toString())
					.setParameter("dsTipo", dsTipo)
					.getSingleResult();

			BigDecimal result = new BigDecimal(resultObject.toString());
			return result;

		} catch (NoResultException e) {
			return null;
		}
	}

	public Ago agoAtiva() {
		Ago ago = null;
		try {
			ago =  manager.createQuery("from Ago d where d.flAtivo is true", Ago.class)
					.getSingleResult();

			return ago;

		} catch(NoResultException e) {
			return ago;
		}
	}
	
	// Buscar todos os registros que estão ativos. Só deveria haver um registro AGO ativo
	public List<Ago> agoAtivas() {
		List<Ago> agoList = new ArrayList<Ago>(); 
		try {
			agoList = manager.createQuery("from Ago d where d.flAtivo is true", Ago.class)
					.getResultList();
			return agoList;
		} catch(NoResultException e) {
			return agoList;
		}
	}
	
	// Buscar registros de valores de inscricão que estejam ativos, ou seja, que a data fim seja igual ou superior a hoje+
	public List<AgoValorInscricao> agoValorInscricaoAtivos(Ago ago) {
		List<AgoValorInscricao> valorInscriaoList = new ArrayList<AgoValorInscricao>();
		try {
			valorInscriaoList = manager.createQuery("from AgoValorInscricao d where d.ago = :ago and dtPeriodoFim >= CURDATE()", AgoValorInscricao.class)
					.setParameter("ago", ago)
					.getResultList();
			
			return valorInscriaoList;
			
		} catch(NoResultException e) {
			return valorInscriaoList;
		}
	}

	public List<Ministro> agoMinistroOutros(Ministro agoMinistroOutrosTemp) {
		try {

			boolean flWhere = false;
			boolean flAnd = false;

			// Preparar para buscar o nome informado do ministro em qualquer parte do nome e em qualquer ordem informada
			String nmNomeTemp[] = agoMinistroOutrosTemp.getNmNome().split(" ");

			StringBuilder strNome = new StringBuilder();
			for(int i = 0; i < nmNomeTemp.length; i++){
				if(nmNomeTemp[i].trim().length() > 0){
					if(flAnd) strNome.append(" and ");
					strNome.append(" ministro.nmNome like ");
					strNome.append("'%" +nmNomeTemp[i].trim() + "%'");
					flAnd = true;
				}
			}

			// Criar lista vazia para armazenar retorno da consulta
			List<Ministro> ministros = new ArrayList<Ministro>();

			// Buscar ministro no banco de dados
			StringBuilder jpql = new StringBuilder();

			jpql.append("from Ministro ministro ")
			.append("left join fetch ministro.cargo ")
			.append("left join fetch ministro.igreja ")
			.append("lett join fetch ministro.estado ");

			if(agoMinistroOutrosTemp.getCdCodigo() != null && agoMinistroOutrosTemp.getCdCodigo() > 0) {
				jpql.append("where ministro.cdCodigo = " + agoMinistroOutrosTemp.getCdCodigo());
				flWhere = true;
			}

			if(agoMinistroOutrosTemp.getNmNome().trim() != null && agoMinistroOutrosTemp.getNmNome().trim().length() > 3) {
				if(!flWhere) {
					jpql.append(" where ");
					flWhere = true;
				} else {
					jpql.append(" and ");
				}

				jpql.append(strNome.toString());

			}

			// Só faz a consulta no banco se houver a clausua Where no sql
			if(flWhere) {
				ministros = manager.createQuery(jpql.toString(), Ministro.class)
						.getResultList();
			}

			return ministros;

		} catch(NoResultException e) {
			return null;
		}
	}
	
	// Salvar Ago
	public Ago agoSaveAgo(Ago ago) {
		try {
			return manager.merge(ago);
			
		}catch(Exception e) {
			throw e;
		}
	}

	// Salvar recibo de Inscricao
	public AgoRecibo agoSaveRecibo(AgoRecibo agoRecibo) {
		try{
			return manager.merge(agoRecibo); 
		}catch(Exception e){
			throw e;
		}
	}
	
	// Salvar Inscricoes solicitadas
	public AgoInscrito agoSaveInscricaoSolicitada(AgoInscrito agoInscrito) {
		try {
			return manager.merge(agoInscrito);
		}catch(Exception e) {
			throw e;
		}
	}
	
	// Buscar Ministro pela PrimayKey
	public Ministro findMinistroPrimaryKey(Long id) {
		try {
			return manager.find(Ministro.class, id);
		}catch(NoResultException e) {
			return null;
		}
	}

	public List<JunoRegLancamentosTO> agoBuscarLancamentosDeUmRecibo(Long sqRecibo) {
		try {
			// Buscar Lancamentos AGO pelo Recibo
			StringBuilder sql = new StringBuilder();
			sql.append("select agt.agt_ds_descricao_lancamento as dsDescricaoTipoLancamento, agt.agt_vl_valor_lancamento as vlLancamento, agt.agt_vl_valor_saldo_devedor as vlSaldoDevedor, agt.agt_ds_insc_anuidade as dsInscAnuidade, agr.agr_ds_url as dsUrl, agr.agr_ds_forma_pagamento as dsFormaPagamento ")
				.append("from tb_agr_ago_recibo agr ")
				.append("left join tb_agt_ago_inscrito agt on agt.agr_sq_recibo = agr.agr_sq_recibo ")
				.append("where agt.agr_sq_recibo = :sqRecibo");
			
			
			// Fazer a consulta no BD
			@SuppressWarnings("unchecked")
			List<Object[]> resultObject = manager.createNativeQuery(sql.toString())
			.setParameter("sqRecibo", sqRecibo)
			.getResultList();
			
			// Criar lista vazia para armazenar retorno da consulta
			List<JunoRegLancamentosTO> regLancamentosTO = new ArrayList<JunoRegLancamentosTO>();

			// Popular a lista que será retornada
			for(Object ob[] : resultObject) {
				JunoRegLancamentosTO regDTO = new JunoRegLancamentosTO();

				regDTO.setDsLancamentoTipo(ob[0].toString());
				regDTO.setVlLancamento(new BigDecimal(ob[1].toString()));
				regDTO.setVlSaldo(new BigDecimal(ob[2].toString()));
				regDTO.setAgoInscricaoOuAnuidade(AgoInscricaoOuAnuidade.valueOf(ob[3].toString()));
				regDTO.setDsUrl(ob[4].toString());
				regDTO.setDsFormaPagamento(ob[5].toString());
				
				regLancamentosTO.add(regDTO);
			}
			
			return regLancamentosTO;
			
		} catch(NoResultException e) {
			return null;
		}
	}
	
	// Listar recibos de inscricão realizadas por um Ministro
	public List<AgoRecibo> finAllReciboInscricoesDoMinistro(Long sqMinistro) {
		try {
			List<AgoRecibo> agoRecibo;
			agoRecibo =  manager.createQuery("from AgoRecibo d where d.ministro.sqMinistro = :sqMinistro", AgoRecibo.class)
					.setParameter("sqMinistro", sqMinistro)
					.getResultList();
			
			return agoRecibo;
			
		}catch(NoResultException e) {
			return null;
		}
	}

	public Convencao findConvencaoPrimayKey(long l) {
		Convencao convencao = null;
		try {
			convencao =  manager.createQuery("from Convencao d where d.sqConvencao = :sqConvencao", Convencao.class)
					.setParameter("sqConvencao", l)
					.getSingleResult();

			return convencao;
			
		} catch(NoResultException e ) {
			return convencao;
		}
	}

}
