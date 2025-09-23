package br.com.rafaelblomer.infrastructure.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String nome;
    @NotBlank
    @Email
    @Column(unique = true)
    private String email;
    @NotBlank
    @Size(min = 11, max = 13)
    @Column(unique = true)
    private String telefone;
    @NotBlank
    @Size(min = 10)
    private String senha;
    @NotBlank
    @Size(min = 14, max = 14)
    @Column(unique = true)
    private String cnpj;
    private Boolean ativo;

    public Usuario(String nome, String email, String cnpj, String telefone, String senha) {
        this.nome = nome;
        this.email = email;
        this.cnpj = cnpj;
        this.telefone = telefone;
        this.senha = senha;
        this.ativo = false;
    }

    public Usuario() {
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public @NotBlank @Size(min = 14, max = 14) String getCnpj() {
        return cnpj;
    }

    public void setCnpj(@NotBlank @Size(min = 14, max = 14) String cnpj) {
        this.cnpj = cnpj;
    }

    public @NotBlank @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank @Email String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank String getNome() {
        return nome;
    }

    public void setNome(@NotBlank String nome) {
        this.nome = nome;
    }

    public @NotBlank String getSenha() {
        return senha;
    }

    public void setSenha(@NotBlank String senha) {
        this.senha = senha;
    }

    public @NotBlank @Size(min = 11, max = 13) String getTelefone() {
        return telefone;
    }

    public void setTelefone(@NotBlank @Size(min = 11, max = 13) String telefone) {
        this.telefone = telefone;
    }
}
