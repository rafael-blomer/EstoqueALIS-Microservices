package br.com.rafaelblomer.infrastructure.entities;

import br.com.rafaelblomer.infrastructure.entities.enums.TipoMovimentacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class MovimentacaoEstoque {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Estoque estoque;
    private LocalDateTime dataHora;
    private TipoMovimentacao tipoMov;
    private List<ItemMovimentacaoLote> itensMovimentacao;

}
