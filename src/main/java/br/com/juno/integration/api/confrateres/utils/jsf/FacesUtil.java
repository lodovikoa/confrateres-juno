package br.com.juno.integration.api.confrateres.utils.jsf;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.PrimeFaces;

public class FacesUtil {

	public static void addErrorMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, 
				new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
	}

	public static void addInfoMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, 
				new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
	}

	public static String obterParametro(String param){
		return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(param);
	}

	public static boolean isPostback(){
		return FacesContext.getCurrentInstance().isPostback();
	}

	public static boolean isNotPostback(){
		return !isPostback();
	}


	public static ExternalContext getExternalContext() {
		return getFacesContext().getExternalContext();
	}

	public static FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	public static HttpServletRequest getHttpServletRequest() {
		return ((HttpServletRequest) getExternalContext().getRequest());	
	}

	public static HttpServletResponse getHttpServletResponse() {
		return ((HttpServletResponse) getExternalContext().getResponse());	
	}	

	public static PrimeFaces getRequestContext() {
		return PrimeFaces.current();
	}

}
