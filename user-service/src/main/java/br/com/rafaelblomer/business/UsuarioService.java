package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.converters.UsuarioConverter;
import br.com.rafaelblomer.business.dtos.AtualizacaoUsuarioDTO;
import br.com.rafaelblomer.business.dtos.CadastroUsuarioDTO;
import br.com.rafaelblomer.business.dtos.LoginUsuarioDTO;
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

    public String logarUsuario(LoginUsuarioDTO loginUsuarioDTO) {
        return null;
    }

    public ResponseUsuarioDTO atualizarUsuario(@Valid AtualizacaoUsuarioDTO atualizacaoUsuarioDTO, String token) {
        return null;
    }

    public void desativarUsuario(String token) {

    }

    public ResponseUsuarioDTO buscarUsuarioDTOPorToken(String token) {
        return null;
    }

    public String UsuarioEsqueceuSenha(String email) {
        return null;
    }

    public String alterarSenhaUsuario(String token, String novaSenha) {
        return null;
    }

    //MÉTODOS UTILITÁRIOS

    private Usuario buscarUsuarioEntityPorToken(String token) {
        return null;
    }

    private void verificarTokenEmail () {

    }

    private Usuario buscarUsuarioPorEmail(String email) {
        return null;
    }

    private void verificarUsuarioAtivo(Usuario usuario) {

    }

    private void verificarTelefoneUsuario(Long id, String telefone) {

    }

    private void AtualizarDadosUsuario(Usuario usuarioAntigo, AtualizacaoUsuarioDTO atualizacaoUsuarioDTO) {
        if(atualizacaoUsuarioDTO.nome() != null)
            usuarioAntigo.setNome(atualizacaoUsuarioDTO.nome());
        if (atualizacaoUsuarioDTO.telefone() != null)
            usuarioAntigo.setTelefone(atualizacaoUsuarioDTO.telefone());
    }
}
