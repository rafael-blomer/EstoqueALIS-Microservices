package br.com.rafaelblomer.business.converters;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import br.com.rafaelblomer.business.dtos.ProdutoCadastroDTO;
import br.com.rafaelblomer.business.dtos.ProdutoResponseDTO;
import br.com.rafaelblomer.infrastructure.entities.Produto;

@Component
public class ProdutoConverter {

    public Produto cadastroParaProdutoEntity(ProdutoCadastroDTO dto) {
        return new Produto(dto.nome(), dto.marca(), dto.descricao(), 0, new ArrayList<>(), dto.idEstoque());
    }

    public ProdutoResponseDTO entityParaResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getMarca(),
                produto.getDescricao(),
                produto.getQuantidadeTotal(),
                produto.getIdEstoque());
    }
}
