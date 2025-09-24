package br.com.rafaelblomer.business.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import br.com.rafaelblomer.business.dtos.UsuarioResponseDTO;

@FeignClient(name = "user-service", url = "http://localhost:8081/usuario")
public interface UsuarioClient {
		
	@GetMapping
	UsuarioResponseDTO buscarPorToken(@RequestHeader("Authorization") String token);
}
