package br.com.rafaelblomer.infrastructure.security;

import java.util.Collection;
import java.util.List;

import br.com.rafaelblomer.infrastructure.entities.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserAuthenticated implements UserDetails {
    private static final long serialVersionUID = 1L;
    private final Usuario user;

    public UserAuthenticated(Usuario user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "read");
    }

    @Override
    public String getPassword() {
        return user.getSenha();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
}
