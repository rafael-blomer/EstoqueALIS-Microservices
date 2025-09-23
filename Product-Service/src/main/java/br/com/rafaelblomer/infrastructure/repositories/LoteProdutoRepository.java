package br.com.rafaelblomer.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.rafaelblomer.infrastructure.entities.LoteProduto;

@Repository
public interface LoteProdutoRepository extends JpaRepository<LoteProduto, Long> {

}
