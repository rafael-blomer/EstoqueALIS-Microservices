package br.com.rafaelblomer.business.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroUsuarioDTO(@NotBlank String nome,
                                 @NotBlank @Email String email,
                                 @NotBlank @Size(min = 14, max = 14) String cnpj,
                                 @NotBlank @Size(min = 11, max = 13) String telefone,
                                 @NotBlank @Size(min = 10) String senha) {
}
