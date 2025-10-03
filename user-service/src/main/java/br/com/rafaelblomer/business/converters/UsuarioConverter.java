package br.com.rafaelblomer.business.converters;

import br.com.rafaelblomer.business.dtos.CadastroUsuarioDTO;
import br.com.rafaelblomer.business.dtos.ResponseUsuarioDTO;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UsuarioConverter {

    public Usuario paraUsuarioEntidade(CadastroUsuarioDTO cadastro) {
        return Usuario.builder()
                .nome(cadastro.nome())
                .cnpj(cadastro.cnpj())
                .email(cadastro.email())
                .telefone(cadastro.telefone())
                .senha(cadastro.senha())
                .build();
    }

    public ResponseUsuarioDTO paraUsuarioResponse(Usuario usuario) {
        return new ResponseUsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getCnpj(),
                new ArrayList<>());
    }
}
