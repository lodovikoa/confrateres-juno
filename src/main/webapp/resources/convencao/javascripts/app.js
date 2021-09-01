$(document).ready(function(){
	
	$('.js-toggle').bind('click', function(){
		$('.js-sidebar').toggleClass('is-toggled');
		$('.js-content').toggleClass('is-toggled');
	});
	
});

PrimeFaces.locales['pt'] = {  
        closeText: 'Fechar',  
        prevText: 'Anterior',  
        nextText: 'Próximo',  
        currentText: 'Começo',  
        monthNames: ['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'],  
        monthNamesShort: ['Jan','Fev','Mar','Abr','Mai','Jun', 'Jul','Ago','Set','Out','Nov','Dez'],  
        dayNames: ['Domingo','Segunda','Terça','Quarta','Quinta','Sexta','Sábado'],  
        dayNamesShort: ['Dom','Seg','Ter','Qua','Qui','Sex','Sáb'],  
        dayNamesMin: ['D','S','T','Q','Q','S','S'],  
        weekHeader: 'Semana',  
        firstDay: 0,  
        isRTL: false,  
        showMonthAfterYear: false,  
        yearSuffix: '',  
        timeOnlyTitle: 'Só Horas',  
        timeText: 'Tempo',  
        hourText: 'Hora',  
        minuteText: 'Minuto',  
        secondText: 'Segundo',  
        currentText: 'Hoje',  
        ampm: false,  
        month: 'Mês',  
        week: 'Semana',  
        day: 'Dia',  
        allDayText : 'Todo Dia'  
    };

function jsFecharDialogo(xhr, status, args, widgetVar, widgetEfeito) {
    if(args.validationFailed || !args.loggedIn) {
        PF(widgetVar).jq.effect(widgetEfeito, {times:5}, 100);
    }
    else {
        PF(widgetVar).hide();
        $('#loginLink').fadeOut();
    }
}	

function configurarMoeda(){
	$(".moeda").maskMoney({ decimal: ",", thousands: ".", allowZero: true});
}

$(document).ready(function(){
	configurarMoeda();
});

// Tratamento para importar anexos
/*
$(function(){
	var settings = {
			type: 'json',
			filelimit: 1,
			allow: '*.(jpg!jpeg!png)',
			action:'/confrateres/fotos'
	};
	UIkit.uploadSelect($("#upload-select"), settings),
	UIkit.uploadDrop($("#upload-drop"), settings);
});
*/

// Tratamento para relatórios

function relatorioMinistroGeral(nmRelatorio, tpRelatorio, sqRegiao, sqIgreja, sqDepartamento, sqCargo, cdSituacao, nnOrdem) {
	url = nmRelatorio + "?tpRelatorio=" + tpRelatorio + "&sqRegiao=" + sqRegiao + "&sqIgreja=" + sqIgreja + "&sqDepartamento=" + sqDepartamento + "&sqCargo=" + sqCargo + "&cdSituacao=" + cdSituacao + "&nnOrdem=" + nnOrdem;
	abrirRelatorio(url);
}

function relatorioMinistroPrPresidente(nmRelatorio, tpRelatorio, sqRegiao, cdSituacao) {
	url = nmRelatorio + "?tpRelatorio=" + tpRelatorio + "&sqRegiao=" + sqRegiao + "&cdSituacao=" + cdSituacao;
	abrirRelatorio(url);
}
         
function relatorioFinanceiroRecibo(nmRelatorio, tpRelatorio, sqRecibo, tpRecibo) {
	url = nmRelatorio + "?tpRelatorio=" + tpRelatorio + "&sqRecibo=" + sqRecibo + "&tpRecibo=" + tpRecibo;
	abrirRelatorio(url);
}

function relatorioFinanceiroResumo(nmRelatorio, tpRelatorio, sqResumo, dsPeriodo, dsRegiao, dsDataFechado) {
	url = nmRelatorio + "?tpRelatorio=" + tpRelatorio + "&sqResumo=" + sqResumo + "&dsPeriodo=" + dsPeriodo + "&dsRegiao=" + dsRegiao + "&dtFechado=" + dsDataFechado;
	abrirRelatorio(url);
}

// Relatorio Entradas por periodo
function relatorioFinanceiroEntradaPeriodo(nmRelatorio, tpRelatorio, sqResumo, dsPeriodo, dsDataFechado, dsRegiao, sqPlanoConta, dsConta, cdOrdem) {
	url = nmRelatorio + "?tpRelatorio=" + tpRelatorio + "&sqResumo=" + sqResumo + "&dsPeriodo=" + dsPeriodo + "&dsDataFechado=" + dsDataFechado + "&dsRegiao=" + dsRegiao + "&sqPlanoConta=" + sqPlanoConta + "&dsConta=" + dsConta + "&cdOrdem=" + cdOrdem;
	abrirRelatorio(url);
}

