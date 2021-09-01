package br.com.juno.integration.api.confrateres.utils.jsf;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

public class JsfExceptionHandleFactory extends ExceptionHandlerFactory{

	private ExceptionHandlerFactory parent;
	
	@SuppressWarnings("deprecation")
	public JsfExceptionHandleFactory( ExceptionHandlerFactory parent) {
		this.parent = parent;
	}
	
	@Override
	public ExceptionHandler getExceptionHandler() {
		return new JsfExceptionHandler(parent.getExceptionHandler());
	}

}
