package br.com.rafaelblomer.business.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.rafaelblomer.business.dtos.EstoqueResponseDTO;

@FeignClient(name = "inventory-service", path = "/estoques")
public interface EstoqueClient {

    @GetMapping("/usuario/{usuarioId}")
    List<EstoqueResponseDTO> getEstoquesByUsuario(@PathVariable Long usuarioId);
}
