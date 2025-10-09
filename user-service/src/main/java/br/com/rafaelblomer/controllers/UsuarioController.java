package br.com.rafaelblomer.controllers;

import br.com.rafaelblomer.business.UsuarioService;
import br.com.rafaelblomer.business.dtos.AtualizacaoUsuarioDTO;
import br.com.rafaelblomer.business.dtos.CadastroUsuarioDTO;
import br.com.rafaelblomer.business.dtos.LoginUsuarioDTO;
import br.com.rafaelblomer.business.dtos.ResponseUsuarioDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/cadastro")
    public ResponseEntity<ResponseUsuarioDTO> cadastroUsuario(@Valid @RequestBody CadastroUsuarioDTO cadastro) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criarUsuario(cadastro));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginUsuarioDTO loginUsuarioDTO) {
        return ResponseEntity.ok().body(usuarioService.logarUsuario(loginUsuarioDTO));
    }

    @GetMapping("/usuario")
    public ResponseEntity<ResponseUsuarioDTO> buscarUsuarioPorToken(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(usuarioService.buscarUsuarioDTOPorToken(token));
    }

    @PatchMapping("/desativar")
    public ResponseEntity<Void> desativarUsuario(@RequestHeader("Authorization") String token) {
        usuarioService.desativarUsuario(token);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/atualizar")
    public ResponseEntity<ResponseUsuarioDTO> atualizarDadosUsuario(@RequestHeader("Authorization") String token,
                                                                    @Valid @RequestBody AtualizacaoUsuarioDTO atualizacaoUsuarioDTO) {
        return ResponseEntity.ok().body(usuarioService.atualizarUsuario(atualizacaoUsuarioDTO, token));
    }

    @GetMapping("/verificaremail")
    public ResponseEntity<String> verificarEmailUsuario(@RequestParam("token") String token) {
        return ResponseEntity.ok().body(usuarioService.verificarEmailUsuario(token));
    }

    @PostMapping("/esquecisenha")
    public ResponseEntity<String> usuarioEsqueceuSenha(@RequestParam String email) {
        return ResponseEntity.ok().body(usuarioService.UsuarioEsqueceuSenha(email));
    }

    @PostMapping("/alterarsenha")
    public ResponseEntity<String> alterarSenhaUsuario(@RequestParam String token, @RequestParam String novaSenha) {
        return ResponseEntity.ok().body(usuarioService.alterarSenhaUsuario(token, novaSenha));
    }
}
