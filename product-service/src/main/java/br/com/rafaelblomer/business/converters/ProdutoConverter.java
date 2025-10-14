package br.com.rafaelblomer.business.converters;

import br.com.rafaelblomer.business.dtos.ProdutoCadastroDTO;
import br.com.rafaelblomer.business.dtos.ProdutoResponseDTO;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ProdutoConverter {


    public Produto cadastroParaProdutoEntity(ProdutoCadastroDTO dto) {
        return Produto.builder()
                .nome(dto.nome())
                .marca(dto.marca())
                .descricao(dto.descricao())
                .idEstoque(dto.idEstoque())
                .idLotes(new ArrayList<>())
                .ativo(true)
                .build();
    }

    public ProdutoResponseDTO entityParaResponseDTO(Produto produto, Integer quantidadeTotal) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getMarca(),
                produto.getDescricao(),
                quantidadeTotal,
                produto.getIdEstoque());
    }
}
