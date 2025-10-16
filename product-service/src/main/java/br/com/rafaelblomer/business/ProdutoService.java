package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.converters.ProdutoConverter;
import br.com.rafaelblomer.business.dtos.ProdutoAtualizacaoDTO;
import br.com.rafaelblomer.business.dtos.ProdutoCadastroDTO;
import br.com.rafaelblomer.business.dtos.ProdutoResponseDTO;
import br.com.rafaelblomer.business.dtos.UsuarioDTO;
import br.com.rafaelblomer.business.exceptions.AcaoNaoPermitidaException;
import br.com.rafaelblomer.business.exceptions.ObjetoInativoException;
import br.com.rafaelblomer.business.exceptions.ObjetoNaoEncontradoException;
import br.com.rafaelblomer.infrastructure.client.UsuarioClient;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import br.com.rafaelblomer.infrastructure.repositories.ProdutoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private ProdutoConverter converter;

    @Autowired
    private UsuarioClient usuarioClient;


    public ProdutoResponseDTO criarProduto(@Valid ProdutoCadastroDTO cadastroDto, String token) {
        UsuarioDTO usuario = buscarUserToken(token);
        verificarPermissaoEstoqueUsuario(usuario, cadastroDto.idEstoque());
        //Fazer verificação de estoque ativo
        System.out.println(usuario.nome());
        Produto produto = converter.cadastroParaProdutoEntity(cadastroDto);
        repository.save(produto);
        return converter.entityParaResponseDTO(produto, 0);
    }

    public ProdutoResponseDTO atualizarProduto(Long idProduto, ProdutoAtualizacaoDTO dto, String token) {
        Produto produtoAntigo = buscarProdutoEntityId(idProduto);
        verificarProdutoAtivo(produtoAntigo);
        //verificarEstoqueAtivo
        UsuarioDTO usuario = buscarUserToken(token);
        verificarPermissaoProdutoUsuario(usuario, produtoAntigo);
        atualizarDadosProduto(produtoAntigo, dto);
        return converter.entityParaResponseDTO(repository.save(produtoAntigo), 0);
    }

    public ProdutoResponseDTO buscarProdutoPorId(Long produtoId, String token) {
        Produto produto = buscarProdutoEntityId(produtoId);
        UsuarioDTO usuario = buscarUserToken(token);
        verificarPermissaoProdutoUsuario(usuario, produto);
        return converter.entityParaResponseDTO(produto, 0);
    }

    public List<ProdutoResponseDTO> buscarTodosProdutosUsuario(String token) {
        UsuarioDTO usuario = buscarUserToken(token);
        List<Produto> listProductEntity = repository.findByIdEstoqueIn(usuario.idsEstoques());
        return listProductEntity.stream().map(produtoEntity -> converter.entityParaResponseDTO(produtoEntity, 0)).toList();
    }

    public List<ProdutoResponseDTO> buscarTodosProdutosEstoque(Long estoqueId, String token) {
        UsuarioDTO usuario = buscarUserToken(token);
        verificarPermissaoEstoqueUsuario(usuario, estoqueId);
        return repository.findByIdEstoque(estoqueId).stream()
                .map(produtoEntity -> converter.entityParaResponseDTO(produtoEntity, 0))
                .toList();
    }

    public void desativarProduto(Long produtoId, String token) {
        UsuarioDTO usuario = buscarUserToken(token);
        Produto produto = buscarProdutoEntityId(produtoId);
        verificarProdutoAtivo(produto);
        verificarPermissaoProdutoUsuario(usuario, produto);
        produto.setAtivo(false);
        repository.save(produto);
    }

    //MÉTODOS UTILITÁRIOS

    private Produto buscarProdutoEntityId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ObjetoNaoEncontradoException("Produto não encontrado"));
    }

    private void verificarProdutoAtivo(Produto produto) {
        if (!produto.getAtivo())
            throw new ObjetoInativoException("O produto foi desativado.");
    }

    private void verificarPermissaoProdutoUsuario(UsuarioDTO usuario, Produto produto) {
        boolean permitido = usuario.idsEstoques().stream()
                .anyMatch(idEstoque -> idEstoque.equals(produto.getIdEstoque()));
        if (!permitido)
            throw new AcaoNaoPermitidaException("Você não tem permissão para realizar essa ação.");
    }

    private void verificarPermissaoEstoqueUsuario(UsuarioDTO usuario, Long estoqueId) {
        for (Long ids : usuario.idsEstoques()) {
            if (estoqueId.equals(ids))
                return;
        }
        throw new AcaoNaoPermitidaException("Você não tem permissão para realizar essa ação.");
    }

    private void atualizarDadosProduto(Produto antigo, ProdutoAtualizacaoDTO novo) {
        if(novo.nome() != null)
            antigo.setNome(novo.nome());
        if (novo.marca() != null)
            antigo.setMarca(novo.marca());
        if (novo.descricao() != null)
            antigo.setDescricao((novo.descricao()));
    }

    private UsuarioDTO buscarUserToken(String token) {
        return usuarioClient.buscarUsuarioPorToken(token).orElseThrow(() -> new ObjetoNaoEncontradoException("Usuário não encontrado"));
    }

    public void desativarProdutosDeEstoque() {
        //rabbitMQ
    }
}
