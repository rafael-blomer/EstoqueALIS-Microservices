package br.com.rafaelblomer.business;

import java.util.ArrayList;
import java.util.List;

import br.com.rafaelblomer.business.clients.InventoryClient;
import br.com.rafaelblomer.business.exceptions.ObjetoInativoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.rafaelblomer.business.clients.UsuarioClient;
import br.com.rafaelblomer.business.converters.ProdutoConverter;
import br.com.rafaelblomer.business.dtos.EstoqueResponseDTO;
import br.com.rafaelblomer.business.dtos.ProdutoAtualizacaoDTO;
import br.com.rafaelblomer.business.dtos.ProdutoCadastroDTO;
import br.com.rafaelblomer.business.dtos.ProdutoResponseDTO;
import br.com.rafaelblomer.business.dtos.UsuarioResponseDTO;
import br.com.rafaelblomer.business.exceptions.AcaoNaoPermitidaException;
import br.com.rafaelblomer.business.exceptions.DadoIrregularException;
import br.com.rafaelblomer.business.exceptions.ObjetoNaoEncontradoException;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import br.com.rafaelblomer.infrastructure.repositories.ProdutoRepository;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private InventoryClient inventoryClient;

    @Autowired
    private ProdutoConverter converter;

    /**
     * Cria um novo produto vinculado a um estoque do usuário autenticado.
     *
     * @param dto   DTO com os dados para cadastro do produto
     * @param token Token JWT para identificar o usuário
     * @return ProdutoResponseDTO com os dados do produto criado
     * Fluxo:
     * - Converte o DTO em entidade Produto
     * - Busca e valida o estoque informado
     * - Verifica se o estoque pertence ao usuário
     * - Salva o produto no repositório
     * TODO: lembrar de fazer serviço de estoque retornar apenas lotes ativos
     * TODO: fazer serviço de relatorio trazer quandtidade total de produto
     */
    @Transactional
    public ProdutoResponseDTO criarProduto(ProdutoCadastroDTO dto, String token) {
        UsuarioResponseDTO usuario = buscarUsuario(token);
        EstoqueResponseDTO estoque = buscarEstoque(dto.idEstoque());
        verificarPermissaoEstoqueUsuario(estoque, usuario);
        Produto produto = converter.cadastroParaProdutoEntity(dto);
        repository.save(produto);
        return converter.entityParaResponseDTO(produto);
    }

    /**
     * Atualiza os dados de um produto existente.
     *
     * @param idProduto ID do produto a ser atualizado
     * @param dto       DTO com os novos dados do produto
     * @param token     Token JWT para identificar o usuário
     * @return ProdutoResponseDTO atualizado
     * Fluxo:
     * - Busca o produto pelo ID
     * - Verifica se está ativo
     * - Valida se o usuário tem permissão no estoque vinculado
     * - Aplica as alterações informadas
     * - Salva no repositório
     */
    @Transactional
    public ProdutoResponseDTO atualizarProduto(Long idProduto, ProdutoAtualizacaoDTO dto, String token) {
        UsuarioResponseDTO usuario = buscarUsuario(token);
        Produto antigo = buscarProdutoId(idProduto);
        EstoqueResponseDTO estoque = buscarEstoque(antigo.getIdEstoque());
        verificarProdutoAtivo(antigo);
        verificarPermissaoEstoqueUsuario(estoque, usuario);
        verificarPermissaoProdutoUsuario(usuario, antigo);
        atualizarDadosProduto(antigo, dto);
        return converter.entityParaResponseDTO(repository.save(antigo));
    }

    /**
     * Busca um produto específico pelo seu ID.
     *
     * @param id    ID do produto
     * @param token Token JWT para identificar o usuário
     * @return ProdutoResponseDTO encontrado
     * Apenas produtos vinculados a estoques do usuário podem ser acessados.
     */
    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarProdutoPorId(Long id, String token) {
        Produto produto = buscarProdutoId(id);
        UsuarioResponseDTO usuario = buscarUsuario(token);
        verificarPermissaoProdutoUsuario(usuario, produto);
        return converter.entityParaResponseDTO(produto);
    }

    /**
     * Retorna todos os produtos ativos pertencentes a todos os estoques do usuário autenticado.
     *
     * @param token Token JWT para identificar o usuário
     * @return Lista de ProdutoResponseDTO
     * Apenas produtos ativos e vinculados a estoques ativos serão retornados.
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarTodosProdutosUsuario(String token) {
        UsuarioResponseDTO usuario = buscarUsuario(token);
        List<Long> idsEstoques = usuario.estoques().stream()
                .filter(EstoqueResponseDTO::ativo)
                .map(EstoqueResponseDTO::estoqueId)
                .toList();
        return repository.findByIdEstoqueIn(idsEstoques).stream()
                .filter(Produto::getAtivo)
                .map(converter::entityParaResponseDTO)
                .toList();
    }

    /**
     * Retorna todos os produtos ativos de um estoque específico,
     * desde que o usuário autenticado tenha acesso a esse estoque.
     *
     * @param estoqueId ID do estoque
     * @param token     Token JWT para identificar o usuário
     * @return Lista de ProdutoResponseDTO
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarTodosProdutosEstoque(Long estoqueId, String token) {
        UsuarioResponseDTO usuario = buscarUsuario(token);
        EstoqueResponseDTO estoque = buscarEstoque(estoqueId);
        verificarPermissaoEstoqueUsuario(estoque, usuario);
        return repository.findByEstoqueId(estoqueId)
                .stream()
                .filter(Produto::getAtivo)
                .map(p -> converter.entityParaResponseDTO(p))
                .toList();
    }

    /**
     * Desativa um produto.
     *
     * @param id    ID do produto a ser desativado
     * @param token Token JWT para identificar o usuário
     *              - Apenas produtos ativos podem ser desativados.
     *              - O usuário precisa ter acesso ao estoque do produto.
     */
    @Transactional
    public void desativarProduto(Long id, String token) {
        UsuarioResponseDTO usuario = buscarUsuario(token);
        Produto produto = buscarProdutoId(id);
        verificarProdutoAtivo(produto);
        verificarPermissaoProdutoUsuario(usuario, produto);
        produto.setAtivo(false);
        repository.save(produto);
    }

    //ÚTEIS

    /**
     * Busca um produto pelo ID.
     *
     * @param id ID do produto
     * @return Entidade Produto encontrada
     * @throws ObjetoNaoEncontradoException se o produto não for encontrado
     */
    public Produto buscarProdutoId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ObjetoNaoEncontradoException("Produto não encontrado"));
    }

    /**
     * Verifica se o produto está ativo.
     *
     * @param produto Produto a ser verificado
     * @throws DadoIrregularException se o produto estiver desativado
     */
    public void verificarProdutoAtivo(Produto produto) {
        if (!produto.getAtivo())
            throw new DadoIrregularException("O produto foi desativado.");
    }

    /**
     * Verifica se o usuário possui permissão para acessar/alterar o produto informado.
     *
     * @param usuario Usuário autenticado
     * @param produto Produto alvo da ação
     * @throws AcaoNaoPermitidaException se o produto não pertencer a um estoque do usuário
     */
    public void verificarPermissaoProdutoUsuario(UsuarioResponseDTO usuario, Produto produto) {
        boolean permitido = usuario.estoques().stream()
                .anyMatch(estoque -> estoque.estoqueId().equals(produto.getIdEstoque()));
        if (!permitido)
            throw new AcaoNaoPermitidaException("Você não tem permissão para realizar essa ação.");
    }

    /**
     * Verifica se o usuário possui permissão sobre o estoque informado.
     *
     * @param estoque = Estoque teoricamente criado por usuário
     * @param usuario = Usuário que teoricamente é dono do estoque
     * @throws AcaoNaoPermitidaException se o estoque não estiver vinculado ao usuário
     */
    private void verificarPermissaoEstoqueUsuario(EstoqueResponseDTO estoque, UsuarioResponseDTO usuario) {
        if (!estoque.usuarioId().equals(usuario.id()))
            throw new AcaoNaoPermitidaException("Usuário não pode acionar esse estoque.");
    }

    /**
     * Atualiza os dados de um produto existente com as informações de atualização.
     *
     * @param antigo Produto já existente
     * @param novo   DTO com os novos dados
     * Apenas os campos não nulos no DTO serão atualizados
     */
    private void atualizarDadosProduto(Produto antigo, ProdutoAtualizacaoDTO novo) {
        if (novo.nome() != null)
            antigo.setNome(novo.nome());
        if (novo.marca() != null)
            antigo.setMarca(novo.marca());
        if (novo.descricao() != null)
            antigo.setDescricao((novo.descricao()));
    }

    /**
     * Busca um Usuário no UsuarioClient
     *
     * @param token token de sessão do usuário
     * @return usuário encontrado
     */
    private UsuarioResponseDTO buscarUsuario(String token) {
        return usuarioClient.buscarPorToken(token);
    }

    /**
     * Busca um estoque no InventoryClient
     *
     * @param estoqueId id do estoque procurado
     * @return estoque encontrado
     */
    private EstoqueResponseDTO buscarEstoque(Long estoqueId) {
        return inventoryClient.buscarEstoquePorId(estoqueId);
    }
}
