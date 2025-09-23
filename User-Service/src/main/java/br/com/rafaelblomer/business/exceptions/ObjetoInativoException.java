package br.com.rafaelblomer.business.exceptions;

public class ObjetoInativoException extends RuntimeException {
	private static final long serialVersionUID = 7289765479238597654L;

	public ObjetoInativoException(String msg) {
		super(msg);
	}
}
