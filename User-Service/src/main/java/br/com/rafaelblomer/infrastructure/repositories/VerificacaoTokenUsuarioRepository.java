package br.com.rafaelblomer.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.rafaelblomer.infrastructure.entities.VerificacaoTokenUsuario;

@Repository
public interface VerificacaoTokenUsuarioRepository extends JpaRepository<VerificacaoTokenUsuario, Long> {

    VerificacaoTokenUsuario findByToken(String token);
}
