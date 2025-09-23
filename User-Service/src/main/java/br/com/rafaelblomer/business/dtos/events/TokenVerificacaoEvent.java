package br.com.rafaelblomer.business.dtos.events;

public record TokenVerificacaoEvent(String token, Long usuarioId, String email) { 
	
}
