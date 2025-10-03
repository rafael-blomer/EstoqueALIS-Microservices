package br.com.rafaelblomer.business.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginUsuarioDTO(@NotBlank String email,
                              @NotBlank @Size(min = 10) String senha) {
}
