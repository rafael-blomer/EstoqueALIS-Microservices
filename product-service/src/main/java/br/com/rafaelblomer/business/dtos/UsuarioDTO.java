package br.com.rafaelblomer.business.dtos;

import java.util.List;

public record UsuarioDTO(Long idUsuario,
                         String nome,
                         String email,
                         String telefone,
                         String cnpj,
                         List<Long> idsEstoques) {
}
