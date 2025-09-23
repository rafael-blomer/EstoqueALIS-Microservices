package br.com.rafaelblomer.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.rafaelblomer.infrastructure.entities.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

}
