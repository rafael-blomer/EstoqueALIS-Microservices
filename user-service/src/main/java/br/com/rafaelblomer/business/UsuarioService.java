package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.converters.UsuarioConverter;
import br.com.rafaelblomer.business.dtos.AtualizacaoUsuarioDTO;
import br.com.rafaelblomer.business.dtos.CadastroUsuarioDTO;
import br.com.rafaelblomer.business.dtos.LoginUsuarioDTO;
import br.com.rafaelblomer.business.dtos.ResponseUsuarioDTO;
import br.com.rafaelblomer.business.exceptions.DadoIrregularException;
import br.com.rafaelblomer.business.exceptions.ObjetoInativoException;
import br.com.rafaelblomer.business.exceptions.ObjetoNaoEncontradoException;
import br.com.rafaelblomer.business.exceptions.VerficacaoEmailException;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import br.com.rafaelblomer.infrastructure.entities.VerificacaoTokenUsuario;
import br.com.rafaelblomer.infrastructure.repositories.UsuarioRepository;
import br.com.rafaelblomer.infrastructure.repositories.VerificacaoTokenUsuarioRepository;
import br.com.rafaelblomer.infrastructure.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioConverter usuarioConverter;

    @Autowired
    private VerificacaoTokenUsuarioRepository verificacaoRepository;

    @Autowired
    private VerificacaoTokenUsuarioRepository tokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder encoder;

    public ResponseUsuarioDTO criarUsuario(CadastroUsuarioDTO cadastro) {
        Usuario usuario = usuarioConverter.paraUsuarioEntidade(cadastro);
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
        String tokenString = UUID.randomUUID().toString();
        VerificacaoTokenUsuario verificacaoTokenUsuario = VerificacaoTokenUsuario.criarComExpiracao(tokenString, usuario);
        tokenRepository.save(verificacaoTokenUsuario);
        //Disparar mensagem RabbitMQ
        return usuarioConverter.paraUsuarioResponse(usuario);
    }

    public String logarUsuario(LoginUsuarioDTO loginUsuarioDTO) {
        return null;
    }

    public ResponseUsuarioDTO atualizarUsuario(AtualizacaoUsuarioDTO atualizacaoUsuarioDTO, String token) {
        Usuario antigo = buscarUsuarioEntityPorToken(token);
        verificarTelefoneUsuario(antigo.getId(), atualizacaoUsuarioDTO.telefone());
        AtualizarDadosUsuario(antigo, atualizacaoUsuarioDTO);
        usuarioRepository.save(antigo);
        return usuarioConverter.paraUsuarioResponse(antigo);
    }

    public void desativarUsuario(String token) {
        Usuario entity = buscarUsuarioEntityPorToken(token);
        entity.setAtivo(false);
        //Disparar mensagem RabbitMQ
        usuarioRepository.save(entity);
    }

    public ResponseUsuarioDTO buscarUsuarioDTOPorToken(String token) {
        return usuarioConverter.paraUsuarioResponse(buscarUsuarioEntityPorToken(token));
    }

    public String verificarEmailUsuario(String token) {
        return null;
    }

    public String UsuarioEsqueceuSenha(String email) {
        //Disparar mensagem RabbitMQ

        return null;
    }

    public String alterarSenhaUsuario(String token, String novaSenha) {
        return null;
    }

    //MÉTODOS UTILITÁRIOS

    private Usuario buscarUsuarioEntityPorToken(String token) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }

    private void verificarTokenEmail (VerificacaoTokenUsuario verificationToken) {
        if (verificationToken == null)
            throw new VerficacaoEmailException("O token de verificação está inválido");
        else if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new VerficacaoEmailException("O token expirou.");
    }

    private Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ObjetoNaoEncontradoException("Email não cadastrado. Tente novamente"));
    }

    private void verificarUsuarioAtivo(Usuario usuario) {
        if(!usuario.getAtivo())
            throw new ObjetoInativoException("O usuário não está ativo.");
    }

    private void verificarTelefoneUsuario(Long id, String telefone) {
        if(usuarioRepository.existsByTelefoneAndIdNot(telefone, id))
            throw new DadoIrregularException("Número de telefone irregular. Tente outro.");
    }

    private void AtualizarDadosUsuario(Usuario usuarioAntigo, AtualizacaoUsuarioDTO atualizacaoUsuarioDTO) {
        if(atualizacaoUsuarioDTO.nome() != null)
            usuarioAntigo.setNome(atualizacaoUsuarioDTO.nome());
        if (atualizacaoUsuarioDTO.telefone() != null)
            usuarioAntigo.setTelefone(atualizacaoUsuarioDTO.telefone());
    }
}
