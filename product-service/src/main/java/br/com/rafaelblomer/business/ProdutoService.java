package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.converters.ProdutoConverter;
import br.com.rafaelblomer.business.dtos.ProdutoAtualizacaoDTO;
import br.com.rafaelblomer.business.dtos.ProdutoCadastroDTO;
import br.com.rafaelblomer.business.dtos.ProdutoResponseDTO;
import br.com.rafaelblomer.business.exceptions.ObjetoInativoException;
import br.com.rafaelblomer.business.exceptions.ObjetoNaoEncontradoException;
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


    public ProdutoResponseDTO criarProduto(@Valid ProdutoCadastroDTO dto, String token) {
        return null;
    }

    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoAtualizacaoDTO dto, String token) {
        return null;
    }

    public ProdutoResponseDTO buscarProdutoPorId(Long id, String token) {
        return null;
    }

    public List<ProdutoResponseDTO> buscarTodosProdutosUsuario(String token) {
        return null;
    }

    public List<ProdutoResponseDTO> buscarTodosProdutosEstoque(Long id, String token) {
        return null;
    }

    public void desativarProduto(Long id, String token) {
    }

    //MÉTODOS UTILITÁRIOS

    private Produto buscarProtudoEntityId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ObjetoNaoEncontradoException("Produto não encontrado"));
    }

    private void verificarProdutoAtivo(Produto produto) {
        if (!produto.getAtivo())
            throw new ObjetoInativoException("O produto foi desativado.");
    }

    private void verificarPermissaoProdutoUsuario() {

    }

    private void verificarPermissaoEstoqueUsuario() {

    }

    private void atualizarDadosProduto(Produto antigo, ProdutoAtualizacaoDTO novo) {
        if(novo.nome() != null)
            antigo.setNome(novo.nome());
        if (novo.marca() != null)
            antigo.setMarca(novo.marca());
        if (novo.descricao() != null)
            antigo.setDescricao((novo.descricao()));
    }

    public void desativarProdutosDeEstoque() {
        //rabbitMQ
    }
}
