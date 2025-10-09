package br.com.rafaelblomer.infrastructure.repositories;

import br.com.rafaelblomer.infrastructure.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
<<<<<<< HEAD
=======

    Boolean existsByTelefoneAndIdNot(String telefone, Long id);
>>>>>>> 5c426d3ca33de295667cddbec306d2859cad08ad
}
