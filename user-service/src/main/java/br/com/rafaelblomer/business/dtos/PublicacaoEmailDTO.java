package br.com.rafaelblomer.business.dtos;

public record PublicacaoEmailDTO(String emailDestino,
                                 String nomeUsuario,
                                 String token) {
}
