package br.com.rafaelblomer.business.dtos;

import java.util.List;

public record ResponseUsuarioDTO(Long idUsuario,
                                 String nome,
                                 String email,
                                 String telefone,
                                 String cnpj,
                                 List<Integer> idsEstoques) {
}
