package br.com.rafaelblomer.business.exceptions;

public class VerficacaoEmailException extends RuntimeException {
	private static final long serialVersionUID = -5300545755469363769L;

	public VerficacaoEmailException(String msg) {
		super(msg);
	}
}
