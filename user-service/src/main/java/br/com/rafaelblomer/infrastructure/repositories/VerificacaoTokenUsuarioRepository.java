package br.com.rafaelblomer.infrastructure.repositories;

import br.com.rafaelblomer.infrastructure.entities.VerificacaoTokenUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificacaoTokenUsuarioRepository extends JpaRepository<VerificacaoTokenUsuario, Long> {

    VerificacaoTokenUsuario findByToken(String token);
}
