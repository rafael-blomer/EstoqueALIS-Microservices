package br.com.rafaelblomer.business.converters;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.rafaelblomer.business.dtos.EstoqueResponseDTO;
import br.com.rafaelblomer.business.dtos.UsuarioCadastroDTO;
import br.com.rafaelblomer.business.dtos.UsuarioResponseDTO;
import br.com.rafaelblomer.infrastructure.entities.Usuario;

@Component
public class UsuarioConverter {

    public Usuario dtoCadastroParaEntity(UsuarioCadastroDTO entityCadastro) {
        return new Usuario(
                entityCadastro.nome(),
                entityCadastro.email(),
                entityCadastro.cnpj(),
                entityCadastro.telefone(),
                entityCadastro.senha());
    }

    public UsuarioResponseDTO entityParaResponseDTO(Usuario entity, List<EstoqueResponseDTO> list ) {
        return new UsuarioResponseDTO(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getTelefone(),
                entity.getCnpj(),
                list);
    }
}
