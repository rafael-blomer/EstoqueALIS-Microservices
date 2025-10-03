package br.com.rafaelblomer.controllers;

import br.com.rafaelblomer.business.UsuarioService;
import br.com.rafaelblomer.business.dtos.CadastroUsuarioDTO;
import br.com.rafaelblomer.business.dtos.ResponseUsuarioDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/cadastro")
    public ResponseEntity<ResponseUsuarioDTO> cadastro(@Valid @RequestBody CadastroUsuarioDTO cadastro) {
        return ResponseEntity.ok().body(usuarioService.criarUsuario(cadastro));
    }
}
