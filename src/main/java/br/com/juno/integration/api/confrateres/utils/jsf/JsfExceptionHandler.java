package br.com.juno.integration.api.confrateres.utils.jsf;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.juno.integration.api.confrateres.exception.NegocioException;

public class JsfExceptionHandler extends ExceptionHandlerWrapper{

	private ExceptionHandler wrapped;
	private static Log log = LogFactory.getLog(JsfExceptionHandler.class);
	
	@SuppressWarnings("deprecation")
	public JsfExceptionHandler(ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}
	
	@Override
	public ExceptionHandler getWrapped() {
		return this.wrapped;
	}
	
	@Override
	public void handle() throws FacesException {
		Iterator<ExceptionQueuedEvent> events = getUnhandledExceptionQueuedEvents().iterator();
		boolean erroTratado = false;
		
		while(events.hasNext()){
			ExceptionQueuedEvent event = events.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
			
			Throwable exception = context.getException();
			NegocioException negocioException = getNegocioException(exception);
			
			log.error("Erro: (" + exception.getMessage() + ") \n", exception);
			
			try{
				if(exception instanceof ViewExpiredException){
					erroTratado = true;
					redirect("/");
				}else if(negocioException != null){
					erroTratado = true;
				//	FacesUtil.addErrorMessage(negocioException.getMessage());
				}else{
					erroTratado = true;
					redirect("/erro.xhtml");
				}
			}finally{
				if(erroTratado){
					events.remove();
				}
			}
		}
		
		getWrapped().handle();
	}
	
	private NegocioException getNegocioException(Throwable exception){
		if(exception instanceof NegocioException){
			return (NegocioException) exception;
		}else if(exception.getCause() != null){
			return getNegocioException(exception.getCause());
		}
		
		return null;
	}
	
	private void redirect(String page){
		try{
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			String contextPath = externalContext.getRequestContextPath();
			
			externalContext.redirect(contextPath + page);
			facesContext.responseComplete();
		}catch (IOException e){
			throw new FacesException(e);
		}
	}

}