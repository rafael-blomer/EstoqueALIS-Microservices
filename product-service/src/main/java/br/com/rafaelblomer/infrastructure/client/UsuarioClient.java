package br.com.rafaelblomer.infrastructure.client;

import br.com.rafaelblomer.business.dtos.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UsuarioClient {

    @GetMapping("/usuario")
    Optional<UsuarioDTO> buscarUsuarioPorToken(@RequestHeader("Authorization") String token);
}