//Relatorio Entradas por Ministro
function relatorioFinanceiroEntradaMinistro(nmRelatorio, tpRelatorio, sqRegiaoFinanceiro, dsRegiaoFinanceiro, sqRegiaoMinistro, dsRegiaoMinistro, sqDepartamento, dsDepartamento, sqIgreja, dsIgreja, sqCargo, dsCargo, sqMinistro, nmNome, sqPlanoConta, dsConta, dsTipoLancamentosSelecionados, dtInicio, dtFim) {	
	url = nmRelatorio + "?tpRelatorio=" + tpRelatorio + "&sqRegiaoFinanceiro=" + sqRegiaoFinanceiro + "&dsRegiaoFinanceiro=" + dsRegiaoFinanceiro + "&sqRegiaoMinistro=" + sqRegiaoMinistro + "&dsRegiaoMinistro=" + dsRegiaoMinistro + "&sqDepartamento=" + sqDepartamento + "&dsDepartamento=" + dsDepartamento + "&sqIgreja=" + sqIgreja + "&dsIgreja=" + dsIgreja + "&sqCargo=" + sqCargo + "&dsCargo=" + dsCargo + "&sqMinistro=" + sqMinistro + "&dsContribuinte=" + nmNome + "&sqPlanoConta=" + sqPlanoConta + "&dsConta=" + dsConta + "&dsTpLanSelecionados=" + dsTipoLancamentosSelecionados +  "&dtInicio=" + dtInicio + "&dtFim=" + dtFim;
	abrirRelatorio(url);
}

//Relatorio Entradas por Recibo
function relatorioFinanceiroEntradaRecibo(nmRelatorio, tpRelatorio, sqDepartamento, dsDepartamento, sqRegiao, dsRegiao, dtInicio, dtFim, cdOrdem) {
	url = nmRelatorio + "?tpRelatorio=" + tpRelatorio + "&sqDepartamento=" + sqDepartamento + "&dsDepartamento=" + dsDepartamento + "&sqRegiao=" + sqRegiao + "&dsRegiao=" + dsRegiao + "&dtInicio=" + dtInicio + "&dtFim=" + dtFim + "&cdOrdem=" + cdOrdem;
	abrirRelatorio(url);
}

//Relatorio Entradas Pendentes
function relatorioFinanceiroEntradaPendente(nmRelatorio,tpRelatorio, sqDepartamento, dsDepartamento, sqRegiao, dsRegiao, sqIgreja, dsIgreja, sqCargo, dsCargo, sqMinistro, nmMinistro, sqPlanoConta, dsPlanoConta, dsTipoLancamentosSelecionados, cdSituacao) {
	url = nmRelatorio + "?tpRelatorio=" + tpRelatorio + "&sqDepartamento=" + sqDepartamento + "&dsDepartamento=" + dsDepartamento + "&sqRegiao=" + sqRegiao + "&dsRegiao=" + dsRegiao + "&sqIgreja=" + sqIgreja + "&dsIgreja=" + dsIgreja + "&sqCargo=" + sqCargo + "&dsCargo=" + dsCargo + "&sqMinistro=" + sqMinistro + "&nmMinistro=" + nmMinistro + "&sqPlanoConta=" + sqPlanoConta + "&dsPlanoConta=" + dsPlanoConta + "&dsTpLanSelecionados=" + dsTipoLancamentosSelecionados + "&cdSituacao=" + cdSituacao;
	abrirRelatorio(url);
}

//Relatorio Entradas por Forma de Recebimento
function relatorioFinanceiroEntradaFormaRecebimento(nmRelatorio,tpRelatorio, sqDepartamento, dsDepartamento, sqRegiaoRecebimento, dsRegiaoRecebimento, sqRegiaoMinistro, dsRegiaoMinistro, sqIgreja, dsIgreja, sqCargo, dsCargo, sqMinistro, nmMinistro, dtInicio, dtFim, cdOrdem) {
	url = nmRelatorio + "?tpRelatorio=" + tpRelatorio + "&sqDepartamento=" + sqDepartamento + "&dsDepartamento=" + dsDepartamento + "&sqRegiaoRecebimento=" + sqRegiaoRecebimento + "&dsRegiaoRecebimento=" + dsRegiaoRecebimento + "&sqRegiaoMinistro=" + sqRegiaoMinistro + "&dsRegiaoMinistro=" + dsRegiaoMinistro + "&sqIgreja=" + sqIgreja + "&dsIgreja=" + dsIgreja + "&sqCargo=" + sqCargo + "&dsCargo=" + dsCargo + "&sqMinistro=" + sqMinistro + "&nmMinistro=" + nmMinistro + "&dtInicio=" + dtInicio + "&dtFim=" + dtFim + "&cdOrdem=" + cdOrdem;
	abrirRelatorio(url);
}

//Relatorio Saidas
function relatorioFinanceiroSaidaPeriodo(nmRelatorio, tpRelatorio, sqRegiao, dsRegiao, sqResumo, dsPeriodo, dsDtFechado, sqPlanoConta, dsConta, cdOrdem) {
	url = nmRelatorio + "?tpRelatorio=" + tpRelatorio + "&sqRegiao=" + sqRegiao + "&dsRegiao=" + dsRegiao + "&sqResumo=" + sqResumo + "&dsPeriodo=" + dsPeriodo + "&dsDtFechado=" + dsDtFechado + "&sqPlanoConta=" + sqPlanoConta + "&dsConta=" + dsConta + "&cdOrdem=" + cdOrdem;
	abrirRelatorio(url);
}

function abrirRelatorio(url) {	
	window.open(url,'popup','left=60,top=20,width=0,height=0,toolbar=no,status=no,location=no');
}
