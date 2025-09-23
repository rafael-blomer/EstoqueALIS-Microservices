package br.com.rafaelblomer.business.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoteProdutoCadastroDTO(@NotNull Long produtoId,
                                     @NotNull Integer quantidadeLote,
                                     @NotNull LocalDate dataValidade,
                                     @NotNull @Size(max = 65) String loteFabricante) {
}
