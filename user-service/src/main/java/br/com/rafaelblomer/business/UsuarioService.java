package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.converters.UsuarioConverter;
import br.com.rafaelblomer.business.dtos.CadastroUsuarioDTO;
import br.com.rafaelblomer.business.dtos.ResponseUsuarioDTO;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import br.com.rafaelblomer.infrastructure.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioConverter usuarioConverter;

    public ResponseUsuarioDTO criarUsuario(@Valid CadastroUsuarioDTO cadastro) {
        Usuario usuario = usuarioConverter.paraUsuarioEntidade(cadastro);
        return usuarioConverter.paraUsuarioResponse(usuarioRepository.save(usuario));
    }
}
