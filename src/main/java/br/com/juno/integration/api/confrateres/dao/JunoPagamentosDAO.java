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

import br.com.juno.integration.api.confrateres.model.Boleto;
import br.com.juno.integration.api.confrateres.model.JunoConfiguracao;
import br.com.juno.integration.api.confrateres.model.JunoRegLancamentosTO;
import br.com.juno.integration.api.confrateres.model.Ministro;

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
}
