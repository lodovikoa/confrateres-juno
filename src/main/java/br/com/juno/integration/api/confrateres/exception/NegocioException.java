package br.com.juno.integration.api.confrateres.exception;

public class NegocioException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NegocioException(String msg) {
		super(msg);
	}
	
	public NegocioException(String msg, Throwable e) {
		super(msg, e);
	}
}