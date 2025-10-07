package br.com.rafaelblomer.API_gateway.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        // Endpoints públicos
                        .pathMatchers(HttpMethod.POST, "/user-service/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/user-service/cadastro").permitAll()
                        .pathMatchers(HttpMethod.GET, "/user-service/verificaremail").permitAll()
                        .pathMatchers(HttpMethod.POST, "/user-service/esquecisenha").permitAll()
                        .pathMatchers(HttpMethod.POST, "/user-service/alterarsenha").permitAll()
                        // Qualquer outro endpoint requer autenticação
                        .anyExchange().authenticated()
                )
                // Adiciona o filtro JWT antes da autenticação padrão
                .addFilterAt(jwtRequestFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
