package br.com.rafaelblomer.business;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.rafaelblomer.business.client.EstoqueClient;
import br.com.rafaelblomer.business.converters.UsuarioConverter;
import br.com.rafaelblomer.business.dtos.EstoqueResponseDTO;
import br.com.rafaelblomer.business.dtos.UsuarioAtualizacaoDTO;
import br.com.rafaelblomer.business.dtos.UsuarioCadastroDTO;
import br.com.rafaelblomer.business.dtos.UsuarioLoginDTO;
import br.com.rafaelblomer.business.dtos.UsuarioResponseDTO;
import br.com.rafaelblomer.business.dtos.events.TokenVerificacaoEvent;
import br.com.rafaelblomer.business.exceptions.DadoIrregularException;
import br.com.rafaelblomer.business.exceptions.ObjetoInativoException;
import br.com.rafaelblomer.business.exceptions.ObjetoNaoEncontradoException;
import br.com.rafaelblomer.business.exceptions.VerficacaoEmailException;
import br.com.rafaelblomer.infrastructure.config.RabbitMQConfig;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import br.com.rafaelblomer.infrastructure.entities.VerificacaoTokenUsuario;
import br.com.rafaelblomer.infrastructure.repositories.UsuarioRepository;
import br.com.rafaelblomer.infrastructure.repositories.VerificacaoTokenUsuarioRepository;
import br.com.rafaelblomer.infrastructure.security.JwtUtil;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioConverter converter;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private EstoqueClient estoqueClient;

    @Autowired
    private VerificacaoTokenUsuarioRepository tokenRepository;

    /**
     * Cria um novo usuário, criptografa sua senha, gera um token de verificação
     * e envia e-mail de confirmação.
     * @param entityCadastro DTO com dados do usuário a ser cadastrado.
     * @return DTO de resposta com dados do usuário criado.
     * TODO: configurar RABBITMQ no serviço de email
     */
    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioCadastroDTO entityCadastro) {
        Usuario entity = converter.dtoCadastroParaEntity(entityCadastro);
        entity.setSenha(encoder.encode(entity.getSenha()));
        entity = repository.save(entity);
        String tokenString = UUID.randomUUID().toString();
        VerificacaoTokenUsuario verificacaoTokenUsuario = new VerificacaoTokenUsuario(tokenString, entity);
        tokenRepository.save(verificacaoTokenUsuario);
        TokenVerificacaoEvent event = new TokenVerificacaoEvent(tokenString, entity.getId(), entity.getEmail());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_TOKEN, event);
        return converter.entityParaResponseDTO(entity, buscarEstoqueDTO(entity.getId()));
    }


    /**
     * Atualiza os dados de um usuário autenticado via token.
     * @param token JWT que identifica o usuário autenticado.
     * @param novo DTO com os novos dados (nome, telefone, etc).
     * @return DTO de resposta com os dados atualizados.
     */
    @Transactional
    public UsuarioResponseDTO atualizarUsuario(String token, UsuarioAtualizacaoDTO novo) {
        Usuario antigo = findByToken(token);
        validarTelefone(antigo.getId(), novo.telefone());
        atualizarDadosUsuario(antigo, novo);
        repository.save(antigo);
        return converter.entityParaResponseDTO(antigo, buscarEstoqueDTO(antigo.getId()));
    }

    /**
     * Desativa um usuário autenticado via token.
     * @param token JWT que identifica o usuário autenticado.
     */
    @Transactional
    public void alterarStatusAtivoUsuario(String token) {
        Usuario entity = findByToken(token);
        entity.setAtivo(false);
        repository.save(entity);
    }

    /**
     * Realiza o login de um usuário, verificando credenciais e gerando um JWT.
     * @param dto DTO contendo e-mail e senha do usuário.
     * @return Token JWT no formato "Bearer ...".
     */
    @Transactional
    public String realizarLogin(UsuarioLoginDTO dto) {
        Usuario usuario = buscarPorEmail(dto.email());
        verificarUsuarioAtivo(usuario);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.senha()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtUtil.generateToken(authentication.getName());
        return "Bearer " + jwtToken;
    }


    /**
     * Busca os dados de um usuário autenticado via token.
     * @param token JWT que identifica o usuário autenticado.
     * @return DTO com os dados do usuário.
     */
    public UsuarioResponseDTO buscarUsuarioDTOToken(String token) {
        Usuario usuario = findByToken(token);
        return converter.entityParaResponseDTO(usuario, buscarEstoqueDTO(usuario.getId()));
    }


    /**
     * Valida o token de verificação de e-mail e ativa o usuário.
     * @param token String do token de verificação recebido por e-mail.
     * @return "valido" se o token for aceito.
     */
    public String verificarUsuario(String token) {
        VerificacaoTokenUsuario verificationToken = tokenRepository.findByToken(token);
        verificarTokenEmail(verificationToken);
        Usuario usuario = verificationToken.getUser();
        usuario.setAtivo(true);
        repository.save(usuario);
        tokenRepository.delete(verificationToken);
        return "valido";
    }


    /**
     * Inicia o processo de redefinição de senha.
     * Gera um token temporário e envia por e-mail.
     * @param email E-mail do usuário.
     * @return Mensagem de confirmação do envio.
     * TODO: configurar RABBITMQ no serviço de email
     */
    public String esqueciSenha(String email) {
        Usuario usuario = buscarPorEmail(email);
        String token = UUID.randomUUID().toString();
        VerificacaoTokenUsuario verificacaoTokenUsuario = new VerificacaoTokenUsuario(token, usuario);
        tokenRepository.save(verificacaoTokenUsuario);
        TokenVerificacaoEvent event = new TokenVerificacaoEvent(token, usuario.getId(), usuario.getEmail());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_TOKEN, event);
        return "Email para alteração de senha enviado.";
    }


    /**
     * Altera a senha de um usuário após validação de token de recuperação.
     * @param token Token de recuperação de senha.
     * @param senhaNova Nova senha a ser definida (será criptografada).
     * @return Mensagem de sucesso.
     */
    public String alterarSenha (String token, String senhaNova) {
        VerificacaoTokenUsuario verificationToken = tokenRepository.findByToken(token);
        verificarTokenEmail(verificationToken);
        Usuario usuario = verificationToken.getUser();
        usuario.setSenha(encoder.encode(senhaNova));
        repository.save(usuario);
        tokenRepository.delete(verificationToken);
        return "Senha alterada com sucesso.";
    }

    //ÚTEIS

    /**
     * Busca um usuário pelo e-mail.
     * @throws ObjetoNaoEncontradoException se o e-mail não estiver cadastrado.
     */
    private Usuario buscarPorEmail(String email) {
        return repository.findByEmail(email).orElseThrow(
                () -> new ObjetoNaoEncontradoException("Email não cadastrado. Tente novamente"));
    }

    /**
     * Verifica se o token de verificação é válido e não expirou.
     * @param verificationToken Token a ser validado.
     * @throws VerficacaoEmailException se o token for inválido ou expirado.
     */
    private void verificarTokenEmail(VerificacaoTokenUsuario verificationToken) {
        if (verificationToken == null)
            throw new VerficacaoEmailException("O token de verificação está inválido");
        else if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new VerficacaoEmailException("O token expirou.");
    }

    /**
     * Busca um usuário autenticado através do token JWT.
     * @param token JWT do usuário.
     * @return Usuário correspondente ao token.
     * @throws UsernameNotFoundException se o e-mail não existir.
     */
    public Usuario findByToken(String token) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        return repository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }

    /**
     * Atualiza os dados de um usuário com base nas informações recebidas.
     */
    private void atualizarDadosUsuario(Usuario antigo, UsuarioAtualizacaoDTO novo) {
        if(novo.nome() != null)
            antigo.setNome(novo.nome());
        if (novo.telefone() != null)
            antigo.setTelefone(novo.telefone());
    }

    /**
     * Valida se o usuário está ativo.
     * @throws ObjetoInativoException se o usuário estiver inativo.
     */
    private void verificarUsuarioAtivo(Usuario entity) {
        if(!entity.getAtivo())
            throw new ObjetoInativoException("O usuário não está ativo.");
    }

    /**
     * Verifica se o telefone informado já está sendo usado por outro usuário.
     * @param id ID do usuário atual.
     * @param telefone Telefone a validar.
     * @throws DadoIrregularException se o telefone já estiver cadastrado por outro usuário.
     */
    private void validarTelefone(Long id, String telefone) {
        if(repository.existsByTelefoneAndIdNot(telefone, id))
            throw new DadoIrregularException("Número de telefone irregular. Tente outro.");
    }
    
    /**
     * Busca os estoques do usuário no Inventory-service
     * @param idUsuario = id do usuário
     * @return retorna a lista dos estoques do usuário
     * TODO: fazer retornar apenas os estoques ativos
     */
    private List<EstoqueResponseDTO> buscarEstoqueDTO(Long idUsuario) {
    	return estoqueClient.getEstoquesByUsuario(idUsuario);
    }
}
