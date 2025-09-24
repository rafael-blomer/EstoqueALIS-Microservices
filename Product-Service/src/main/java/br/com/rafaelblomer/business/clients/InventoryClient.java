package br.com.rafaelblomer.business.clients;

import br.com.rafaelblomer.business.dtos.EstoqueResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "estoque-service", url = "http://localhost:8082/estoque")
public interface InventoryClient {

    @GetMapping("/{id}")
    EstoqueResponseDTO buscarEstoquePorId(@PathVariable Long idEstoque);
}
