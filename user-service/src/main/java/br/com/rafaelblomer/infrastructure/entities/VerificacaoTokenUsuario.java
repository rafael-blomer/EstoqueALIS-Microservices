package br.com.rafaelblomer.infrastructure.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "TB_VERIFICACAOTOKEN")
public class VerificacaoTokenUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private Usuario user;
    private LocalDateTime expiryDate;

    public static VerificacaoTokenUsuario criarComExpiracao(String token, Usuario user) {
        return VerificacaoTokenUsuario.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(5))
                .build();
    }
}
